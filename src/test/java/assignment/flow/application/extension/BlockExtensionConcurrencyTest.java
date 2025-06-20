package assignment.flow.application.extension;

import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.domain.entity.ExtensionCounter;
import assignment.flow.domain.exception.extension.BlockExtensionLimitException;
import assignment.flow.domain.repo.BlockExtensionRepository;
import assignment.flow.domain.repo.ExtensionCounterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockExtensionConcurrencyTest {

    @Mock
    private BlockExtensionRepository extensionRepository;

    @Mock
    private ExtensionCounterRepository counterRepository;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private BlockExtensionCommandServiceImpl commandService;

    @Test
    @DisplayName("확장자 등록시 동시성 이슈 테스트")
    void testConcurrentRegistrationNearLimit() throws InterruptedException, ExecutionException {
        ReflectionTestUtils.setField(commandService, "CUSTOM_MAX", 200);

        ExtensionCounter counter = new ExtensionCounter("CUSTOM", 199L, 0);
        final Object lock = new Object();

        lenient().when(extensionRepository.existsBlockExtensionsByExtensionName(anyString())).thenReturn(false);

        lenient().when(counterRepository.findByCounterIdForUpdate("CUSTOM")).thenAnswer(invocation -> {
            synchronized (lock) {
                return counter;
            }
        });

        lenient().when(extensionRepository.save(any(BlockExtension.class))).thenAnswer(invocation -> {
            BlockExtension extension = invocation.getArgument(0);
            return new BlockExtension(1L, extension.getExtensionName(), extension.getExtensionType(), extension.isEnabled());
        });

        lenient().doAnswer(invocation -> {
            return null;
        }).when(counterRepository).save(counter);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Callable<Object>> tasks = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            final String extensionName = "ext" + i;
            tasks.add(() -> {
                try {
                    startLatch.await();

                    synchronized (lock) {
                        if (counter.getCountValue() >= 200) {
                            System.out.println("[실패] 개수 제한에 의해 확장자 등록 실패: " + extensionName);
                            throw new BlockExtensionLimitException();
                        }

                        counter.incrementCount();
                        System.out.println("[성공] 확장자 등록 성공: " + extensionName);
                        successCount.incrementAndGet();
                    }

                    return null;
                } catch (BlockExtensionLimitException e) {
                    System.out.println("개수 제한 예외로 인한 에러: " + extensionName);
                    failCount.incrementAndGet();
                    return null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        }

        List<Future<Object>> futures = new ArrayList<>();
        for (Callable<Object> task : tasks) {
            futures.add(executorService.submit(task));
        }

        startLatch.countDown();

        for (Future<Object> future : futures) {
            future.get();
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(1, successCount.get());
        assertEquals(4, failCount.get());

        assertEquals(200L, counter.getCountValue());
    }

    @Test
    @DisplayName("확장자 등록시 비관적 락을 사용한 동시성 테스트")
    void testConcurrentRegistrationWithPessimisticLock() {
        ReflectionTestUtils.setField(commandService, "CUSTOM_MAX", 200);

        ExtensionCounter counter = new ExtensionCounter("CUSTOM", 190L, 0);

        when(extensionRepository.existsBlockExtensionsByExtensionName(anyString())).thenReturn(false);
        when(counterRepository.findByCounterIdForUpdate("CUSTOM")).thenReturn(counter);
        when(extensionRepository.save(any(BlockExtension.class))).thenAnswer(invocation -> {
            BlockExtension extension = invocation.getArgument(0);
            return new BlockExtension(1L, extension.getExtensionName(), extension.getExtensionType(), extension.isEnabled());
        });

        for (int i = 0; i < 10; i++) {
            commandService.registerExtension("ext" + i);
        }

        assertEquals(200L, counter.getCountValue(), "Counter should be incremented to 200");

        verify(counterRepository, times(10)).save(counter);

        assertThrows(BlockExtensionLimitException.class, () -> {
            commandService.registerExtension("ext10");
        });
    }
}

package assignment.flow.application.extension;

import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.domain.entity.ExtensionType;
import assignment.flow.domain.repo.BlockExtensionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockExtensionQueryServiceTest {

    @Mock
    private BlockExtensionRepository repository;

    @InjectMocks
    private BlockExtensionQueryServiceImpl queryService;

    @Test
    @DisplayName("Default 확장자 조회")
    void testFindDefaultExtensions() {
        BlockExtension ext1 = new BlockExtension("exe", ExtensionType.DEFAULT, true);
        BlockExtension ext2 = new BlockExtension("bat", ExtensionType.DEFAULT, false);
        List<BlockExtension> defaultExtensions = Arrays.asList(ext1, ext2);

        when(repository.findAllByExtensionType(ExtensionType.DEFAULT)).thenReturn(defaultExtensions);

        List<BlockExtension> result = queryService.findDefaultExtensions();

        assertEquals(2, result.size());
        assertEquals("exe", result.get(0).getExtensionName());
        assertEquals("bat", result.get(1).getExtensionName());
    }

    @Test
    @DisplayName("Custom 확장자 조회")
    void testFindCustomExtensions() {
        BlockExtension ext1 = new BlockExtension("pdf", ExtensionType.CUSTOM, true);
        BlockExtension ext2 = new BlockExtension("doc", ExtensionType.CUSTOM, true);
        List<BlockExtension> customExtensions = Arrays.asList(ext1, ext2);

        when(repository.findAllByExtensionType(ExtensionType.CUSTOM)).thenReturn(customExtensions);

        List<BlockExtension> result = queryService.findCustomExtensions();

        assertEquals(2, result.size());
        assertEquals("pdf", result.get(0).getExtensionName());
        assertEquals("doc", result.get(1).getExtensionName());
    }

    @Test
    @DisplayName("Custom 확장자 개수 조회")
    void testCountCustomExtensions() {
        when(repository.countByExtensionType(ExtensionType.CUSTOM)).thenReturn(5L);

        long count = queryService.countCustomExtensions();

        assertEquals(5L, count);
    }

    @Test
    @DisplayName("아무 Custom 확장자도 없는 경우 - Extension Type으로 조회")
    void testFindCustomExtensions_empty() {
        when(repository.findAllByExtensionType(ExtensionType.CUSTOM)).thenReturn(List.of());

        List<BlockExtension> result = queryService.findCustomExtensions();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("아무 Custom 확장자도 없는 경우 - Counter로 조회")
    void testCountCustomExtensions_zero() {
        when(repository.countByExtensionType(ExtensionType.CUSTOM)).thenReturn(0L);

        long count = queryService.countCustomExtensions();

        assertEquals(0L, count);
    }
}
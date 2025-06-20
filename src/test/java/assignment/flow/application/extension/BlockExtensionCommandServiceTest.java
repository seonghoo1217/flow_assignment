package assignment.flow.application.extension;

import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.domain.entity.ExtensionCounter;
import assignment.flow.domain.entity.ExtensionType;
import assignment.flow.domain.exception.extension.BlockExtensionExistsException;
import assignment.flow.domain.exception.extension.BlockExtensionLimitException;
import assignment.flow.domain.exception.extension.BlockExtensionNotSubject;
import assignment.flow.domain.repo.BlockExtensionRepository;
import assignment.flow.domain.repo.ExtensionCounterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockExtensionCommandServiceTest {

    @Mock
    private BlockExtensionRepository extensionRepository;

    @Mock
    private ExtensionCounterRepository counterRepository;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private BlockExtensionCommandServiceImpl commandService;

    private ExtensionCounter counter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(commandService, "CUSTOM_MAX", 200);

        counter = new ExtensionCounter("CUSTOM", 0L, 0);
        lenient().when(counterRepository.findByCounterIdForUpdate("CUSTOM")).thenReturn(counter);
    }

    @DisplayName("확장자 이름 빈칸 유효성 검증 - null, 빈 문자열, 공백 문자열")
    @ParameterizedTest(name = "입력값 \"{0}\" 일 때 IllegalArgumentException")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void testRegisterExtension_emptyName(String extensionName) {
        /*IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.registerExtension("")
        );
        assertEquals("확장자를 입력해주세요.", exception.getMessage());

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.registerExtension(null)
        );
        assertEquals("확장자를 입력해주세요.", exception.getMessage());

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.registerExtension("   ")
        );
        assertEquals("확장자를 입력해주세요.", exception.getMessage());*/
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.registerExtension(extensionName)
        );
        assertEquals("확장자를 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("확장자 최대 길이 유효성 검증 - 20자 초과")
    void testRegisterExtension_nameTooLong() {
        String tooLongName = "a".repeat(21);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.registerExtension(tooLongName)
        );
        assertEquals("확장자는 최대 20자까지 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("확장자 이름 유효성 검증 - 영문자와 숫자만 허용")
    void testRegisterExtension_invalidCharacters() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.registerExtension("abc!")
        );
        assertEquals("확장자는 영문자와 숫자만 허용됩니다.", exception.getMessage());

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> commandService.registerExtension("한글")
        );
        assertEquals("확장자는 영문자와 숫자만 허용됩니다.", exception.getMessage());
    }

    @Test
    @DisplayName("확장자 이미 존재하는지 유효성 검증")
    void testRegisterExtension_alreadyExists() {
        when(extensionRepository.existsBlockExtensionsByExtensionName("pdf")).thenReturn(true);

        BlockExtensionExistsException exception = assertThrows(
                BlockExtensionExistsException.class,
                () -> commandService.registerExtension("pdf")
        );
    }

    @Test
    @DisplayName("확장자 등록 시 최대 개수 제한 유효성 검증 - 이미 최대 개수 도달")
    void testRegisterExtension_limitReached() {
        when(extensionRepository.existsBlockExtensionsByExtensionName(anyString())).thenReturn(false);

        counter = new ExtensionCounter("CUSTOM", 200L, 0);
        when(counterRepository.findByCounterIdForUpdate("CUSTOM")).thenReturn(counter);

        BlockExtensionLimitException exception = assertThrows(
                BlockExtensionLimitException.class,
                () -> commandService.registerExtension("pdf")
        );
    }

    @Test
    @DisplayName("확장자 등록 성공 ")
    void testRegisterExtension_success() {
        when(extensionRepository.existsBlockExtensionsByExtensionName("pdf")).thenReturn(false);

        BlockExtension savedExtension = new BlockExtension(1L, "pdf", ExtensionType.CUSTOM, true);
        when(extensionRepository.save(any(BlockExtension.class))).thenReturn(savedExtension);

        Long id = commandService.registerExtension("pdf");

        assertEquals(1L, id);
        assertEquals(1L, counter.getCountValue());
        verify(counterRepository).save(counter);
    }

    @Test
    @DisplayName("존재 하지 않는 확장자 삭제 유효성 검증")
    void testDeleteExtension_notFound() {
        when(extensionRepository.findByExtensionName("pdf")).thenReturn(Optional.empty());

        assertThrows(
                BlockExtensionNotSubject.class,
                () -> commandService.deleteExtension("pdf")
        );
    }

    @Test
    @DisplayName("기본 확장자 삭제 유효성 검증")
    void testDeleteExtension_defaultExtension() {
        BlockExtension defaultExtension = new BlockExtension("exe", ExtensionType.DEFAULT, true);
        when(extensionRepository.findByExtensionName("exe")).thenReturn(Optional.of(defaultExtension));

        assertThrows(
                IllegalArgumentException.class,
                () -> commandService.deleteExtension("exe")
        );
    }

    @Test
    @DisplayName("확장자 삭제 성공")
    void testDeleteExtension_success() {
        BlockExtension customExtension = new BlockExtension("pdf", ExtensionType.CUSTOM, true);
        when(extensionRepository.findByExtensionName("pdf")).thenReturn(Optional.of(customExtension));

        commandService.deleteExtension("pdf");

        verify(extensionRepository).delete(customExtension);
    }

    @Test
    @DisplayName("존재 하지 않는 확장자 토글 유효성 검증")
    void testToggleExtension_notFound() {
        when(extensionRepository.findByExtensionName("pdf")).thenReturn(Optional.empty());

        assertThrows(
                BlockExtensionNotSubject.class,
                () -> commandService.toggleExtension("pdf")
        );
    }

    @Test
    @DisplayName("확장자 토글 성공")
    void testToggleExtension_success() {
        BlockExtension extension = new BlockExtension("pdf", ExtensionType.CUSTOM, false);
        when(extensionRepository.findByExtensionName("pdf")).thenReturn(Optional.of(extension));

        commandService.toggleExtension("pdf");

        assertTrue(extension.isEnabled());
        verify(extensionRepository).save(extension);
    }
}

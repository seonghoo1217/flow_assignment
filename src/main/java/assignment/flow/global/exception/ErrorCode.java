package assignment.flow.global.exception;

import assignment.flow.domain.exception.extension.BlockExtensionExistsException;
import assignment.flow.domain.exception.extension.BlockExtensionLimitException;
import assignment.flow.domain.exception.upload.UploadEmptyException;
import assignment.flow.domain.exception.upload.UploadFailureException;
import lombok.Getter;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

@Getter
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버에 오류가 생겼습니다. 관리자에게 문의하세요.",
            Set.of()),
    INVALID_INPUT_VALUE(
            HttpStatus.BAD_REQUEST,
            "입력 값이 올바르지 않습니다.",
            Set.of(MethodArgumentNotValidException.class, ConstraintViolationException.class)),
    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "지원하지 않는 HTTP 메서드입니다.",
            Set.of(HttpRequestMethodNotSupportedException.class)),


    //Extension
    EXISTS_BLOCK_EXTENSION(
            HttpStatus.BAD_REQUEST,
            "이미 존재하는 확장자 차단입니다.",
            Set.of(BlockExtensionExistsException.class)),
    LIMIT_EXTENSION_COUNT(
            HttpStatus.BAD_REQUEST,
            "확장자 차단은 최대 200개까지 가능합니다.",
            Set.of(BlockExtensionLimitException.class)),

    //upload
    EMPTY_UPLOAD(
            HttpStatus.BAD_REQUEST,
            "업로드할 파일이 없습니다.",
            Set.of(UploadEmptyException.class)),
    UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "파일 업로드에 실패했습니다.",
            Set.of(UploadFailureException.class));

    private final HttpStatusCode status;
    private final String code;
    private final String message;
    private final Set<Class<? extends Exception>> exceptions;

    ErrorCode(
            HttpStatusCode status,
            String code,
            String message,
            Set<Class<? extends Exception>> exceptions) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.exceptions = exceptions;
    }

    ErrorCode(HttpStatusCode status, String message, Set<Class<? extends Exception>> exceptions) {
        this.status = status;
        this.code = String.valueOf(status.value());
        this.message = message;
        this.exceptions = exceptions;
    }
}

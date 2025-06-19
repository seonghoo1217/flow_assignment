package assignment.flow.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExtensionRegisterReqDto(
        @NotBlank(message = "확장자가 없습니다.")
        @Size(max = 20, message = "확장자는 최대 20자까지 입력 가능합니다")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "확장자는 영숫자만 허용됩니다")
        String extensionName) {

}

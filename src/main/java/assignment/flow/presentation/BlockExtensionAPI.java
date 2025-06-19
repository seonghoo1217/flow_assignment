package assignment.flow.presentation;

import assignment.flow.application.BlockExtensionQueryService;
import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/block-extensions")
@RequiredArgsConstructor
public class BlockExtensionAPI {

    private final BlockExtensionQueryService blockExtensionQueryService;

    @GetMapping("/defaults")
    public ApiResponse<?> getDefaultExtensions() {
        List<BlockExtension> defaultExtensions = blockExtensionQueryService.findDefaultExtensions();

        return ApiResponse.success(defaultExtensions);
    }
}

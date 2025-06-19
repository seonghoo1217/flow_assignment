package assignment.flow.presentation;

import assignment.flow.application.BlockExtensionCommandService;
import assignment.flow.application.BlockExtensionQueryService;
import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.global.dto.ApiResponse;
import assignment.flow.presentation.dto.req.ExtensionRegisterReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/block-extensions")
@RequiredArgsConstructor
public class BlockExtensionAPI {

    private final BlockExtensionQueryService queryService;
    private final BlockExtensionCommandService commandService;

    @GetMapping("/defaults")
    public ApiResponse<?> getDefaultExtensions() {
        List<BlockExtension> defaultExtensions = queryService.findDefaultExtensions();

        return ApiResponse.success(defaultExtensions);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> registerExtension(@RequestBody ExtensionRegisterReqDto req) {
        Long id = commandService.registerExtension(req.extensionName());
        return ApiResponse.success(id);
    }
}

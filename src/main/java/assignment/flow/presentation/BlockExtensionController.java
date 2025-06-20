package assignment.flow.presentation;

import assignment.flow.application.extension.BlockExtensionQueryService;
import assignment.flow.domain.entity.BlockExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping()
@RequiredArgsConstructor
@Log4j2
public class BlockExtensionController {

    private final BlockExtensionQueryService queryService;

    @GetMapping("block-extensions")
    public String blockExtensionsPage(Model model) {
        List<BlockExtension> defaultExtensions = queryService.findDefaultExtensions();
        List<BlockExtension> customExtensions = queryService.findCustomExtensions();
        long customCount = queryService.countCustomExtensions();

        log.info("Default Extensions: {}", defaultExtensions);

        model.addAttribute("defaultExtensions", defaultExtensions);
        model.addAttribute("customExtensions", customExtensions);
        model.addAttribute("customCount", customCount);

        return "block-extensions";
    }
}
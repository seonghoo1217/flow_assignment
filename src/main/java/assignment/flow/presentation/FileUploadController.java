package assignment.flow.presentation;

import assignment.flow.application.upload.FileUploadCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/file-upload")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadCommandService uploadCommandService;

    @GetMapping
    public String showUploadForm(Model model) {
        return "file-upload";
    }

    @PostMapping
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/file-upload";
        }

        try {
            String savedFilename = uploadCommandService.upload(file);

            redirectAttributes.addFlashAttribute("message",
                    "업로드 성공 " + file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            log.info("업로드 성공: {} → {}", file.getOriginalFilename(), savedFilename);

        } catch (IOException e) {
            log.error("업로드 실패", e);
            redirectAttributes.addFlashAttribute("message",
                    "업로드 실패 " + file.getOriginalFilename() + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-fail");
        }

        return "redirect:/file-upload";
    }
}
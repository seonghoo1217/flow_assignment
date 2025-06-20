package assignment.flow.presentation;

import assignment.flow.application.upload.FileUploadCommandService;
import assignment.flow.domain.exception.upload.UploadEmptyException;
import assignment.flow.domain.exception.upload.UploadFailureException;
import assignment.flow.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file-upload")
@RequiredArgsConstructor
@Log4j2
public class FileUploadAPI {

    private final FileUploadCommandService uploadCommandService;

    @PostMapping
    public ApiResponse<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new UploadEmptyException();
        }

        try {
            String savedFilename = uploadCommandService.uploadAPI(file);
            log.info("업로드 파일 이름: {}", savedFilename);
            return ApiResponse.success(savedFilename);
        } catch (IOException e) {
            throw new UploadFailureException();
        }
    }
}

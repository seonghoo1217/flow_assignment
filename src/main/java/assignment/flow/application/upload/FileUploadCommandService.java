package assignment.flow.application.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadCommandService {
    String upload(MultipartFile file) throws IOException;

    String uploadAPI(MultipartFile file) throws IOException;
}

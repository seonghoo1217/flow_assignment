package assignment.flow.application.upload;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadCommandServiceImpl implements FileUploadCommandService {
    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadDir);
        if (!Files.exists(rootLocation)) {
            try {
                Files.createDirectories(rootLocation);
            } catch (IOException e) {
                throw new RuntimeException("디렉터리 생성 안됌", e);
            }
        }
    }

    @Override
    public String upload(MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty() || original.contains("..")) {
            throw new IOException("잘못된 파일 양식: " + original);
        }

        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) {
            ext = original.substring(idx);
        }

        String filename = UUID.randomUUID() + ext;
        Path target = rootLocation.resolve(filename);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("파일 저장 실패 " + original, e);
        }

        return filename;
    }

    @Override
    public String uploadAPI(MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty() || original.contains("..")) {
            throw new IOException("잘못된 파일 양식: " + original);
        }

        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) {
            ext = original.substring(idx);
        }

        String filename = UUID.randomUUID() + ext;
        Path target = rootLocation.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }
}

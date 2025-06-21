package assignment.flow.global.filter;

import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.domain.repo.BlockExtensionRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileExtensionBlockFilter implements Filter {

    private final BlockExtensionRepository extensionRepository;

    private final long MAX_FILE_UPLOAD_SIZE = 500L * 1024L; // 500 KB


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
            try {
                List<BlockExtension> blockedExtensions = extensionRepository.findAllByEnabled(true);
                Set<String> blockedExtensionNames = blockedExtensions.stream()
                        .map(ext -> ext.getExtensionName().toLowerCase())
                        .collect(Collectors.toSet());

                log.debug("Block: {}", blockedExtensionNames);

                if (request instanceof MultipartHttpServletRequest) {
                    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

                    if (multipartRequest.getFileMap().isEmpty()) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "NO_FILE_UPLOADED");
                        return;
                    }

                    boolean invalid = multipartRequest.getFileMap().values().stream()
                            .anyMatch(file -> file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty());

                    if (invalid) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "FILE_NAME_EMPTY");
                        return;
                    }

                    boolean tooLarge = multipartRequest.getFileMap().values().stream()
                            .anyMatch(file -> file.getSize() > MAX_FILE_UPLOAD_SIZE);
                    if (tooLarge) {
                        response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                                "FILE_TOO_LARGE");
                        return;
                    }

                    boolean hasBlockedFile = multipartRequest.getFileMap().values().stream()
                            .anyMatch(file -> {
                                if (file.isEmpty()) {
                                    return false;
                                }

                                String filename = file.getOriginalFilename();
                                if (filename == null || !filename.contains(".")) {
                                    return false;
                                }

                                String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                                boolean isBlocked = blockedExtensionNames.contains(extension);

                                if (isBlocked) {
                                    log.warn("막힌 파일 {} (확장자: {})", filename, extension);
                                }

                                return isBlocked;
                            });

                    if (hasBlockedFile) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "차단된 확장자");
                        return;
                    }
                } else {
                    for (Part part : request.getParts()) {
                        if (part.getSize() == 0) {
                            log.warn("빈 파일 업로드: {}", part.getSubmittedFileName());
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "FILE_NAME_EMPTY");
                            return;
                        }


                        if (part.getSize() > MAX_FILE_UPLOAD_SIZE) {
                            log.warn("크기 초과 파일: {} ({} bytes)",
                                    part.getSubmittedFileName(), part.getSize());
                            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                                    "FILE_TOO_LARGE");
                            return;
                        }

                        String filename = part.getSubmittedFileName();
                        if (filename != null && filename.contains(".")) {
                            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                            if (blockedExtensionNames.contains(extension)) {
                                log.warn("막힌 파일: {} (확장자: {})", filename, extension);
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "File type not allowed");
                                return;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("필터 에러", e);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}

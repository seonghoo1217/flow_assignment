package assignment.flow.infra.config;

import assignment.flow.global.filter.FileExtensionBlockFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final FileExtensionBlockFilter fileExtensionBlockFilter;

    @Bean
    public FilterRegistrationBean<FileExtensionBlockFilter> fileExtensionBlockFilterRegistration() {
        FilterRegistrationBean<FileExtensionBlockFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(fileExtensionBlockFilter);
        registrationBean.addUrlPatterns("/file-upload", "/api/file-upload");
        registrationBean.setName("fileExtensionBlockFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
package assignment.flow.infra.intialize;

import assignment.flow.application.extension.BlockExtensionCommandService;
import assignment.flow.domain.entity.ExtensionCounter;
import assignment.flow.domain.repo.ExtensionCounterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class FixedExtensionLoader implements ApplicationRunner {

    private final BlockExtensionCommandService blockExtensionCommandService;
    private final ExtensionProperties extensionProperties;
    private final ExtensionCounterRepository extensionCounterRepository;

    private final String COUNTER_ID = "CUSTOM";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(">>> default extensions: {}", extensionProperties.getDefaults());

        List<String> defaults = extensionProperties.getDefaults();
        blockExtensionCommandService.initDefaultExtensions(defaults);

        if (!extensionCounterRepository.existsExtensionCounterByCounterId(COUNTER_ID)) {
            extensionCounterRepository.save(new ExtensionCounter(COUNTER_ID, 0L, 0));
        }
    }
}

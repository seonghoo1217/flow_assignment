package assignment.flow.application.extension;

import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.domain.entity.ExtensionCounter;
import assignment.flow.domain.entity.ExtensionType;
import assignment.flow.domain.exception.extension.BlockExtensionExistsException;
import assignment.flow.domain.exception.extension.BlockExtensionLimitException;
import assignment.flow.domain.exception.extension.BlockExtensionNotSubject;
import assignment.flow.domain.repo.BlockExtensionRepository;
import assignment.flow.domain.repo.ExtensionCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockExtensionCommandServiceImpl implements BlockExtensionCommandService {

    private final BlockExtensionRepository extensionRepository;
    private final ExtensionCounterRepository counterRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Value("${app.extensions.custom.max}")
    private Integer CUSTOM_MAX;

    private static final String BATCH_INSERT_SQL =
            "INSERT INTO block_extensions " +
                    "  (extension_name, extension_type, enabled, CREATED_DATE, UPDATE_DATE) " +
                    "VALUES " +
                    "  (:extensionName, :extensionType, :enabled, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    @Override
    public void initDefaultExtensions(List<String> defaultExtensions) {
        List<String> names = defaultExtensions.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        Set<String> existing = extensionRepository.findAllByExtensionNameIn(names).stream()
                .map(BlockExtension::getExtensionName)
                .collect(Collectors.toSet());

        List<SqlParameterSource> batchParams = names.stream()
                .filter(name -> !existing.contains(name))
                .map(name -> new MapSqlParameterSource()
                        .addValue("extensionName", name)
                        .addValue("extensionType", ExtensionType.DEFAULT.name())
                        .addValue("enabled", false))
                .collect(Collectors.toList());

        if (!batchParams.isEmpty()) {
            jdbc.batchUpdate(BATCH_INSERT_SQL,
                    batchParams.toArray(new SqlParameterSource[0]));
        }
    }

    @Override
    public Long registerExtension(String extensionName) {
        String verifyName = normalVerify(extensionName);

        if (extensionRepository.existsBlockExtensionsByExtensionName(verifyName)) {
            throw new BlockExtensionExistsException();
        }
        ExtensionCounter counter =
                counterRepository.findByCounterIdForUpdate("CUSTOM");

        if (counter.getCountValue() >= CUSTOM_MAX) {
            throw new BlockExtensionLimitException();
        }

        BlockExtension blockExtension = new BlockExtension(verifyName, ExtensionType.CUSTOM, true);

        BlockExtension save = extensionRepository.save(blockExtension);

        counter.incrementCount();
        counterRepository.save(counter);

        return save.getId();
    }

    private String normalVerify(String extensionName) {
        if (!StringUtils.hasText(extensionName)) {
            throw new IllegalArgumentException("확장자를 입력해주세요.");
        }
        String trimmed = extensionName.trim().toLowerCase();
        if (trimmed.length() > 20) {
            throw new IllegalArgumentException("확장자는 최대 20자까지 가능합니다.");
        }
        if (!trimmed.matches("^[a-z0-9]+$")) {
            throw new IllegalArgumentException("확장자는 영문자와 숫자만 허용됩니다.");
        }
        return trimmed;
    }

    @Override
    public void deleteExtension(String extensionName) {
        BlockExtension blockExtension = extensionRepository.findByExtensionName(extensionName)
                .orElseThrow(BlockExtensionNotSubject::new);

        if (blockExtension.getExtensionType() == ExtensionType.DEFAULT) {
            throw new IllegalArgumentException();
        }
        ExtensionCounter counter =
                counterRepository.findByCounterIdForUpdate("CUSTOM");

        counter.decrementCount();

        extensionRepository.delete(blockExtension);
    }

    @Override
    public void toggleExtension(String extensionName) {
        BlockExtension blockExtension = extensionRepository.findByExtensionName(extensionName)
                .orElseThrow(BlockExtensionNotSubject::new);

        blockExtension.toggle();
        extensionRepository.save(blockExtension);
    }
}

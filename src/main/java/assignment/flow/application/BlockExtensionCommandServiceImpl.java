package assignment.flow.application;

import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.domain.entity.ExtensionType;
import assignment.flow.domain.exception.BlockExtensionExistsException;
import assignment.flow.domain.exception.BlockExtensionLimitException;
import assignment.flow.domain.exception.BlockExtensionNotSubject;
import assignment.flow.domain.repo.BlockExtensionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockExtensionCommandServiceImpl implements BlockExtensionCommandService {

    private final BlockExtensionRepository repository;
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

        Set<String> existing = repository.findAllByExtensionNameIn(names).stream()
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
    public Long registerExtension(String name) {

        if (repository.existsBlockExtensionsByExtensionName(name)) {
            throw new BlockExtensionExistsException();
        }

        long customCount = repository.countByExtensionType(ExtensionType.CUSTOM);
        if (customCount >= CUSTOM_MAX) {
            throw new BlockExtensionLimitException();
        }


        BlockExtension blockExtension = new BlockExtension(name, ExtensionType.CUSTOM, true);

        BlockExtension save = repository.save(blockExtension);

        return save.getId();
    }

    @Override
    public void deleteExtension(String extensionName) {
        BlockExtension blockExtension = repository.findByExtensionName(extensionName)
                .orElseThrow(BlockExtensionNotSubject::new);

        repository.delete(blockExtension);
    }
}

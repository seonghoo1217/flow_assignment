package assignment.flow.application;

import assignment.flow.domain.entity.BlockExtension;
import assignment.flow.domain.entity.ExtensionType;
import assignment.flow.domain.repo.BlockExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockExtensionQueryServiceImpl implements BlockExtensionQueryService {

    private final BlockExtensionRepository repository;

    @Override
    public List<BlockExtension> findDefaultExtensions() {
        return repository.findAllByExtensionType(ExtensionType.DEFAULT);
    }

    @Override
    public List<BlockExtension> findCustomExtensions() {
        return repository.findAllByExtensionType(ExtensionType.CUSTOM);
    }

    @Override
    public long countCustomExtensions() {
        return repository.countByExtensionType(ExtensionType.CUSTOM);
    }
}

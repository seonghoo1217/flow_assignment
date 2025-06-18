package assignment.flow.domain.repo;

import assignment.flow.domain.entity.BlockExtension;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BlockExtensionRepository extends Repository<BlockExtension, Long> {
    List<BlockExtension> findAllByExtensionNameIn(Collection<String> names);

    Optional<BlockExtension> findByExtensionName(String name);
}

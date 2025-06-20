package assignment.flow.application.extension;

import assignment.flow.domain.entity.BlockExtension;

import java.util.List;

public interface BlockExtensionQueryService {

    List<BlockExtension> findDefaultExtensions();

    List<BlockExtension> findCustomExtensions();

    long countCustomExtensions();
}

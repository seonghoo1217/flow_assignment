package assignment.flow.application;

import java.util.List;

public interface BlockExtensionCommandService {

    void initDefaultExtensions(List<String> defaultExtensions);

    void registerExtension(String extensionName);
}

package assignment.flow.application.extension;

import java.util.List;

public interface BlockExtensionCommandService {

    void initDefaultExtensions(List<String> defaultExtensions);

    Long registerExtension(String extensionName);

    void deleteExtension(String extensionName);

    void toggleExtension(String extensionName);
}

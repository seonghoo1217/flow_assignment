package assignment.flow.infra.intialize;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.extensions")
public class ExtensionProperties {
    private List<String> defaults = new ArrayList<>();

    public List<String> getDefaults() {
        return defaults;
    }

    public void setDefaults(List<String> defaults) {
        this.defaults = defaults;
    }
}

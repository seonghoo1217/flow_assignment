package assignment.flow.infra.intialize;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.extensions")
@Getter
public class ExtensionProperties {
    private List<String> defaults = new ArrayList<>();

    public void setDefaults(List<String> defaults) {
        this.defaults = defaults;
    }
}

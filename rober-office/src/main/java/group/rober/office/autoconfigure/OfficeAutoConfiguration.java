package group.rober.office.autoconfigure;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@EnableConfigurationProperties(OfficeProperties.class)
@ComponentScan(basePackages = "group.rober.office")
public class OfficeAutoConfiguration {
    protected OfficeProperties properties;

    public OfficeAutoConfiguration(OfficeProperties properties) {
        this.properties = properties;
    }
}

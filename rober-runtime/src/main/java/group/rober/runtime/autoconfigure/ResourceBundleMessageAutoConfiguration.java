package group.rober.runtime.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration("messageSource")
@ConfigurationProperties(prefix = "spring.messages")
public class ResourceBundleMessageAutoConfiguration extends ReloadableResourceBundleMessageSource {


    private static final String DEPENDENCE_BASE_NAME = "i18n/messages";

    private static final String PROPERTIES_SUFFIX = ".properties";

    private static final String CLASSPATH_SINGLE_URL_PREFIX = "classpath:";

    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private final ConcurrentMap<String, String> cachedJarProperties = new ConcurrentHashMap();


    @Override
    protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
        if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
            return refreshClassPathProperties(filename, propHolder);
        } else if (!filename.startsWith(CLASSPATH_SINGLE_URL_PREFIX)) {
            return super.refreshProperties(CLASSPATH_SINGLE_URL_PREFIX + filename, propHolder);
        } else {
            return super.refreshProperties(filename, propHolder);
        }
    }

    private PropertiesHolder refreshClassPathProperties(String filename, PropertiesHolder propHolder) {
        Properties properties = new Properties();
        long lastModified = -1;
        try {
            Resource[] resources = resolver.getResources(filename + PROPERTIES_SUFFIX);
            for (Resource resource : resources) {
                String sourcePath = resource.getURI().toString().replace(PROPERTIES_SUFFIX, "");
                PropertiesHolder holder = super.refreshProperties(sourcePath, propHolder);
                properties.putAll(holder.getProperties());
                if (lastModified < resource.lastModified())
                    lastModified = resource.lastModified();
            }
        } catch (IOException ignored) {
        }
        return new PropertiesHolder(properties, lastModified);
    }

    public HashMap<String, String> getProperties() {
        HashMap<String, String> propertiesMap = new HashMap<>();
        if (cachedJarProperties.size() == 0) {
            Locale locale = LocaleContextHolder.getLocale();
            try {
                //考虑locale，需要读取不同的文件，所以会利用多个名字去获取。从默认到全名倒序去检索，
                // 通过hashmap的key不重复的特性，保证message配置只保留一份。
                //目前存在的一个问题是，此处总量是采取merge的方式，而不是单独一份文件的方式。
                String baseName = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + DEPENDENCE_BASE_NAME;
                List<String> filenames = this.calculateFilenamesForLocale(baseName,locale);
                filenames.add(baseName);
                for(int j = filenames.size() - 1; j >= 0; --j) {
                    Resource[] resources = resolver.getResources(filenames.get(j) + PROPERTIES_SUFFIX);
                    if (resources != null && resources.length > 0) {
                        for(int i = 0; i< resources.length; i++) {
                            InputStreamReader inputStreamReader = new InputStreamReader(resources[i].getInputStream());
                            char[] data = new char[500];
                            inputStreamReader.read(data);
                            String[] keyValue = (new String(data)).trim().split("=");
                            if (keyValue.length < 2)
                                continue;
                            cachedJarProperties.put(keyValue[0], keyValue[1]);
                        }
                    }
                }
            } catch (IOException e) {
                logger.info("读取文件失败！ " + e);
            }
        }
        propertiesMap.putAll(cachedJarProperties);

        PropertiesHolder propertiesHolder =
                this.getMergedProperties(LocaleContextHolder.getLocale());
        Properties properties = propertiesHolder.getProperties();
        Set<Map.Entry<Object,Object>> entrySet = properties.entrySet();
        for(Map.Entry entry:entrySet) {
            propertiesMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return propertiesMap;
    }
}

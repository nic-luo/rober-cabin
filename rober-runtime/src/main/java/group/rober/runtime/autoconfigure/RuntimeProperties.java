package group.rober.runtime.autoconfigure;

import group.rober.runtime.kit.IOKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "group.rober.runtime", ignoreUnknownFields = true)
public class RuntimeProperties {
    /**
     * 整个系统临时文件目录
     */
    private String temporaryDirectory = "/tmp/rober";
//    private String dbDialectType = "mysql";
    private String druidLoginUsername = "rober";
    private String druidLoginPassword = "r0ber";
    private Charset charset = Charset.defaultCharset();
    private boolean productionModel = false;    /*是否生产模式*/
    private String longDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    private String shorDateFormat = "yyyy-MM-dd";
    private List<String> jsonSerializePropertySecrets = new ArrayList<String>();
    private String jsonSerializeSecretMask = "******";
    private List<String> jsonSerializePropertyExcludes = new ArrayList<String>();

    private boolean corsEnable = false;
    private String pathPattern = "/**";
    private String allowedHeaders = "*";
    private String[] allowedMethods = new String[] {"*"};
    private String allowedOrigins = "*";

    private String staticResourceProxyUrl;

    private String scriptEngineName = "groovy";
    private String globalScript = "";

    private Logger logger = LoggerFactory.getLogger(getClass());

    public String getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public void setTemporaryDirectory(String temporaryDirectory) {
        this.temporaryDirectory = temporaryDirectory;
    }

//    public String getDbDialectType() {
//        return dbDialectType;
//    }
//
//    public void setDbDialectType(String dbDialectType) {
//        this.dbDialectType = dbDialectType;
//    }

    public String getDruidLoginUsername() {
        return druidLoginUsername;
    }

    public void setDruidLoginUsername(String druidLoginUsername) {
        this.druidLoginUsername = druidLoginUsername;
    }

    public String getDruidLoginPassword() {
        return druidLoginPassword;
    }

    public void setDruidLoginPassword(String druidLoginPassword) {
        this.druidLoginPassword = druidLoginPassword;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public boolean isProductionModel() {
        return productionModel;
    }

    public void setProductionModel(boolean productionModel) {
        this.productionModel = productionModel;
    }

    public String getLongDateFormat() {
        return longDateFormat;
    }

    public void setLongDateFormat(String longDateFormat) {
        this.longDateFormat = longDateFormat;
    }

    public String getShorDateFormat() {
        return shorDateFormat;
    }

    public void setShorDateFormat(String shorDateFormat) {
        this.shorDateFormat = shorDateFormat;
    }

    public List<String> getJsonSerializePropertySecrets() {
        return jsonSerializePropertySecrets;
    }

    public void setJsonSerializePropertySecrets(List<String> jsonSerializePropertySecrets) {
        this.jsonSerializePropertySecrets = jsonSerializePropertySecrets;
    }

    public String getJsonSerializeSecretMask() {
        return jsonSerializeSecretMask;
    }

    public void setJsonSerializeSecretMask(String jsonSerializeSecretMask) {
        this.jsonSerializeSecretMask = jsonSerializeSecretMask;
    }

    public List<String> getJsonSerializePropertyExcludes() {
        return jsonSerializePropertyExcludes;
    }

    public void setJsonSerializePropertyExcludes(List<String> jsonSerializePropertyExcludes) {
        this.jsonSerializePropertyExcludes = jsonSerializePropertyExcludes;
    }

    public boolean isCorsEnable() {
        return corsEnable;
    }

    public void setCorsEnable(boolean corsEnable) {
        this.corsEnable = corsEnable;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public String[] getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(String[] allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getStaticResourceProxyUrl() {
        return staticResourceProxyUrl;
    }

    public void setStaticResourceProxyUrl(String staticResourceProxyUrl) {
        this.staticResourceProxyUrl = staticResourceProxyUrl;
    }

    public String getScriptEngineName() {
        return scriptEngineName;
    }

    public void setScriptEngineName(String scriptEngineName) {
        this.scriptEngineName = scriptEngineName;
    }

    public String getGlobalScript() {
        return globalScript;
    }

    public void setGlobalScript(String globalScript) {
        this.globalScript = globalScript;
    }

    @PostConstruct
    public void init(){
        String res = "classpath:group/rober/runtime/autoconfigure/GlobalScript.txt";
        URL url = null;
        InputStream inputStream = null;
        try {
            url = ResourceUtils.getURL(res);
            inputStream = url.openStream();
            globalScript = IOKit.toString(inputStream, Charset.defaultCharset());
        } catch (FileNotFoundException e) {
            logger.warn("全局资源脚本不存在",e);
        } catch (IOException e) {
            logger.warn("读取全局资源脚本出错",e);
        } finally {
            IOKit.close(inputStream);
        }
    }
}

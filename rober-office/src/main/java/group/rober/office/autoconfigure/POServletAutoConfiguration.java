package group.rober.office.autoconfigure;

import group.rober.runtime.autoconfigure.RuntimeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(OfficeProperties.class)
public class POServletAutoConfiguration {

    @Autowired
    protected OfficeProperties properties;
    @Autowired
    private RuntimeProperties runtimeProperties;

    public POServletAutoConfiguration(OfficeProperties properties) {
        this.properties = properties;
    }

    public OfficeProperties getProperties() {
        return properties;
    }

    public void setProperties(OfficeProperties properties) {
        this.properties = properties;
    }

    public RuntimeProperties getRuntimeProperties() {
        return runtimeProperties;
    }

    public void setRuntimeProperties(RuntimeProperties runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }

    /**
     * 添加PageOffice的服务器端授权程序Servlet（必须）
     *
     * @return ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean pageOfficeServlet() {
        String licensePath = properties.getPageOffice().getLicensePath();
        String staticResourceUrl = properties.getPageOffice().getStaticResourceUrl();
        com.zhuozhengsoft.pageoffice.poserver.Server poserver = new com.zhuozhengsoft.pageoffice.poserver.Server();
        poserver.setSysPath(licensePath);//设置PageOffice注册成功后,license.lic文件存放的目录

        ServletRegistrationBean srb = new ServletRegistrationBean(poserver);
        srb.addUrlMappings(staticResourceUrl + PageOfficeProperties.DIFF_PATH_SEGMENT + "/poserver.zz");
        srb.addUrlMappings(staticResourceUrl + PageOfficeProperties.DIFF_PATH_SEGMENT + "/posetup.exe");
        srb.addUrlMappings(staticResourceUrl + PageOfficeProperties.DIFF_PATH_SEGMENT + "/pageoffice.js");
        srb.addUrlMappings(staticResourceUrl + PageOfficeProperties.DIFF_PATH_SEGMENT + "/jquery.min.js");
        srb.addUrlMappings(staticResourceUrl + PageOfficeProperties.DIFF_PATH_SEGMENT + "/pobstyle.css");
        srb.addUrlMappings(staticResourceUrl + PageOfficeProperties.DIFF_PATH_SEGMENT + "/sealsetup.exe");
        return srb;
    }
}

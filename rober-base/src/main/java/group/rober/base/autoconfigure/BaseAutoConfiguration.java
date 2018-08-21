package group.rober.base.autoconfigure;

import group.rober.base.BaseConsts;
import group.rober.base.interceptor.GlobalVariableInterceptor;
import group.rober.base.interceptor.WebHolderInterceptor;
import group.rober.base.tags.BriefPlainTextMethodModel;
import group.rober.runtime.autoconfigure.RuntimeProperties;
import group.rober.runtime.holder.ApplicationContextHolder;
import group.rober.runtime.holder.WebHolder;
import group.rober.runtime.kit.HttpKit;
import group.rober.runtime.kit.ListKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.web.RequestParameterMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
//@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
@ComponentScan(basePackages = "group.rober.base", excludeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {CacheConfiguration.class, SpringRedisCacheConfiguration.class})})
@EnableConfigurationProperties(BaseProperties.class)
@Import({SpringRedisCacheConfiguration.class, CacheConfiguration.class})
public class BaseAutoConfiguration extends WebMvcConfigurerAdapter implements ServletContextInitializer,InitializingBean {

    @Autowired
    protected BaseProperties properties;
    @Autowired
    private freemarker.template.Configuration configuration;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(properties.getMultipartMaxFileSize());
        return factory.createMultipartConfig();
    }

    @Bean(BaseConsts.DICT_CACHE)
    public ConcurrentMapCacheFactoryBean getDictCache(){
        ConcurrentMapCacheFactoryBean cache = new ConcurrentMapCacheFactoryBean();
        cache.setName(BaseConsts.DICT_CACHE);
        return cache;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        GlobalVariableInterceptor globalVariableInterceptor = new GlobalVariableInterceptor();
        globalVariableInterceptor.setBaseProperties(properties);

        registry.addInterceptor(new WebHolderInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(globalVariableInterceptor).addPathPatterns("/**");
    }

    public void onStartup(ServletContext servletContext) throws ServletException {
        WebHolder.setServletContext(servletContext);
    }

    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = configurer.getUrlPathHelper();
        if (null == urlPathHelper){
            urlPathHelper = new UrlPathHelper();
        }
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Bean
    public CommandLineRunner customFreemarker(final FreeMarkerViewResolver resolver) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                resolver.setViewClass(RoberFreemarkerView.class);//增加视图
//                Map map = resolver.getAttributesMap();
//                map.put("conver", new MyConver());//添加自定义解析器
            }
        };
    }


    /**
     *
     */
    public static class RoberFreemarkerView extends FreeMarkerView {
        protected freemarker.template.Configuration getConfiguration() {
            freemarker.template.Configuration configuration = ApplicationContextHolder.getBean(freemarker.template.Configuration.class);
            freemarker.template.Configuration configuration1 = super.getConfiguration();
            return configuration;
        }

        protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
            String queryString = request.getQueryString();
            StringBuffer pageLocation = request.getRequestURL();
            StringBuffer pageURL = request.getRequestURL();
            if(StringKit.isNotBlank(queryString)){
                pageLocation.append("?").append(queryString);
            }
            String pagelet = StringKit.nvl(request.getParameter("pagelet"),"0");

            BaseProperties properties = ApplicationContextHolder.getBean(BaseProperties.class);
            RuntimeProperties runtimeProperties = ApplicationContextHolder.getBean(RuntimeProperties.class);
            model.put("ctxPath", request.getServletContext().getContextPath());
            model.put("currentTimeMillis", System.currentTimeMillis());
            model.put("viewPath", properties.getViewPath());
            model.put("pageLocation", pageLocation);
            model.put("pageURL", pageURL.toString());
            model.put("pageletMode",pagelet);   //是否启用pagelet的小页面形式

            //如果是生产模式，提供一个JS压缩后缀给前端使用
            if(runtimeProperties.isProductionModel()){
                model.put("jsSuffix", ".min");
            }else{
                model.put("jsSuffix", "");
            }

//            model.put("",request.getServletPath());
//            model.put("",request.getRequestURI());
//            model.put("",request.getRequestURL());
            super.exposeHelpers(model, request);
        }
    }




    public void afterPropertiesSet() throws Exception {
        // 加上这句后，可以在页面上使用shiro标签
        configuration.setSharedVariable("briefPlainText", new BriefPlainTextMethodModel());
        configuration.setNumberFormat(properties.getNumberFormat());
        //设置自动包含文件:去重合并
        configuration.setAutoIncludes(ListKit.mergeDistinct(configuration.getAutoIncludes(),properties.getAutoIncludes()));
    }

}

package group.rober.runtime.holder;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Created by tisir yangsong158@qq.com on 2017-05-04
 * spring ApplicationContext保持器,通过该类,可以快速获取ApplicationContext对象
 */


@Component
@Scope("singleton")
public class ApplicationContextHolder implements ApplicationContextAware, DisposableBean {

    private static volatile ApplicationContext applicationContext = null;
    private static Logger logger = LoggerFactory.getLogger(ApplicationContextHolder.class);

    /**
     * 取得存储在静态变量中的ApplicationContext.
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     *
     * @param name name
     * @param <T> T
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T)applicationContext.getBean(name);
    }

    public static <T> T getBean(String name,Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(name,requiredType);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型
     *
     * @param requiredType requiredType
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     *
     * @param className className
     * @param <T> T
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByClassName(String className) {
        assertContextInjected();

        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("类:"+className+"，不存在!");
            throw new RuntimeException("类:"+className+"，不存在!",e);
        }

        return applicationContext.getBean((Class<T>) cls);
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext为Null.
     */
    public static void clearHolder() {
        logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        applicationContext = null;
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    public void setApplicationContext(ApplicationContext appContext) {
        logger.debug("注入ApplicationContext到SpringContextHolder:{}", appContext);

        if (ApplicationContextHolder.applicationContext != null) {
            logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
                    + ApplicationContextHolder.applicationContext);
        }

        ApplicationContextHolder.applicationContext = appContext; //NOSONAR
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    public void destroy() throws Exception {
        ApplicationContextHolder.clearHolder();
    }


    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        Validate.validState(applicationContext != null,
                "applicaitonContext属性未注入, 请在applicationContext.xml中定义ContextHolder");
    }
}

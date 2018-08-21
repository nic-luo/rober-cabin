package group.rober.runtime.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by tisir yangsong158@qq.com on 2017-05-31
 */
public class BeanPostProcessorImpl implements BeanPostProcessor {

    public BeanPostProcessorImpl() {

    }
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerMapping) {
            //当它被设置为true后，总是使用当前servlet上下文中的全路径进行URL查找，否则使用当前servlet映射内的路径。默认为false。
            ((RequestMappingHandlerMapping) bean).setAlwaysUseFullPath(false);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        /*if(bean instanceof BeanSelfAware){
            BeanSelfAware myBean = (BeanSelfAware)bean;
            myBean.setSelf((BeanSelfAware)bean);
            return myBean;
        }*/
        return bean;
    }
}

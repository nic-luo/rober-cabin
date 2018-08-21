package group.rober.base.listener;

import group.rober.runtime.holder.WebHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

//@ServletComponentScan
//@WebListener
public class WebHolderListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent event) {

    }

    public void contextInitialized(ServletContextEvent event) {
        WebHolder.setServletContext(event.getServletContext());
        WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext())
                .getAutowireCapableBeanFactory().autowireBean(this);
    }

}

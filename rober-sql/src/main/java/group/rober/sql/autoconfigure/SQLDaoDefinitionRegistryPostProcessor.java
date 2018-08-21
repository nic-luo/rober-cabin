package group.rober.sql.autoconfigure;

import group.rober.runtime.kit.ClassKit;
import group.rober.runtime.kit.StringKit;
import group.rober.sql.annotation.SQLDao;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class SQLDaoDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private List<String> lookupBasePackages(BeanDefinitionRegistry registry) {
        List<String> packages = new ArrayList<String>();
        String[] names = registry.getBeanDefinitionNames();
        for (int i = 0; i < names.length; i++) {
            String beanName = names[i];
            BeanDefinition definition = registry.getBeanDefinition(beanName);
            String clazzName = definition.getBeanClassName();
            if (StringKit.isBlank(clazzName)) {
                continue;
            }
            Class<?> clazz = ClassKit.forName(clazzName);
            ComponentScan anno = clazz.getAnnotation(ComponentScan.class);
            if (anno != null) {
                String[] basePackages = anno.basePackages();
                for (String pkg : basePackages) {
                    packages.add(pkg);
                }
            }
        }
        return packages;

    }

    private void register(String pkg, BeanNameGenerator beanNameGenerator, BeanDefinitionRegistry registry) {
        //取所有的自动扫描包
        Reflections reflections = null;
        reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(pkg)
                .filterInputsBy(new FilterBuilder().include(".*?\\.class"))//只要class文件
                .setExpandSuperTypes(false));
        try{
//            reflections = new Reflections(pkg);
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .setUrls(ClasspathHelper.forPackage(pkg))
//                .setScanners(new TypeAnnotationsScanner()));
        }catch(Exception e){}

        Set<Class<?>> clazzSet = reflections.getTypesAnnotatedWith(SQLDao.class);
        for (Class<?> clazz : clazzSet) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClass(SQLDaoFactoryBean.class);
            beanDefinition.setLazyInit(true);
            beanDefinition.getPropertyValues().addPropertyValue("clazz", clazz);
//            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            String beanName = clazz.getName();
            registry.registerBeanDefinition(beanName, beanDefinition);

        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        logger.info("自动扫描SQLDao.....[开始]");
        final BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
        List<String> packages = lookupBasePackages(registry);
        packages.forEach(pkg -> {
            register(pkg, beanNameGenerator, registry);
        });
        logger.info("自动扫描SQLDao.....[完成]");
    }

}

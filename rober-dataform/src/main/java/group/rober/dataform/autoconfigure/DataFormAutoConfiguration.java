package group.rober.dataform.autoconfigure;

import group.rober.dataform.DataFormConsts;
import group.rober.dataform.handler.impl.BeanDataListHandler;
import group.rober.dataform.handler.impl.BeanDataOneHandler;
import group.rober.dataform.handler.impl.MapDataListHandler;
import group.rober.dataform.handler.impl.MapDataOneHandler;
import group.rober.dataform.mapper.DataFormMapper;
import group.rober.dataform.mapper.impl.DataFormMapperDBTableImpl;
import group.rober.dataform.mapper.impl.DataFormMapperJSONFileImpl;
import group.rober.dataform.mapper.impl.DataFormMapperXMLFileImpl;
import group.rober.dataform.service.DataFormAdminService;
import group.rober.dataform.service.impl.DataFormAdminServiceDBImpl;
import group.rober.dataform.service.impl.DataFormAdminServiceJSONImpl;
import group.rober.dataform.tags.DataFormTags;
import group.rober.runtime.kit.ListKit;
import group.rober.sql.core.DataAccessor;
import group.rober.sql.core.MapDataAccessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by tisir yangsong158@qq.com on 2017-05-13
 * 认证授权服务模块自动注入,主要完成基本配置以及自动扫描包路径的基础设置
 */
@Configuration
@EnableConfigurationProperties(DataFormProperties.class)
@ComponentScan(basePackages={"group.rober.dataform"})
public class DataFormAutoConfiguration implements InitializingBean {

    private DataFormProperties properties;


    @Autowired
    private freemarker.template.Configuration freemarkerConfig;

    public DataFormAutoConfiguration(DataFormProperties properties) {
        this.properties = properties;
    }

    @Bean("dataFormMapper")
    public DataFormMapper getDataFormMapper(){
        if (DataFormProperties.TYPE_XML.equalsIgnoreCase(properties.getDataFormMapperType())) {
            DataFormMapperXMLFileImpl dataFormMapper = new DataFormMapperXMLFileImpl();
            dataFormMapper.setBasePath(properties.getResourcePath());
            dataFormMapper.setVersionConflictDetection(false);
            return  dataFormMapper;
        } else if (DataFormProperties.TYPE_JSON.equalsIgnoreCase(properties.getDataFormMapperType())) {
            DataFormMapperJSONFileImpl dataFormMapper = new DataFormMapperJSONFileImpl();
           /* dataFormMapper.setBasePath(properties.getResourcePath());
            dataFormMapper.setVersionConflictDetection(false);*/
            return  dataFormMapper;
        }
        else {
            DataFormMapperDBTableImpl dataFormMapper = new DataFormMapperDBTableImpl();
            return  dataFormMapper;
        }
    }

    @Bean("dataFormAdminService")
    public DataFormAdminService getDataFormAdminService(){
        if (DataFormProperties.TYPE_JSON.equalsIgnoreCase(properties.getDataFormMapperType())) {
            DataFormAdminServiceJSONImpl dataFormAdminServiceJSONImpl = new DataFormAdminServiceJSONImpl();
            return  dataFormAdminServiceJSONImpl;
        } else {
            DataFormAdminServiceDBImpl dataFormAdminServiceDBImpl = new DataFormAdminServiceDBImpl();
            return  dataFormAdminServiceDBImpl;
        }
    }

    @Bean(DataFormConsts.MAP_DATA_LIST_HANDLER_DEFAULT)
    @ConditionalOnClass({MapDataAccessor.class})
    public MapDataListHandler getMapDataListHandler(MapDataAccessor mapDataAccessor){
        MapDataListHandler mapDataListHandler = new MapDataListHandler();
        mapDataListHandler.setMapDataAccessor(mapDataAccessor);
        return mapDataListHandler;
    }

    @Bean(DataFormConsts.MAP_DATA_ONE_HANDLER_DEFAULT)
    @ConditionalOnClass({MapDataAccessor.class})
    public MapDataOneHandler getMapDataOneHandler(MapDataAccessor mapDataAccessor){
        MapDataOneHandler mapDataOneHandler = new MapDataOneHandler();
        mapDataOneHandler.setMapDataAccessor(mapDataAccessor);
        return mapDataOneHandler;
    }

    @Bean(DataFormConsts.BEAN_DATA_ONE_HANDLER_DEFAULT)
    @ConditionalOnClass({DataAccessor.class})
    public BeanDataOneHandler<Object> getBeanDataOneHandler(DataAccessor dataAccessor){
        BeanDataOneHandler<Object> handler = new BeanDataOneHandler<Object>();
        handler.setDataAccessor(dataAccessor);
        return handler;
    }

    @Bean(DataFormConsts.BEAN_DATA_LIST_HANDLER_DEFAULT)
    @ConditionalOnClass({DataAccessor.class})
    public BeanDataListHandler<Object> getBeanDataListHandler(DataAccessor dataAccessor){
        BeanDataListHandler<Object> handler = new BeanDataListHandler<Object>();
        handler.setDataAccessor(dataAccessor);
        return handler;
    }

    public void afterPropertiesSet() throws Exception {
        freemarkerConfig.setSharedVariable(properties.getDataformTagName(), new DataFormTags());
        //设置自动包含文件:去重合并
        freemarkerConfig.setAutoIncludes(ListKit.mergeDistinct(freemarkerConfig.getAutoIncludes(),properties.getAutoIncludes()));
    }
}

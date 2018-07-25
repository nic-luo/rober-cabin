package group.rober.sql.autoconfigure;

import group.rober.sql.core.DataAccessor;
import group.rober.sql.core.DataQuery;
import group.rober.sql.core.DataUpdater;
import group.rober.sql.core.MapDataAccessor;
import group.rober.sql.core.MapDataQuery;
import group.rober.sql.core.MapDataUpdater;
import group.rober.sql.serialno.finder.SerialNoGeneratorFinder;
import group.rober.sql.sqlfile.SQLTextLoader;
import group.rober.sql.sqlfile.impl.SQLTextLoaderImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@EnableConfigurationProperties(SqlProperties.class)
@ComponentScan(basePackages = "group.rober.sql")
@MapperScan(basePackages = "group.rober.sql.**.mapper")   //加上这句，指定mybatis包的扫描路径
public class SqlAutoConfiguration {

    private SqlProperties properties;

    public SqlAutoConfiguration(SqlProperties properties) {
        this.properties = properties;
    }

    @Bean("dataQuery")
    @ConditionalOnClass(NamedParameterJdbcTemplate.class)
    @ConditionalOnMissingBean(DataQuery.class)
    public DataQuery getDataQuery(NamedParameterJdbcTemplate jdbcTemplate){
        DataQuery query = new DataQuery();

        query.setJdbcTemplate(jdbcTemplate);
        query.setSqlDialectType(properties.getSqlDialectType());

        return query;
    }

    @Bean("dataUpdater")
    @ConditionalOnClass({DataQuery.class, SerialNoGeneratorFinder.class})
    @ConditionalOnMissingBean(DataUpdater.class)
    public DataUpdater getDataUpdater(DataQuery dataQuery, SerialNoGeneratorFinder finder){
        DataUpdater updater = new DataUpdater();
        updater.setJdbcTemplate(dataQuery.getJdbcTemplate());
        updater.setDataQuery(dataQuery);
        updater.setSerialNoGeneratorFinder(finder);
        updater.setSqlDialectType(properties.getSqlDialectType());
        return updater;
    }

    @Bean("mapDataQuery")
    @ConditionalOnClass(NamedParameterJdbcTemplate.class)
    @ConditionalOnMissingBean(MapDataQuery.class)
    public MapDataQuery getMapDataQuery(NamedParameterJdbcTemplate jdbcTemplate){
        MapDataQuery query = new MapDataQuery();

        query.setJdbcTemplate(jdbcTemplate);
        query.setSqlDialectType(properties.getSqlDialectType());

        return query;
    }
    @Bean("mapDataUpdater")
    @ConditionalOnClass(NamedParameterJdbcTemplate.class)
    @ConditionalOnMissingBean(MapDataUpdater.class)
    public MapDataUpdater getMapDataUpdater(NamedParameterJdbcTemplate jdbcTemplate,MapDataQuery mapDataQuery){
        MapDataUpdater updater = new MapDataUpdater();

        updater.setJdbcTemplate(jdbcTemplate);
        updater.setDataQuery(mapDataQuery);
        updater.setSqlDialectType(properties.getSqlDialectType());

        return updater;
    }

    @Bean("dataAccessor")
    @ConditionalOnClass({DataQuery.class,DataUpdater.class})
    @ConditionalOnMissingBean(DataAccessor.class)
    public DataAccessor getDataAccessor(DataQuery dataQuery,DataUpdater dataUpdater){
        DataAccessor dataAccessor = new DataAccessor(dataQuery,dataUpdater);
        return dataAccessor;
    }

    @Bean("mapDataAccessor")
    @ConditionalOnClass({MapDataQuery.class,MapDataUpdater.class})
    @ConditionalOnMissingBean(MapDataAccessor.class)
    public MapDataAccessor getMapDataAccessor(MapDataQuery mapDataQuery,MapDataUpdater mapDataUpdater){
        MapDataAccessor mapDataAccessor = new MapDataAccessor(mapDataQuery,mapDataUpdater);
        return mapDataAccessor;
    }

    @Bean("sqlFileLoader")
    public SQLTextLoader getSQLFileLoader(){
        SQLTextLoader loader = new SQLTextLoaderImpl();
        return loader;
    }

//    @Bean("sqlBeanPostProcessor")
//    public BeanPostProcessor getBeanPostProcessor(){
//        return new BeanPostProcessor(){
//
//            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//                return bean;
//            }
//
//            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//                System.out.println("CLAXX:"+bean.getClass().getName());
//                SQLDataAccessor sqlDataAccessor = bean.getClass().getAnnotation(SQLDataAccessor.class);
//                if(sqlDataAccessor != null){
//                    System.out.println("XXXX:"+bean.getClass().getName());
//                }
//                return bean;
//            }
//        };
//    }
}

package group.rober.sql.autoconfigure;

import group.rober.runtime.holder.ApplicationContextHolder;
import group.rober.runtime.kit.StringKit;
import group.rober.sql.annotation.SQLDao;
import group.rober.sql.annotation.NamedMapping;
import group.rober.sql.annotation.SQLParam;
import group.rober.sql.annotation.SQLText;
import group.rober.sql.core.DataAccessor;
import group.rober.sql.core.MapDataAccessor;
import group.rober.sql.exception.SQLProcessException;
import group.rober.sql.sqlfile.SQLCollecter;
import group.rober.sql.sqlfile.impl.SQLTextLoaderImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class SQLDaoFactoryBean implements FactoryBean {

    private Class<?> clazz;

    public Object getObject() throws Exception {
        SQLAccessorProxy proxy = new SQLAccessorProxy(clazz);
        Object impl = proxy.getProxy();
        return impl;
    }

    public Class<?> getObjectType() {
        return clazz;
    }

    public boolean isSingleton() {
        return true;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static class SQLAccessorProxy implements MethodInterceptor {
        private Class<?> interfaceClazz;
        private Enhancer enhancer = new Enhancer();
        private DataAccessor dataAccessor;
        private MapDataAccessor mapDataAccessor;

        public SQLAccessorProxy(Class<?> interfaceClazz) {
            this.interfaceClazz = interfaceClazz;
        }

        public Object getProxy(){
            enhancer.setSuperclass(interfaceClazz); //设置需要创建子类的类
            enhancer.setCallback(this);
            return enhancer.create();               //通过字节码技术动态创建子类实例
        }

        //实现MethodInterceptor接口方法
        public Object intercept(Object object, Method method, Object[] args,
                                MethodProxy proxy) throws Throwable {
            String methodName = method.getName();
            switch (methodName){
                case "hashCode":return proxy.invokeSuper(object, args);//通过代理类调用父类中的方法
                case "toString":return proxy.invokeSuper(object, args);
            }

//            System.out.println("调用方法："+object.getClass().getName()+"->"+method.getName());
            return parseInvoke(object,method,args,proxy);
        }

        public Object parseInvoke(Object object, Method method, Object[] args,
                                  MethodProxy proxy){
            String mapFile = getSqlMapFile();
            String sql = getSqlText(method,mapFile);
            if(StringKit.isBlank(sql)){
                String className = Optional
                        .ofNullable(object)
                        .map(obj->obj.getClass().getName())
                        .orElseGet(()->"");

                String methodName = Optional
                        .ofNullable(method)
                        .map(m->m.getName())
                        .orElseGet(()->"");

                throw new SQLProcessException("{0}.{1}() 方法没有找到对应的SQL",className,methodName);
            }
            Class<?> dataClazz = getReturnDataClass(method);

            Map<String,Object> queryParam = new HashMap<>();
            //取方法的参数名，这种做法只有1.8才支持
//            Parameter[] parameters = method.getParameters();
//            if(parameters!=null&&parameters.length>0){
//                for(Parameter p: parameters){
//                    System.out.println("parameter: " + p.getType().getName() + ", " + p.getName());
//                }
//            }
            //根据注解，组装查询参数
//            Class<?>[] parameterTypes = method.getParameterTypes();
//            for(int i=0;i<parameterTypes.length;i++){
//                Class<?> ptClazz = parameterTypes[i];
//                SQLParam sqlParam = ptClazz.getAnnotation(SQLParam.class);
//                if(sqlParam==null||StringKit.isBlank(sqlParam.value())){
//                    continue;
//                }
//                queryParam.put(sqlParam.value(),args[i]);
//            }

            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
//                Annotation[] annotations = parameter.getDeclaredAnnotations();
                SQLParam sqlParam = parameter.getAnnotation(SQLParam.class);
                if(sqlParam==null||StringKit.isBlank(sqlParam.value())){
                    continue;
                }
                queryParam.put(sqlParam.value(),args[i]);
            }

            //如果不是Map，那么就是JavaBean
            if(!dataClazz.isAssignableFrom(Map.class)){
                boolean isReturnListData = isReturnListData(method);
                if(isReturnListData){
                    return selectList(dataClazz,sql,queryParam);
                }else{
                    return selectOne(dataClazz,sql,queryParam);
                }
            }

            return null;
        }

        public DataAccessor getDataAccessor() {
            if(dataAccessor == null) dataAccessor = ApplicationContextHolder.getBean(DataAccessor.class);
            return dataAccessor;
        }



        public MapDataAccessor getMapDataAccessor() {
            if(mapDataAccessor == null) mapDataAccessor = ApplicationContextHolder.getBean(MapDataAccessor.class);
            return mapDataAccessor;
        }



        private Object selectOne(Class<?> clazz, String sql,Map<String,Object> queryParam){
            return getDataAccessor().selectOne(clazz,sql,queryParam);
        }
        private List<?> selectList(Class<?> clazz,String sql,Map<String,Object> queryParam){
            return getDataAccessor().selectList(clazz,sql,queryParam);
        }

        /**
         * 获取返回数据的数据类型
         * @param method
         * @return
         */
        private Class<?> getReturnDataClass(Method method){
            Class<?> clazz = method.getReturnType();
            //如果是List，则需要取List<T>中T的类型
            if(isReturnListData(method)){
                clazz = ResolvableType.forMethodReturnType(method).getGeneric(0).getRawClass();
            }
            return clazz;
        }

        private boolean isReturnListData(Method method){
            Class<?> clazz = method.getReturnType();
            return clazz.isAssignableFrom(List.class);
        }

        /**
         * 映射文件规则
         * 1. 如果注解SQLDao配置了映射文件，直接取
         * 2. 如果没有配置SQLDao,则根据接口的classpath取同名文件+.sql.md
         *
         * @return
         */
        private String getSqlMapFile(){
            String mapFile = StringKit.format("classpath:{0}.sql.md",interfaceClazz.getName().replaceAll("\\.","/"));
            SQLDao anno = interfaceClazz.getAnnotation(SQLDao.class);
            if(StringKit.isNoneBlank(anno.value())){
                mapFile = anno.value();
            }
            return mapFile;
        }

        /**
         * SQL计算规则优先级
         * 1. 文件.md.sql优先级最高
         * 2. SQL内容注解
         * 3. 根据API名称自动计算
         *
         * @param method
         * @param mapFile
         * @return
         */
        private String getSqlText(Method method,String mapFile){
            SQLTextLoaderImpl loader = new SQLTextLoaderImpl();
            SQLCollecter sqlCollecter = loader.parse(mapFile);

            //取查询名称，如果注解绑定的查询映射，则使用注解绑定的，如果没有，则使用方法名
            String sqlName = method.getName();
            NamedMapping nameAnno = method.getAnnotation(NamedMapping.class);
            if(nameAnno != null && StringKit.isNotBlank(nameAnno.value())){
                sqlName = nameAnno.value();
            }
            String sqlText = sqlCollecter.sql(sqlName);

            //如果映射文件中没有,则查找注解
            if(StringKit.isBlank(sqlText)){
                SQLText sqlAnno = method.getAnnotation(SQLText.class);
                if(sqlAnno != null && StringKit.isNotBlank(sqlAnno.value())){
                    sqlText = sqlAnno.value();
                }
            }

            //如果注解也没有，则根据API名称自己计算SQL
            if(StringKit.isBlank(sqlText)){
                sqlText = calcSelectSqlByMethodName(method,"DEMO_TABLE1");
            }

            return sqlText;
        }

        private String calcSelectSqlByMethodName(Method method,String table){
            return null;
        }
    }

}

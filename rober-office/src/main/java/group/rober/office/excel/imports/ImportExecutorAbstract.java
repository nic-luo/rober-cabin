package group.rober.office.excel.imports;

import group.rober.office.excel.imports.exception.ImportExecutorException;
import group.rober.office.excel.imports.config.ExcelImportConfig;
import group.rober.office.excel.imports.config.ExcelImportConfigLoader;
import group.rober.office.excel.imports.intercept.DataProcessIntercept;
import group.rober.office.excel.imports.intercept.DataRowSpecialProcessIntercept;
import group.rober.office.excel.imports.intercept.DataRowValidateIntercept;
import group.rober.runtime.kit.ClassKit;
import group.rober.runtime.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把通用部分剥离出来
 *
 * @param <T> T
 */
public abstract class ImportExecutorAbstract<T> implements ImportExecutor<T> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ExcelImportConfig importConfig;

    public ImportExecutorAbstract(ExcelImportConfigLoader configLoader, String itemName) {
        this.importConfig = configLoader.getExcelImportConfig(itemName);
    }

    public void init() throws ImportExecutorException {
        initIntercept(importConfig);
    }

    public ImportExecutorAbstract appendIntercept(DataProcessIntercept intercept){
        importConfig.getIntercepts().add(intercept);
        return this;
    }

    /**
     * 初始化数据拦截处理器
     *
     * @param config config
     * @throws ImportExecutorException ImportExecutorException
     */
    protected void initIntercept(ExcelImportConfig config) throws ImportExecutorException {
        //校验类
        appendIntercept(new DataRowValidateIntercept());
        //值转换器
//        config.getIntercepts().add(new DataRowConvertIntercept(jdbcTemplate));
        //特殊处理
        appendIntercept(new DataRowSpecialProcessIntercept());

        //自定义拦截器
        String className = config.getIntercept();
        if(!StringKit.isEmpty(className)){
            Object inst = ClassKit.newInstance(className);
            appendIntercept((DataProcessIntercept)inst);
        }
    }
}

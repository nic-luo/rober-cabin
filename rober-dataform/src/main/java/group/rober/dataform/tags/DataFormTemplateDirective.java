package group.rober.dataform.tags;

import freemarker.template.*;
import group.rober.runtime.holder.ApplicationContextHolder;

import java.util.Map;

public class DataFormTemplateDirective {

    protected SimpleScalar getSimpleScalar(Map params, String name){
        TemplateModel paramValue = (TemplateModel) params.get(name);
        if(paramValue instanceof SimpleScalar){
            return ((SimpleScalar)paramValue);
        }
        return null;
    }
    protected TemplateNumberModel getNumberModel(Map params, String name){
        TemplateModel paramValue = (TemplateModel) params.get(name);
        if(paramValue instanceof TemplateNumberModel){
            return ((TemplateNumberModel)paramValue);
        }
        return null;
    }

    public String getParamAsStr(Map params, String name){
        SimpleScalar value = getSimpleScalar(params,name);
        if(value==null)return null;
        return value.getAsString();
    }

    public Integer getParamAsInt(Map params, String name) throws TemplateModelException {
        TemplateNumberModel value = getNumberModel(params,name);
        if(value==null)return null;
        return value.getAsNumber().intValue();
    }

    public Double getParamAsDouble(Map params, String name) throws TemplateModelException {
        TemplateNumberModel value = getNumberModel(params,name);
        if(value==null)return null;
        return value.getAsNumber().doubleValue();
    }

    /**
     * 获取String类型的参数的值
     *
     * @param paramName paramName
     * @param paramMap paramMap
     * @return string
     * @throws TemplateModelException TemplateModelException
     */
    public String getString(String paramName, Map<String, TemplateModel> paramMap) throws TemplateModelException{
        TemplateModel model = paramMap.get(paramName);
        if(model == null){
            return null;
        }
        if(model instanceof TemplateScalarModel){
            return ((TemplateScalarModel)model).getAsString();
        }else if (model instanceof TemplateNumberModel) {
            return ((TemplateNumberModel)model).getAsNumber().toString();
        }else{
            throw new TemplateModelException(paramName);
        }
    }

    /**
     *
     * 获得int类型的参数
     *
     * @param paramName paramName
     * @param paramMap paramMap
     * @return int
     * @throws TemplateModelException TemplateModelException
     */
    public Integer getInt(String paramName, Map<String, TemplateModel> paramMap) throws TemplateModelException{
        TemplateModel model = paramMap.get(paramName);
        if(model==null){
            return null;
        }
        if(model instanceof TemplateScalarModel){
            String str = ((TemplateScalarModel)model).getAsString();
            try {
                return Integer.valueOf(str);
            } catch (NumberFormatException e) {
                throw new TemplateModelException(paramName);
            }
        }else if(model instanceof TemplateNumberModel){
            return ((TemplateNumberModel)model).getAsNumber().intValue();
        }else{
            throw new TemplateModelException(paramName);
        }
    }

    protected Configuration getConfiguration(){
        Configuration configuration = ApplicationContextHolder.getBean(Configuration.class);
        return configuration;
    }
}

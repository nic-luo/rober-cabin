package group.rober.dataform.util;

import group.rober.dataform.exception.DataFormException;
import group.rober.dataform.exception.ValidatorException;
import group.rober.dataform.mapper.impl.po.DataFormElementPO;
import group.rober.dataform.mapper.impl.po.FormElementValidatorPO;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.dataform.model.types.ElementValidatorMode;
import group.rober.dataform.model.types.ElementValidatorRunAt;
import group.rober.dataform.model.types.FormDataModelType;
import group.rober.dataform.validator.ValidateRecord;
import group.rober.dataform.validator.ValidateResult;
import group.rober.runtime.holder.MessageHolder;
import group.rober.runtime.kit.BeanKit;
import group.rober.runtime.kit.ClassKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.MapData;
import group.rober.runtime.lang.PairBond;
import group.rober.runtime.lang.ValueObject;
import group.rober.sql.core.DataAccessor;
import group.rober.sql.core.PaginationData;
import group.rober.sql.core.PaginationQuery;
import group.rober.sql.dialect.SqlDialectType;
import group.rober.sql.kit.SQLInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by luyu on 2017/12/26.
 */
public abstract class DataFormUtils {
    protected static Logger logger = LoggerFactory.getLogger(DataFormUtils.class);

    public static DataFormElementPO convertElementPO(DataFormElement dataFormElement) {
        DataFormElement.FormElementUIHint hint = dataFormElement.getElementUIHint();

        DataFormElementPO elementPO = new DataFormElementPO();
        BeanKit.copyProperties(dataFormElement, elementPO);
        BeanKit.copyProperties(dataFormElement.getElementUIHint(), elementPO);

        elementPO.setPrimaryKey(dataFormElement.getPrimaryKey() ? "Y" : "N");
        elementPO.setUpdateable(dataFormElement.getUpdateable() ? "Y" : "N");
        elementPO.setPersist(dataFormElement.getPersist() ? "Y" : "N");
        elementPO.setSortable(dataFormElement.getSortable() ? "Y" : "N");
        elementPO.setEnabled(dataFormElement.getEnabled() ? "Y" : "N");

        elementPO.setReadonly(hint.getReadonly() ? "Y" : "N");
        elementPO.setRequired(hint.getRequired() ? "Y" : "N");
        elementPO.setVisible(hint.getVisible() ? "Y" : "N");
        elementPO.setDictCodeLazy(hint.getDictCodeLazy() ? "Y" : "N");
        elementPO.setDictCodeTreeLeafOnly(hint.getDictCodeTreeLeafOnly() ? "Y" : "N");
        elementPO.setDictCodeTreeFull(hint.getDictCodeTreeFull() ? "Y" : "N");


        return elementPO;
    }

    public static List<FormElementValidatorPO> convertValidatorPO(DataFormElement element, String dataformId) {
        List<FormElementValidatorPO> validatorPOs = element.getValidatorList().stream()
                .map(formElementValidator -> getFormElementValidatorPO(formElementValidator, element.getCode(), dataformId))
                .collect(Collectors.toList());
        return validatorPOs;
    }

    private static FormElementValidatorPO getFormElementValidatorPO(
            DataFormElement.FormElementValidator formElementValidator, String code, String dataformId) {
        FormElementValidatorPO validatorPO = new FormElementValidatorPO();
        BeanKit.copyProperties(formElementValidator, validatorPO);
        validatorPO.setElementCode(code);
        validatorPO.setDataformId(dataformId);
        validatorPO.setMessage(formElementValidator.getDefaultMessage());
        validatorPO.setMessageI18nCode(formElementValidator.getDefaultMessageI18nCode());
        return validatorPO;
    }

    public static Class<?> getDataFormClass(DataForm dataForm) {
        FormDataModelType dataModelType = dataForm.getDataModelType();
        String id = dataForm.getId();

        Class<?> clazz = null;
        try {
            clazz = Class.forName(dataForm.getDataModel());
        } catch (ClassNotFoundException e) {
            String error = "DataModel配置错误，formId={0},DataModelType={1},模型类:{2}不存在";
            throw new DataFormException(error, id, dataModelType, dataForm.getDataModel());
        }
        return clazz;
    }

    public static List<Object> mapDataListToBeanDataList(DataForm dataForm, List<MapData> dataList) {
        Class<?> clazz = getDataFormClass(dataForm);
        //根据模板绑定的模型，把数据填充进去
        Object dataBean = ClassKit.newInstance(clazz);
        List<Object> objectList = new ArrayList<>();
        for (MapData mapData : dataList) {
            Object inst = BeanKit.deepClone(dataBean);
            BeanKit.mapFillBean(mapData, inst);
            objectList.add(inst);
        }

        return objectList;
    }

    public static Object mapDataToBeanData(DataForm dataForm, MapData mapData) {
        Class<?> clazz = getDataFormClass(dataForm);
        //根据模板绑定的模型，把数据填充进去
        Object dataBean = ClassKit.newInstance(clazz);

        Object object = BeanKit.deepClone(dataBean);
        BeanKit.mapFillBean(mapData, object);

        return object;

    }


    /**
     * 根据显示模板类型类型，取MAP相应元素的值
     *
     * @param element element
     * @param entity entity
     * @return object
     */
    public static Object getMapValue(DataFormElement element, MapData entity) {
        String elementCode = element.getCode();
        ValueObject v = entity.get(elementCode, false);
        return fitValueByValueObject(element,v);
    }

    /**
     * 根据显示模板配置的数据类型，对数值作相应的修正
     *
     * @param element element
     * @param value value
     * @return object
     */
    public static Object fitValue(DataFormElement element, Object value) {
        ValueObject v = ValueObject.valueOf(value);
        return fitValueByValueObject(element,v);
    }

    private static Object fitValueByValueObject(DataFormElement element,ValueObject v){
        switch (element.getDataType()) {
            case Date:
                return v.dateValue();
            case Double:
                return v.doubleValue();
            case Integer:
                return v.intValue();
            default:
                return v.strValue("");
        }
    }

    public static <T> PaginationQuery buildPaginationQuery(DataForm dataForm,String sql,Integer pageIndex,Integer pageSize){
        PaginationQuery query = new PaginationQuery();
        query.setQuery(sql);
        query.setIndex(pageIndex);
        query.setSize(pageSize);

        List<DataFormElement> elements = dataForm.getElements();
        elements.forEach(element -> {
            String summaryExpression = element.getSummaryExpression();
            if(StringKit.isNotBlank(summaryExpression)){
                String name = StringKit.nvl(element.getCode(),element.getColumn());
//                if(StringKit.isBlank(name)){
//                    name = getColumnByCode(dataForm,element.getCode());
//                }
                PairBond<String,String> column = new PairBond<String,String>();

                String columnName = StringKit.nvl(element.getColumn(),StringKit.camelToUnderline(element.getCode()));
                column.setLeft(columnName);
                column.setRight(name);

                query.addSummaryExpression(column,summaryExpression);
            }
        });
        return query;
    }

    /**
     * 根据列名转换为代码
     *
     * @param dataForm dataForm
     * @param fieldCode fieldCode
     * @return string
     */
    public static String getColumnByCode(DataForm dataForm,String fieldCode){
        List<PairBond> pairBonds = dataForm.getQuery().getSelectItems();
        for(PairBond<String,String> pairBond :pairBonds){
            if(fieldCode.equals(pairBond.getLeft())){
                return pairBond.getRight();
            }
        }
        return null;
    }

    /**
     * 根据代码转换为列名
     *
     * @param dataForm dataForm
     * @param column column
     * @return string
     */
    public static String getCodeByColumn(DataForm dataForm,String column){
        List<PairBond> pairBonds = dataForm.getQuery().getSelectItems();
        for(PairBond<String,String> pairBond :pairBonds){
            if(column.equals(pairBond.getRight())){
                return pairBond.getLeft();
            }
        }
        return null;
    }

    /**
     * 根据DataForm的配置，重新处理小计以及合计部分的KEY值
     *
     * @param dataForm dataForm
     * @param paginationData paginationData
     * @param <T> T
     */
    public static <T> void fixSummaryCodeName(DataForm dataForm, PaginationData<T> paginationData){
        if(paginationData==null
                ||paginationData.getSummarizes()==null||paginationData.getSummarizes().isEmpty()
                ||paginationData.getTotalSummarizes()==null||paginationData.getTotalSummarizes().isEmpty()
                ){
            return ;
        }

        Map<String,Object> summarizes = paginationData.getSummarizes();
        Map<String,Object> totalSummarizes = paginationData.getTotalSummarizes();

        paginationData.setSummarizes(newFixedSummary(dataForm,summarizes));
        paginationData.setTotalSummarizes(newFixedSummary(dataForm,totalSummarizes));

    }

    /**
     * 根据显示模板配置，重新修正统计结果
     *
     * @param dataForm dataForm
     * @param summaryData summaryData
     * @return map
     */
    private static Map<String,Object> newFixedSummary(DataForm dataForm,Map<String,Object> summaryData){
        Map<String,Object> retData = new LinkedHashMap<String,Object>();
        Iterator<String> iterator = summaryData.keySet().iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            Object value = summaryData.get(name);
            DataFormElement element = lookupDataFormElement(dataForm,name);
            if(element!=null){
                Object fitValue = fitValue(element,value);
                String fitName = StringKit.nvl(element.getCode(),element.getColumn());
                retData.put(fitName,fitValue);
            }
        }
        return retData;
    }

    /**
     * 根据字段名称，查找元素
     *
     * @param dataForm dataForm
     * @param name name
     * @return DataFormElement
     */
    public static DataFormElement lookupDataFormElement(DataForm dataForm,String name){
        List<DataFormElement> elements = dataForm.getElements();
        for(DataFormElement element:elements){
            if(name.equalsIgnoreCase(element.getCode())||name.equalsIgnoreCase(element.getColumn())){
                return element;
            }
        }
        return null;
    }



}

package group.rober.dataform.service.impl;

import group.rober.dataform.DataFormConsts;
import group.rober.dataform.dto.CloneDataFormBean;
import group.rober.dataform.exception.DataFormException;
import group.rober.dataform.mapper.DataFormMapper;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.dataform.service.DataFormAdminServiceAbstract;
import group.rober.dataform.service.DataFormPublicService;
import group.rober.runtime.kit.BeanKit;
import group.rober.runtime.kit.FileKit;
import group.rober.runtime.kit.JSONKit;
import group.rober.runtime.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用JSON实现显示模板的读取写入
 */
public class DataFormAdminServiceJSONImpl extends DataFormAdminServiceAbstract {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataFormPublicService dataFormPublicService;
    @Autowired
    private DataFormMapper dataFormMapper;



    @Override
    public List<DataForm> getDataForms() {
        return dataFormPublicService.getDataForms();
    }

    @Override
    public DataForm  getDataForm(String dataFormId) {
        return dataFormMapper.getDataForm(dataFormId);
    }


    @Override
    public DataForm cloneDataForm(CloneDataFormBean cloneDataFormBean) {
        this.clearCacheItem(cloneDataFormBean.getNewDataFormId());
        File newFile = dataFormPublicService.getFileByDataFormId(cloneDataFormBean.getNewDataFormId());
        if (newFile.exists())
            throw new DataFormException("new dataFormId has existed");
        DataForm oldDataForm = this.getDataForm(cloneDataFormBean.getOldDataFormId());

        DataForm cloneDataForm = BeanKit.deepClone(oldDataForm);
        cloneDataForm.setId(cloneDataFormBean.getNewDataFormId());
        cloneDataForm.setName("copyof" + oldDataForm.getName());
        cloneDataForm.getElements().stream().forEach(dataFormElement -> {
            dataFormElement.setDataformId(cloneDataFormBean.getNewDataFormId());
        });
        dataFormPublicService.saveDataForm(cloneDataForm,newFile);
        return cloneDataForm;
    }



    @Override
    public DataFormElement getDataFormElementDetail(String dataformId, String code) {
        DataForm dataForm = this.getDataForm(dataformId);
        List<DataFormElement> elements = dataForm.getElements().stream()
                .filter(dataFormElement -> code.equals(dataFormElement.getCode()))
                .collect(Collectors.toList());
        DataFormElement dataFormElement = null;
        if (elements.size() > 0) {
            dataFormElement = elements.get(0);
        }
        return dataFormElement;
    }

    @Override
    public DataForm saveDataForm(DataForm dataForm, String oldDataFormId) {
        String newDataFormId = StringKit.format("{0}-{1}", dataForm.getPack(), dataForm.getCode());
        dataFormMapper.save(dataForm);
        if (!StringUtils.isEmpty(oldDataFormId) && !newDataFormId.equals(oldDataFormId)) {
            this.deleteDataForm(oldDataFormId);
        }
        return dataForm;
    }

    @Override
    public DataFormElement saveDataFormElement(DataFormElement element, String dataFormId) {
        dataFormMapper.saveDataFormElement(element);
        return element;
    }


    @Override
    public void deleteDataForm(String dataFormId) {
        dataFormMapper.delete(dataFormId);
    }

    /**
     * 获取JSON文件存储的物理目录
     *
     * @return File
     */
    public File getStoredDirectory(){
        return null;
    }



    /**
     * 把json文件中的显示模板提取到数据表中
     */
    public void jsonFileTransferToDb(){

    }

    /**
     * 清空缓存
     *
     * @param formId formId
     */
    @CacheEvict(value=DataFormConsts.CACHE_KEY,key="#formId",beforeInvocation=true)
    public void clearCacheItem(String formId){

    }
}

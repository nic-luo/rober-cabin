package group.rober.dataform.mapper.impl;

import group.rober.dataform.DataFormConsts;
import group.rober.dataform.autoconfigure.DataFormProperties;
import group.rober.dataform.exception.DataFormException;
import group.rober.dataform.mapper.DataFormMapper;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.dataform.service.DataFormPublicService;
import group.rober.runtime.autoconfigure.RuntimeProperties;
import group.rober.runtime.kit.FileKit;
import group.rober.runtime.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luyu on 2018/6/1.
 */
public class DataFormMapperJSONFileImpl implements DataFormMapper {

    @Autowired
    private RuntimeProperties runtimeProperties;
    @Autowired
    private DataFormPublicService dataFormPublicService;
    @Autowired
    DataFormProperties dataFormProperties;
    @Autowired
    DataFormMapperJSONFileImpl self;

    @Override
    public void clearCacheAll() {

    }

    @Override
    public void clearCacheItem(String formId) {

    }

    @Override
    public boolean exists(String id) {
        File file = dataFormPublicService.getFileByDataFormId(id);
        if (file.exists())
            return Boolean.TRUE;
        return Boolean.FALSE;
    }


    public DataForm getDataForm(String id) {
        if(runtimeProperties.isProductionModel()){
            return self.getDataFormMybeCache(id);  //通过代理调用，可能会使用缓存
        }else{
            return this.getDataFormMybeCache(id);  //直接调用，一定不会使用缓存
        }
    }

    /**
     * 获取显示模板，可能会取缓存
     *
     * @param id id
     * @return DataForm
     */
    @Cacheable(value= DataFormConsts.CACHE_KEY,key="#id")
    public DataForm getDataFormMybeCache(String id){
        DataForm dataForm = null;
        File file = dataFormPublicService.getFileByDataFormId(id);
        if(file.exists()){
            dataForm = dataFormPublicService.getDataFormFromFile(file);
        }

        if(dataForm == null){
            String urlStr = MessageFormat.format("classpath:{0}/{1}.json"
                    ,dataFormProperties.getDataformDataClasspath()
                    ,id.replace("-","/"));
            URL url = null;
            try {
                url = ResourceUtils.getURL(urlStr);
            } catch (FileNotFoundException e) {
                throw new DataFormException("",e);
            }
            dataForm = dataFormPublicService.getDataFormFrom(url);
        }

        return dataForm;
    }

    @Override
    public DataForm getDataForm(String pack, String code) {
        String dataFormId = StringKit.format("{0}-{1}", pack, code);
        return self.getDataForm(dataFormId);
    }

    @Override
    public List<DataForm> getDataFormsByPack(String pack) {
        String directory = dataFormPublicService.getDataformDataDirectory().getAbsolutePath();
        File packDir = null;
        List<DataForm> dataForms = new ArrayList<>();
        if (!StringUtils.isEmpty(pack)) {
            packDir = FileKit.getFile(directory,pack);
            if (!packDir.exists())
                return dataForms;
            File[] dataFormFiles = packDir.listFiles();
            if (dataFormFiles == null)
                return dataForms;
            for (int fileIndex = 0; fileIndex < dataFormFiles.length; fileIndex++) {
                String dataFormId = dataFormPublicService.getDataFormIdByJsonFile(dataFormFiles[fileIndex],pack);
                DataForm dataForm = self.getDataForm(dataFormId);
                dataForms.add(dataForm);
            }
        } else {
            packDir = FileKit.getFile(directory);
            if (!packDir.exists()) {
                URL url = this.getClass().getClassLoader().getResource(dataFormProperties.getDataformDataClasspath());
                packDir = new File(url.getFile());
            }
            File[] dirs = packDir.listFiles();
            if (dirs == null)
                return dataForms;
            for (int i = 0; i < dirs.length; i++) {
                File[] dataFormFiles = dirs[i].listFiles();
                if (dataFormFiles == null)
                    continue;
                for (int fileIndex = 0; fileIndex < dataFormFiles.length; fileIndex++) {
                    File tFile = dataFormFiles[fileIndex];
                    if("index.builder.json".equals(tFile.getName())) continue;
                    String dataFormId = dataFormPublicService.getDataFormIdByJsonFile(tFile,dirs[i].getName());
                    DataForm dataForm = self.getDataForm(dataFormId);
                    dataForms.add(dataForm);
                }
            }
        }
        return dataForms;
    }

    @Override
    public List<DataForm> getAllDataForms() {
        return getDataFormsByPack(null);
    }

    @Override
    public int save(DataForm dataForm) {
        self.clearCacheItem(dataForm.getId());
        String dataFormId = StringKit.format("{0}-{1}", dataForm.getPack(), dataForm.getCode());
        File jsonFile = FileKit.getFile(dataFormPublicService.getDataformDataDirectory().getAbsolutePath(),dataForm.getPack(),dataForm.getCode() + ".json");
        dataForm.getElements().stream().forEach(dataFormElement -> {
            dataFormElement.setDataformId(dataFormId);
        });
        return dataFormPublicService.saveDataForm(dataForm,jsonFile);
    }

    @Override
    public int saveDataFormElement(DataFormElement element) {
        String dataFmId = element.getDataformId();
        self.clearCacheItem(dataFmId);
        File file = dataFormPublicService.getFileByDataFormId(dataFmId);

        DataForm dataForm = self.getDataForm(dataFmId);
        List<DataFormElement> dataFormElements = dataForm.getElements();
        dataFormElements = dataFormElements.stream().filter(dataFormElement ->  !dataFormElement.getCode().equals(element.getCode())).collect(Collectors.toList());
        dataFormElements.add(element);
        dataForm.setElements(dataFormElements);
        return dataFormPublicService.saveDataForm(dataForm,file);
    }

    @Override
    public int delete(String id) {
        Integer result = 1;
        File file = dataFormPublicService.getFileByDataFormId(id);
        if (!file.exists())
            return result;
        Boolean isDeleted = file.delete();
        if (isDeleted)
            return result;
        return 0;
    }

    @Override
    public int delete(String pack, String code) {
        String dataFormId = StringKit.format("{0}-{1}", pack, code);
        return delete(dataFormId);
    }

    @Override
    public int deleteAll() {
        File directory = dataFormPublicService.getDataformDataDirectory().getAbsoluteFile();
        File[] dirs = directory.listFiles();
        if (dirs == null)
            return 1;
        for (int i = 0; i < dirs.length; i++) {
            dirs[i].delete();
        }
        return 1;
    }



}

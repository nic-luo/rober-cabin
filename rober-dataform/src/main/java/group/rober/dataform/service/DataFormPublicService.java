package group.rober.dataform.service;

import group.rober.dataform.autoconfigure.DataFormProperties;
import group.rober.dataform.exception.DataFormException;
import group.rober.dataform.model.DataForm;
import group.rober.runtime.kit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luyu on 2018/6/1.
 */
@Service
public class DataFormPublicService {

    @Autowired
    protected DataFormProperties properties;

    public File getDataformDataDirectory(){
        String devBaseDir = properties.getDevBaseDir();
        String dataFormDataClasspath = properties.getDataformDataClasspath();
        File workPath = new File(this.getClass().getClassLoader().getResource("").getFile());
        String projectRoot = workPath.getParentFile().getParentFile().getParent();
        projectRoot = projectRoot.replaceAll("\\\\", "/");//对windows环境下的路径做一下替换
        ValidateKit.notBlank(devBaseDir,"开发模式下，请设置显示模板资源文件路径:devBaseDir");
        devBaseDir = devBaseDir.replaceAll("\\$\\{projectRoot\\}",projectRoot);

        String baseDirPath = FileKit.standardFilePath(devBaseDir);
        if(baseDirPath.endsWith("/"))baseDirPath = baseDirPath.substring(0,baseDirPath.length()-1);

        File baseDir = new File(baseDirPath);
        String relativePath = dataFormDataClasspath.replaceAll("\\.","/");
        if(!relativePath.startsWith("/"))relativePath = "/"+relativePath;
        return new File(baseDir+relativePath);
    }

    public File getFileByDataFormId(String dataFormId) {
        String[] dataFormInfo = dataFormId.split("-");
        if (dataFormInfo.length != 2) {
            throw new DataFormException("dataForm format error");
        }
        File file = FileKit.getFile(this.getDataformDataDirectory().getAbsolutePath(),dataFormInfo[0],dataFormInfo[1] + ".json");
        return file;
    }

    public DataForm getDataFormFrom(URL url) {
        String content = null;
        try {
            content = IOKit.toString(url, Charset.defaultCharset());
        } catch (IOException e) {
            throw new DataFormException("",e);
        }
        DataForm dataForm = JSONKit.jsonToBean(content, DataForm.class);
        return dataForm;
    }

    public DataForm getDataFormFromFile(File file) {
        DataForm dataForm = null;
        if (!file.exists())
            return dataForm;
        try {
            String content = FileKit.readFileToString(file, Charset.defaultCharset());
            dataForm = JSONKit.jsonToBean(content, DataForm.class);
        } catch (IOException e) {
            throw new DataFormException(e.getMessage());
        }
        return dataForm;
    }

    public List<DataForm> getDataForms() {
        String directory = this.getDataformDataDirectory().getAbsolutePath();
        List<DataForm> dataForms = new ArrayList<>();
        File dir = new File(directory);
        if (!dir.exists()) {
            URL url = this.getClass().getClassLoader().getResource(properties.getDataformDataClasspath());
            dir = new File(url.getFile());
        }
        if (!dir.exists())
            return dataForms;
        File[] dirs = dir.listFiles();
        for (int i = 0; i < dirs.length; i++) {
            File[] files = dirs[i].listFiles();
            for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                DataForm dataForm = this.getDataFormFromFile(files[fileIndex]);
                dataForms.add(dataForm);
            }
        }
        return dataForms;
    }

    public String getDataFormIdByJsonFile(File file, String pack) {
        String dataFormCode = file.getName().replace(".json","");
        return StringKit.format("{0}-{1}", pack, dataFormCode);
    }

    public Integer saveDataForm(DataForm dataForm,File jsonFile) {
        Integer result = 1;
        String jsonText = JSONKit.toJsonString(dataForm,true);
        try {
            FileKit.touchFile(jsonFile);
            FileKit.write(jsonFile,jsonText, Charset.defaultCharset(),false);
        } catch (IOException e) {
            throw new DataFormException("写入json文件失败",e);
        }
        return result;
    }
}

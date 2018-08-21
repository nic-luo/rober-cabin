package group.rober.base.dict.service;

import group.rober.base.autoconfigure.BaseProperties;
import group.rober.base.dict.model.DictEntry;
import group.rober.base.dict.model.DictItemEntry;
import group.rober.base.dict.service.impl.DictServiceImpl;
import group.rober.base.dict.service.impl.po.DictItemPO;
import group.rober.base.dict.service.impl.po.DictPO;
import group.rober.runtime.kit.BeanKit;
import group.rober.runtime.kit.FileKit;
import group.rober.runtime.kit.JSONKit;
import group.rober.runtime.kit.ValidateKit;
import group.rober.runtime.lang.BizException;
import group.rober.runtime.lang.RoberException;
import group.rober.sql.core.DataAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DictAdminService {
    @Autowired
    BaseProperties baseProperties;
    @Autowired
    DataAccessor dataAccessor;
    @Autowired
    DictServiceImpl dictService;

    public List<DictEntry> getAllDictList(){
        List<DictEntry> entries = new ArrayList<>();
        List<DictPO> dictPoList = dataAccessor.selectList(DictPO.class,"select * from FOWK_DICT where 1=1 order by SORT_CODE ASC");
        dictPoList.forEach(dictPO->{
            DictEntry entry = new DictEntry();
            BeanKit.copyProperties(dictPO, entry);
            Map<String, DictItemEntry> itemMap = dictService.getDictItemMap(dictPO.getCode());
            entry.setItemMap(itemMap);
            entries.add(entry);
        });

        return entries;
    }

    public File getDictDataDirectory(){
        String devBaseDir = baseProperties.getDevBaseDir();
        String dataFormDataClasspath = baseProperties.getDictDataClasspath();
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

    public void dbTransferFile(File dir){
        List<DictEntry> entries = getAllDictList();
        entries.forEach(entry->{
            File curDir = FileKit.getFile(dir,entry.getCategoryCode());
            if(!curDir.exists())curDir.mkdirs();

            File jsonFile = FileKit.getFile(curDir,entry.getCode()+".json");
            if(jsonFile.exists())jsonFile.delete();
            try {
                FileKit.touchFile(jsonFile);
            } catch (IOException e) {
                throw new RoberException("创建json文件失败",e);
            }

            String jsonText = JSONKit.toJsonString(entry,true);
            try {
                FileKit.write(jsonFile,jsonText, Charset.defaultCharset(),false);
            } catch (IOException e) {
                throw new RoberException("写入json文件失败",e);
            }
        });

    }

    @Transactional
    public Integer fileTransferDB() {
        Integer result = 0;
        this.deleteExistedDic();
        File dir = this.getDictDataDirectory();
        List<DictEntry> dicts = this.getDictEntrys(dir);
        for (DictEntry dictEntry:dicts) {
            DictPO dictPO = this.dictEntryConvertDictPO(dictEntry);
            result += dataAccessor.save(dictPO);
            List<DictItemPO> dictItemPOList = this.getDictItemByDictEntry(dictEntry,dictPO);
            dataAccessor.save(dictItemPOList);
        }
        return result;
    }

    private List<DictItemPO> getDictItemByDictEntry(DictEntry dictEntry, DictPO dictPO) {
        List<DictItemEntry> dictItemEntries = dictEntry.getDictItemEntrys();
        List<DictItemPO> dictItemPOList = dictItemEntries.stream().map(itemEntry -> this.dictItemEntryConvertDictItemPO(itemEntry,dictPO.getCode())).collect(Collectors.toList());
        return dictItemPOList;
    }

    private DictItemPO dictItemEntryConvertDictItemPO(DictItemEntry dictItemEntry, String code) {
        DictItemPO dictItemPO = new DictItemPO();
        BeanKit.copyProperties(dictItemEntry,dictItemPO);
        dictItemPO.setDictCode(code);
        return dictItemPO;
    }

    private DictPO dictEntryConvertDictPO(DictEntry dictEntry) {
        DictPO dictPO = new DictPO();
        BeanKit.copyProperties(dictEntry,dictPO);
        return dictPO;
    }

    private void deleteExistedDic() {
        String dictSql = "DELETE FROM FOWK_DICT";
        dataAccessor.execute(dictSql);
        String dictItemSql = "DELETE FROM FOWK_DICT_ITEM";
        dataAccessor.execute(dictItemSql);
    }

    public List<DictEntry> getDictEntrys(File dir) {
        List<DictEntry> dicts = new ArrayList<>();
        if (!dir.exists())
            return dicts;
        File[] dirs = dir.listFiles();
        for (int i = 0; i < dirs.length; i++) {
            if (!dirs[i].isDirectory())
                continue;
            File[] files = dirs[i].listFiles();
            for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                if ("index.builder.json".equals(files[fileIndex].getName()))
                    continue;
                DictEntry dictEntry = this.getDictFromFile(files[fileIndex]);
                dicts.add(dictEntry);
            }
        }
        return dicts;
    }

    public DictEntry getDictFromFile(File file) {
        DictEntry dict = null;
        if (!file.exists())
            return dict;
        try {
            String content = FileKit.readFileToString(file, Charset.defaultCharset());
            dict = JSONKit.jsonToBean(content, DictEntry.class);
        } catch (IOException e) {
            throw new BizException(e.getMessage());
        }
        return dict;
    }

    public void clearDictCache() {
        dictService.clearCacheAll();
    }
}

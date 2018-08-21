package group.rober.base.dict.service;

import group.rober.base.dict.model.DictEntry;
import group.rober.base.dict.model.DictItemEntry;
import group.rober.base.dict.model.DictItemNode;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface DictService {

    DictEntry getDict(String dictCode);

    DictEntry getDictByFilter(String dictCode, String startSort);

    List<DictItemNode> getDictTree(String dictCode);

    List<DictEntry> getDictList();

    List<DictItemEntry> getDictItemHotspot(String dictCode, int hotspot);

    int save(DictEntry dictEntry);

    int save(String dictCode, DictItemEntry dictItemEntry);

    int delete(String dictCode);

    int delete(String dictCode, String dictItemCode);

    int deleteAll();

    void clearCacheAll();

    void clearCacheItem(String dictCode);
}

package group.rober.base.dict.controller;

import group.rober.base.dict.model.DictEntry;
import group.rober.base.dict.model.DictItemNode;
import group.rober.base.dict.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "DictController", description = "数据字典")
@RestController
@RequestMapping("/base")
public class DictController {
    @Autowired
    private DictService dictService;

    @ApiOperation(value = "取所有的数据字典列表")
    @GetMapping("/dicts")
    public List<DictEntry> getDictList() {
        return dictService.getDictList();
    }

    @ApiOperation(value = "取指定的数据字典对照表")
    @GetMapping("/dicts/{dictCode}")
    public DictEntry getDict(@PathVariable("dictCode") String dictCode) {
        return dictService.getDict(dictCode);
    }

    @ApiOperation(value = "根据排序号取指定的数据字典对照表")
    @GetMapping("/dicts/{dictCode}/sort-filter/{startSort}")
    public DictEntry getDictByFilter(@PathVariable("dictCode") String dictCode,
                                     @PathVariable("startSort") String startSort) {
        return dictService.getDictByFilter(dictCode, startSort);
    }

    @ApiOperation(value = "根据排序号取指定的数据字典对照表,并且以树图形式返回")
    @GetMapping("/dicts/{dictCode}/tree")
    public List<DictItemNode> getDictTree(@PathVariable("dictCode") String dictCode) {
        return dictService.getDictTree(dictCode);
    }


}
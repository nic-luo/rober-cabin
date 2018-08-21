package group.rober.base.param.controller;

import group.rober.base.param.model.ParamEntry;
import group.rober.base.param.model.ParamItemEntry;
import group.rober.base.param.service.ParamService;
import group.rober.runtime.lang.TreeNodeWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "ParamController", description = "数据参数")
@RestController
@RequestMapping("/base")
public class ParamController {
    @Autowired
    private ParamService paramService;


    @ApiOperation(value = "获取指定的数据参数对照表")
    @GetMapping("/params/{paramCode}")
    public ParamEntry getParam(@PathVariable("paramCode") String paramCode) {
        return paramService.getParam(paramCode);
    }

    @ApiOperation(value = "获取指定的数据参数科目树图")
    @GetMapping("/params/itemsTree/{paramCode}")
    public List<TreeNodeWrapper<ParamItemEntry>> getParamItemsAsTree(@PathVariable("paramCode") String paramCode) {
        return paramService.getParamItemsAsTree(paramCode);
    }

    @ApiOperation(value = "根据排序号取指定的数据参数对照表")
    @GetMapping("/params/{paramCode}/items/sort-filter/{startSort}")
    public List<ParamItemEntry> getParamItemsByFilter(@PathVariable("paramCode") String paramCode,
                                                      @PathVariable("startSort") String startSort) {
        return paramService.getParamItemsByFilter(paramCode, startSort);
    }

    @ApiOperation(value = "根据排序号取指定的数据参数科目树图")
    @GetMapping("/params/{paramCode}/itemsTree/sort-filter/{startSort}")
    public List<TreeNodeWrapper<ParamItemEntry>> getParamItemsAsTreeByFilter(@PathVariable("paramCode") String paramCode,
                                                                             @PathVariable("startSort") String startSort) {
        return paramService.getParamItemsAsTreeByFilter(paramCode, startSort);
    }
}
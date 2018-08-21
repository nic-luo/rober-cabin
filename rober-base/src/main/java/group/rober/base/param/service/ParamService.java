package group.rober.base.param.service;

import group.rober.base.param.model.ParamEntry;
import group.rober.base.param.model.ParamItemEntry;
import group.rober.runtime.lang.TreeNodeWrapper;

import java.util.List;

public interface ParamService {

    ParamEntry getParam(String paramCode);

    List<TreeNodeWrapper<ParamItemEntry>> getParamItemsAsTree(String paramCode);

    List<ParamItemEntry> getParamItemsByFilter(String paramCode, String startSort);

    List<TreeNodeWrapper<ParamItemEntry>> getParamItemsAsTreeByFilter(String paramCode, String startSort);

}

package group.rober.dataform.service;

import group.rober.dataform.dto.CloneDataFormBean;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.sql.core.PaginationData;

import java.util.List;
import java.util.Map;

/**
 * Created by luyu on 2018/5/31.
 */
public interface DataFormAdminService {

    List<DataForm> getDataForms();

    DataForm getDataForm(String id);

    DataForm cloneDataForm(CloneDataFormBean cloneDataFormBean);

    DataFormElement getDataFormElementDetail(String dataformId, String code);

    DataForm saveDataForm(DataForm dataForm, String oldDataFormId);

    DataFormElement saveDataFormElement(DataFormElement element, String dataformId);

    void deleteDataForm(String dataFormId);

    List<DataFormElement> parseElementsFromTables(String dataFromId,String... tables);

    void clearCacheAll();

    String dbTransferToJsonFile();

}

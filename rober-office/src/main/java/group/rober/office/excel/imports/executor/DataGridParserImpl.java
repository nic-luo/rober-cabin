package group.rober.office.excel.imports.executor;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

import group.rober.office.excel.imports.ImportExecutorAbstract;
import group.rober.office.excel.imports.config.ExcelImportConfigLoader;
import group.rober.office.excel.imports.exception.ImportExecutorException;
import group.rober.office.excel.reader.DataGridReader;
import group.rober.office.excel.reader.ExcelRowData;
import group.rober.office.excel.reader.ReaderException;
import group.rober.office.excel.utils.ExcelUtils;
import group.rober.runtime.kit.IOKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.MapData;
import group.rober.runtime.lang.ValueObject;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DataGrid风格的数据解析转换
 *
 * @author 杨松syang@amarsoft.com
 * @since 2017年3月21日
 */
public class DataGridParserImpl extends ImportExecutorAbstract<List<MapData>> {

    private String configFile = null;
    private Properties properties =  new Properties();
    
    public DataGridParserImpl(ExcelImportConfigLoader configLoader, String itemName) {
        super(configLoader, itemName);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private List<Map<String, Object>> cleanEmptyRow(List<MapData> rows){
    	List<Map<String, Object>> resultData = new ArrayList<Map<String, Object>>();
    	if(rows!=null&&rows.size()>0){
    		for(Map<String,Object> map:rows){
    			boolean emptyRow = true;
    			for(Map.Entry<String,Object> m:map.entrySet()){
    				Object v = m.getValue();
                    emptyRow = emptyRow && v == null;
                    if(v instanceof String){
                        String sv = (String)v;
                        emptyRow = emptyRow && StringKit.isBlank(sv);
                    }
    			}
    			if(!emptyRow){
    				resultData.add(map);
    			}
    		}
    	}
    	return resultData;
    }


    @Override
    public List<MapData> exec(InputStream dataInputStream, int sheetIdx) throws ImportExecutorException {
        DataGridReader reader = new DataGridReader();
        reader.setImportExecutor(this);
        //初始化处理
        init();

        //读取数据文件
        Workbook workBook=null;
        List<MapData> retList = null;
        try {
            workBook = ExcelUtils.openWorkbook(dataInputStream);
            Sheet sheet = workBook.getSheetAt(sheetIdx);
            List<ExcelRowData> rows = reader.readSheet(sheet,importConfig);
            retList = new ArrayList<>(rows.size());
//            //转成List数组
            for(int i=0;i<rows.size();i++){
                ExcelRowData row = rows.get(i);
                MapData rowData = new MapData();
                rowData.putAll(row.getRowData());
                List<String> nameList = row.getNameList();
                for(int j=0;j<nameList.size();j++){
                    String fieldName = nameList.get(j);
                    ValueObject fieldValue = row.getDataElement(fieldName);
                    rowData.put(fieldName, fieldValue.value());
                }
                retList.add(rowData);
            }
        } catch (IOException e) {
            throw new ImportExecutorException("读取数据文件出错",e);
        } catch (ReaderException e) {
            throw new ImportExecutorException(MessageFormat.format("读取数据文件的第[{0}]个sheet页数据出错",sheetIdx),e);
        } catch (Exception e) {
            throw new ImportExecutorException("未预料的异常",e);
        } finally{
            IOKit.close(workBook);
        }
        cleanEmptyRow(retList);
        return retList;
    }
}

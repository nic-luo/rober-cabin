package group.rober.office.excel.reader;

import group.rober.office.excel.imports.config.ExcelImportConfig;
import org.apache.poi.ss.usermodel.Sheet;


/**
 * 数据读取接口
 *
 * @author syang
 * @since 2014/04/03
 */
public interface ExcelReader<R> {
	/**
	 * 根据配置对象config的指导，读取sheet中的数据
	 *
	 * @param sheet 被读取的sheet页对象
	 * @param config 配置对象
	 * @return R
	 * @throws ReaderException 读取异常
	 */
	public R readSheet(Sheet sheet, ExcelImportConfig config) throws ReaderException;
}

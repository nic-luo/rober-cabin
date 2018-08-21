package group.rober.office.excel.writer;


import group.rober.office.excel.imports.config.ExcelImportConfig;

import java.io.Serializable;
import java.util.Map;


/**
 * Excel数据写入接口
 *
 * @author yangsong
 * @since 2014/04/02
 *
 */
public interface DataWriter<R> {
	/**
	 * 写入数据
	 *
	 * @param data 写入的数据对象
	 * @param config 配置对象
	 * @throws WriterException WriterException
	 */
	void write(R data, ExcelImportConfig config) throws WriterException;
	
	/**
	 * 获取操作结果
	 *
	 * @return WriteResult
	 */
	WriteResult getResult();

	static class WriteResult implements Serializable,Cloneable{
		private int readRows;
		private int writeRows;

		public int getReadRows() {
			return readRows;
		}

		public void setReadRows(int readRows) {
			this.readRows = readRows;
		}

		public int getWriteRows() {
			return writeRows;
		}

		public void setWriteRows(int writeRows) {
			this.writeRows = writeRows;
		}
	}
}

package group.rober.office.word.parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 内嵌表格支持
 *
 * @author yangsong
 * @since 2014年7月31日
 */
public class EmbedTable implements Serializable{

	private static final long serialVersionUID = 2991687962826049652L;

	private List<EmbedTableRow> rowList ;
	private TableStyle tableStyle = new TableStyle();
	
	public EmbedTable(){
		rowList = new ArrayList<EmbedTableRow>();
	}
	
	/**
	 * 构建指定行数据，列数据的表格
	 *
	 * @param rows rows
	 * @param cols cols
	 */
	public EmbedTable(int rows,int cols){
		this();
		for(int i=0;i<rows;i++){
			EmbedTableRow row = createRow();
			for(int j=0;j<cols;j++){
				row.createCell();
			}
		}
	}
	
	
	/**
	 * 根据行列号，查找单元格
	 *
	 * @param rowIndex rowIndex
	 * @param colIndex colIndex
	 * @return EmbedTableCell
	 */
	public EmbedTableCell getCell(int rowIndex,int colIndex){
		if(rowIndex<rowList.size()){
			EmbedTableRow row = rowList.get(rowIndex);
			if(colIndex<row.getCells().size()){
				return row.getCells().get(colIndex);
			}else{
				throw new EmbedTableIndexOutOfRangeException("Total Cells:"+rowList.size()+",ColIndex:"+colIndex);
			}
		}else{
			throw new EmbedTableIndexOutOfRangeException("Total Rows:"+rowList.size()+",RowIndex:"+rowIndex);
		}
	}
	
	/**
	 * 创建一个新行
	 *
	 * @return EmbedTableRow
	 */
	public EmbedTableRow createRow(){
		EmbedTableRow row = new EmbedTableRow(this);
		rowList.add(row);
		return row;
	}


	/**
	 * 获取所有行
	 *
	 * @return list
	 */
	public List<EmbedTableRow> getRows(){
		return rowList;
	}
	
	/**
	 * 获取指定行在表格中的行索引
	 *
	 * @param row row
	 * @return int
	 */
	public int getRowIndex(EmbedTableRow row){
		return rowList.indexOf(row);
	}

	/**
	 * 表格样式
	 *
	 * @return TableStyle
	 */
	public TableStyle getTableStyle() {
		return tableStyle;
	}



	public static class TableStyle implements Serializable{
		private static final long serialVersionUID = 7006651617550055011L;
		
		public enum WidthType{AUTO,DXA};		//表格宽度设置
		private WidthType widthType = WidthType.AUTO;
		private Double width =6.0;
		private Map<Integer,Double> widthSettings = new LinkedHashMap<Integer, Double>();
		private Map<Integer,Double> heightSettings = new LinkedHashMap<Integer, Double>();

		/**
		 * 设置表格指定列的宽度，单位为英寸
		 *
		 * @param colIndex 列索引
		 * @param width 宽度值（单位为英寸）
		 */
		public void setWidth(Integer colIndex,Integer width){
			setWidth(colIndex,(double)width);
		}


		/**
		 * 设置表格指定列的宽度，单位为英寸
		 *
		 * @param colIndex 列索引
		 * @param width 宽度值（单位为英寸）
		 */
		public void setWidth(Integer colIndex,Double width){
			widthType = WidthType.DXA;
			widthSettings.put(colIndex, width);
		}

		/**
		 * 设置表格指定列的宽度，单位为英寸
		 *
		 * @param colIndex 列索引
		 * @param width 宽度值（单位为英寸）
		 */
		public void setWidthAuto(Integer colIndex,Double width){
			widthType = WidthType.AUTO;
			widthSettings.put(colIndex, width);
		}
		/**
		 * 获取表格列的宽度
		 *
		 * @param colIndex 列索引
		 * @return double
		 */
		public Double getWidth(Integer colIndex){
			return widthSettings.get(colIndex);
		}
		
		/**
		 * 设置表格指定列的高度，单位为英寸
		 *
		 * @param rowIndex 行索引
		 * @param height 高度（单位为英寸）
		 */
		public void setHeight(Integer rowIndex,Integer height){
			setHeight(rowIndex,(double)height);
		}
		/**
		 * 设置表格指定列的高度，单位为英寸
		 *
		 * @param rowIndex 行索引
		 * @param height 高度（单位为英寸）
		 */
		public void setHeight(Integer rowIndex,Double height){
			widthType = WidthType.DXA;
			heightSettings.put(rowIndex, height);
		}
		
		/**
		 * 获取表格指定列的高度，单位为英寸
		 *
		 * @param rowIndex rowIndex
		 * @return double
		 */
		public Double getHeight(Integer rowIndex){
			return heightSettings.get(rowIndex);
		}
		/**
		 * 宽度类型
		 *
		 * @return widthType
		 */
		public WidthType getWidthType() {
			return widthType;
		}


		/**
		 * @param widthType 宽度类型
		 */
		public void setWidthType(WidthType widthType) {
			this.widthType = widthType;
		}

		/**
		 * @return double 表格宽度
		 */
		public Double getWidth() {
			return width;
		}

		/**
		 * @param width 表格宽度
		 */
		public void setWidth(Double width) {
			this.width = width;
		}
		
	}
}

package group.rober.office.excel.imports;

import group.rober.office.excel.imports.exception.ImportExecutorException;

import java.io.InputStream;

/**
 * 导入执行器
 *
 * @author 杨松 syang@amarsoft.com
 * @since 2018年5月3日
 */
public interface ImportExecutor<T> {
    T exec(InputStream dataInputStream, int sheetIdx) throws ImportExecutorException;
}

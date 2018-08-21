package group.rober.sql.serialno.generator.impl.ordered;

import group.rober.runtime.holder.ApplicationContextHolder;
import group.rober.runtime.kit.DateKit;
import group.rober.runtime.kit.StringKit;
import group.rober.sql.autoconfigure.SqlProperties;
import group.rober.sql.serialno.cursor.SerialNoCursorDao;
import group.rober.sql.serialno.model.BatchIncreaseBean;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.model.SingleIncreaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by zhulifeng on 17-12-13.
 */
public abstract class AbstractTemplateGenerator {

    @Autowired
    SqlProperties sqlProperties;

    @Transactional
    public String nextSerialNo(String dbGeneratorId, String tplGeneratorId) {
        SerialNoCursorDao cursorDao = getSerialNoCursorDao();
        SingleIncreaseBean increaseBean = new SingleIncreaseBean(dbGeneratorId, new Date());
        cursorDao.increaseSingle(increaseBean);

        String template = getTemplate(tplGeneratorId);
        SerialNoCursor serialNoCursor = getSerialNoCursor(cursorDao, dbGeneratorId, template, 1);
        Long cursor = serialNoCursor.getCursorValue();
        DecimalFormat format = new DecimalFormat(template);
        String formatSerialNo = format.format(cursor);

        return formatSerialNo;
    }

    @Transactional
    public SerialNoCursor increaseBatchAndGetCursor(String dbGeneratorId, String tplGeneratorId,
                                                    int count) {
        SerialNoCursorDao cursorDao = getSerialNoCursorDao();
        BatchIncreaseBean increaseBean = new BatchIncreaseBean(dbGeneratorId, count, new Date());
        cursorDao.increaseBatch(increaseBean);
        String template = getTemplate(tplGeneratorId);
        SerialNoCursor serialNoCursor = getSerialNoCursor(cursorDao, dbGeneratorId, template, count);

        return serialNoCursor;
    }

    private String getTemplate(String generatorId) {
        String template = sqlProperties.getSerialNo().getTemplateMap().get(generatorId);
        if (StringKit.isBlank(template)) {
            template = sqlProperties.getSerialNo().getDefaultTemplate();
        }
        if (StringKit.isBlank(template)) {
            throw new RuntimeException("流水号生成器模板不存在,请先配置!");
        }

        return template;
    }

    private SerialNoCursorDao getSerialNoCursorDao() {
        String cursorRecord = sqlProperties.getSerialNo().getCursorRecord();
        if (StringKit.isBlank(cursorRecord)) {
            throw new RuntimeException("数据存储适配器ID不存在,请先配置!");
        }

        SerialNoCursorDao cursorDao =
                ApplicationContextHolder.getBean(cursorRecord, SerialNoCursorDao.class);
        if (cursorDao == null) {
            throw new RuntimeException("数据存储适配器不存在,请先配置!适配器ID是[" + cursorRecord + "]");
        }
        return cursorDao;
    }

    private SerialNoCursor getSerialNoCursor(SerialNoCursorDao cursorDao, String generatorId,
                                             String template, int count) {
        SerialNoCursor serialNoCursor = cursorDao.findOne(generatorId);
        if (null == serialNoCursor) {
            serialNoCursor = new SerialNoCursor();
            serialNoCursor.setId(generatorId);
            serialNoCursor.setTemplate(template);
            serialNoCursor.setCursorValue(Long.valueOf(count));
            serialNoCursor.setUpdatedTime(DateKit.now());
            try {
                cursorDao.insertOne(serialNoCursor);
            } catch (Exception e) {
                SingleIncreaseBean increaseBean = new SingleIncreaseBean(generatorId, new Date());
                cursorDao.increaseSingle(increaseBean);
                serialNoCursor = cursorDao.findOne(generatorId);
            }
        }
        return serialNoCursor;
    }

}

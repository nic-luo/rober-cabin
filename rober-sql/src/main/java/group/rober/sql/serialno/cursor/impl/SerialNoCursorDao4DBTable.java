package group.rober.sql.serialno.cursor.impl;

import group.rober.runtime.kit.BeanKit;
import group.rober.runtime.kit.MapKit;
import group.rober.sql.core.DataAccessor;
import group.rober.sql.serialno.constants.CursorRecordType;
import group.rober.sql.serialno.cursor.SerialNoCursorDao;
import group.rober.sql.serialno.cursor.impl.pojo.SerialNoCursorPO;
import group.rober.sql.serialno.model.BatchIncreaseBean;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.model.SingleIncreaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(CursorRecordType.DB_TABLE)
public class SerialNoCursorDao4DBTable implements SerialNoCursorDao {

    @Autowired
    DataAccessor accessor;

    public SerialNoCursor findOne(String generatorId) {
        SerialNoCursor serialNoCursor = null;
        SerialNoCursorPO serialNoCursorPO = accessor.selectOneById(SerialNoCursorPO.class, generatorId);
        if (null != serialNoCursorPO) {
            serialNoCursor = new SerialNoCursor();
            BeanKit.copyProperties(serialNoCursorPO, serialNoCursor);
        }

        return serialNoCursor;
    }

    public void insertOne(SerialNoCursor serialNoCursor) {
        SerialNoCursorPO serialNoCursorPO = new SerialNoCursorPO();
        BeanKit.copyProperties(serialNoCursor, serialNoCursorPO);
        accessor.save(serialNoCursorPO);
    }

    public void increaseSingle(SingleIncreaseBean increaseBean) {
        String sql = "update FOWK_SERIAL_NUMBER set CURSOR_VALUE=CURSOR_VALUE+1 where ID=:id";
        accessor.execute(sql, MapKit.mapOf("id", increaseBean.getGeneratorId()));
    }

    public void increaseBatch(BatchIncreaseBean increaseBean) {
        String sql = "update FOWK_SERIAL_NUMBER set CURSOR_VALUE=CURSOR_VALUE+:count where ID=:id";
        accessor.execute(sql, MapKit.mapOf("count", increaseBean.getCount(), "id", increaseBean.getGeneratorId()));
    }

    public void lock(String generatorId) {

    }

    public void unlock(String generatorId) {

    }
}

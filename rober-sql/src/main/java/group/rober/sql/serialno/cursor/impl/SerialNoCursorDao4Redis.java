package group.rober.sql.serialno.cursor.impl;

import group.rober.sql.serialno.constants.CursorRecordType;
import group.rober.sql.serialno.cursor.SerialNoCursorDao;
import group.rober.sql.serialno.model.BatchIncreaseBean;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.model.SingleIncreaseBean;
import org.springframework.stereotype.Component;

@Component(CursorRecordType.REDIS)
public class SerialNoCursorDao4Redis implements SerialNoCursorDao {

    @Override
    public SerialNoCursor findOne(String generatorId) {
        return null;
    }

    @Override
    public void insertOne(SerialNoCursor serialNoCursor) {

    }

    @Override
    public void increaseSingle(SingleIncreaseBean increaseBean) {

    }

    @Override
    public void increaseBatch(BatchIncreaseBean increaseBean) {

    }

    @Override
    public void lock(String generatorId) {

    }

    @Override
    public void unlock(String generatorId) {

    }
}

package group.rober.sql.serialno.cursor.impl;

import group.rober.sql.serialno.constants.CursorRecordType;
import group.rober.sql.serialno.cursor.SerialNoCursorDao;
import group.rober.sql.serialno.cursor.mapper.SerialNoCursorMapper;
import group.rober.sql.serialno.model.BatchIncreaseBean;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.model.SingleIncreaseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(CursorRecordType.MYBATIS)
public class SerialNoCursorDao4Mybatis implements SerialNoCursorDao {

    @Autowired(required = false)
    SerialNoCursorMapper serialNoCursorMapper;

    @Override
    public SerialNoCursor findOne(String generatorId) {
        return serialNoCursorMapper.findOne(generatorId);
    }

    @Override
    public void insertOne(SerialNoCursor serialNoCursor) {
        serialNoCursorMapper.insertOne(serialNoCursor);
    }

    @Override
    public void increaseSingle(SingleIncreaseBean increaseBean) {
        serialNoCursorMapper.increaseSingle(increaseBean);
    }

    @Override
    public void increaseBatch(BatchIncreaseBean increaseBean) {
        serialNoCursorMapper.increaseBatch(increaseBean);
    }

    @Override
    public void lock(String generatorId) {

    }

    @Override
    public void unlock(String generatorId) {

    }
}

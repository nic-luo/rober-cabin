package group.rober.sql.serialno.cursor;

import group.rober.sql.serialno.model.BatchIncreaseBean;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.model.SingleIncreaseBean;

public interface SerialNoCursorDao {

    SerialNoCursor findOne(String generatorId);

    void insertOne(SerialNoCursor serialNoCursor);

    void increaseSingle(SingleIncreaseBean increaseBean);

    void increaseBatch(BatchIncreaseBean increaseBean);

    void lock(String generatorId);

    void unlock(String generatorId);
}

package group.rober.sql.serialno.cursor.mapper;

import group.rober.sql.serialno.model.BatchIncreaseBean;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.model.SingleIncreaseBean;

/**
 * Created by zhulifeng on 17-12-6.
 */
public interface SerialNoCursorMapper {

    SerialNoCursor findOne(String id);

    void insertOne(SerialNoCursor serialNo);

    void increaseSingle(SingleIncreaseBean increaseBean);

    void increaseBatch(BatchIncreaseBean increaseBean);
}

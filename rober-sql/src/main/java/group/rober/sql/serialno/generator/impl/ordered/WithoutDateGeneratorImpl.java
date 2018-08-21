package group.rober.sql.serialno.generator.impl.ordered;

import group.rober.sql.serialno.constants.GeneratorType;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.generator.SerialNoFormatter;
import group.rober.sql.serialno.generator.SerialNoGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

/**
 * 默认流水号生成器
 * Created by zhulifeng on 17-12-7.
 */
@Component(GeneratorType.DEFAULT)
public class WithoutDateGeneratorImpl extends AbstractTemplateGenerator
        implements SerialNoGenerator<String> {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String next(String generatorId) {
        return nextSerialNo(generatorId, generatorId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String next(String generatorId, Object object, SerialNoFormatter formatter) {
        String serialNo = next(generatorId);
        if (null != formatter) {
            serialNo = formatter.formatter(serialNo, object);
        }

        return serialNo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String[] nextBatch(String generatorId, int count) {
        SerialNoCursor serialNoCursor = increaseBatchAndGetCursor(generatorId, generatorId, count);
        Long cursor = serialNoCursor.getCursorValue();
        String[] serialNos = new String[count];
        DecimalFormat decimalFormat = new DecimalFormat(serialNoCursor.getTemplate());
        int startSeq = cursor.intValue() - count < 0 ? 0 : cursor.intValue() - count;
        for (int i = 0; i < count; i++) {
            String formatSerialNo = decimalFormat.format(startSeq + i + 1);
            serialNos[i] = formatSerialNo;
        }
        return serialNos;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String[] nextBatch(String generatorId, int count, Object object,
                              SerialNoFormatter formatter) {
        if (null == formatter) {
            return nextBatch(generatorId, count);
        }

        SerialNoCursor serialNoCursor = increaseBatchAndGetCursor(generatorId, generatorId, count);
        Long cursor = serialNoCursor.getCursorValue();
        String[] serialNos = new String[count];
        DecimalFormat decimalFormat = new DecimalFormat(serialNoCursor.getTemplate());
        int startSeq = cursor.intValue() - count < 0 ? 0 : cursor.intValue() - count;
        for (int i = 0; i < count; i++) {
            String formatSerialNo = decimalFormat.format(startSeq + i + 1);
            formatSerialNo = formatter.formatter(formatSerialNo, object);
            serialNos[i] = formatSerialNo;
        }

        return serialNos;
    }
}

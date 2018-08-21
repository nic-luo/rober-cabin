package group.rober.sql.serialno.generator.impl.ordered;

import group.rober.runtime.kit.DateKit;
import group.rober.sql.serialno.constants.DateFormat;
import group.rober.sql.serialno.model.SerialNoCursor;
import group.rober.sql.serialno.generator.SerialNoFormatter;
import group.rober.sql.serialno.generator.WithinDateGenerator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

/**
 * Created by zhulifeng on 17-12-13.
 */
public abstract class AbstractWithinDateGeneratorImpl extends AbstractTemplateGenerator
    implements WithinDateGenerator {

    private static final String DATE_SYMBOL = "${DATE}";

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String next(String generatorId) {
        String dbGeneratorId = appendDateToGeneratorId(generatorId);
        String formatSerialNo = nextSerialNo(dbGeneratorId, generatorId);
        formatSerialNo = formatSerialNo.replace(DATE_SYMBOL, getCurFormattedDate());

        return formatSerialNo;
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
        String dbGeneratorId = appendDateToGeneratorId(generatorId);
        SerialNoCursor serialNoCursor =
            increaseBatchAndGetCursor(dbGeneratorId, generatorId, count);
        Long cursor = serialNoCursor.getCursorValue();
        String[] serialNos = new String[count];
        DecimalFormat decimalFormat = new DecimalFormat(serialNoCursor.getTemplate());
        int startSeq = cursor.intValue() - count < 0 ? 0 : cursor.intValue() - count;
        for (int i = 0; i < count; i++) {
            String formatSerialNo = decimalFormat.format(startSeq + i + 1);
            formatSerialNo = formatSerialNo.replace(DATE_SYMBOL, getCurFormattedDate());
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

        String dbGeneratorId = appendDateToGeneratorId(generatorId);
        SerialNoCursor serialNoCursor =
            increaseBatchAndGetCursor(dbGeneratorId, generatorId, count);
        Long cursor = serialNoCursor.getCursorValue();
        String[] serialNos = new String[count];
        DecimalFormat decimalFormat = new DecimalFormat(serialNoCursor.getTemplate());
        int startSeq = cursor.intValue() - count < 0 ? 0 : cursor.intValue() - count;
        for (int i = 0; i < count; i++) {
            String formatSerialNo = decimalFormat.format(startSeq + i + 1);
            formatSerialNo = formatter.formatter(formatSerialNo, object);
            formatSerialNo = formatSerialNo.replace(DATE_SYMBOL, getCurFormattedDate());
            serialNos[i] = formatSerialNo;
        }

        return serialNos;
    }

    @Override
    public String getDateFormat() {
        return DateFormat.YMD.desc();
    }

    @Override
    public String appendDateToGeneratorId(String generatorId) {
        return generatorId;
    }


    private String getCurFormattedDate() {
        return DateKit.format(DateKit.now(), getDateFormat());
    }

}

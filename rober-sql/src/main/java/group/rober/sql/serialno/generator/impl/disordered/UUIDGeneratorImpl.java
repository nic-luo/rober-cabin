package group.rober.sql.serialno.generator.impl.disordered;

import group.rober.sql.serialno.constants.GeneratorType;
import group.rober.sql.serialno.generator.SerialNoFormatter;
import group.rober.sql.serialno.generator.SerialNoGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by zhulifeng on 17-12-7.
 */
@Component(GeneratorType.UUID)
public class UUIDGeneratorImpl implements SerialNoGenerator<String> {

    @Override
    public String next(String generatorId) {
        UUID uuid = UUID.randomUUID();
        String serialNo = uuid.toString().replaceAll("-", "");

        return serialNo;
    }

    @Override
    public String next(String generatorId, Object object, SerialNoFormatter formatter) {
        return next(generatorId);
    }

    @Override
    public String[] nextBatch(String generatorId, int count) {
        String[] serialNos = new String[count];
        for (int i = 0; i < count; i++) {
            serialNos[i] = next(generatorId);
        }

        return serialNos;
    }

    @Override
    public String[] nextBatch(String generatorId, int count, Object object,
        SerialNoFormatter formatter) {
        return nextBatch(generatorId, count);
    }
}

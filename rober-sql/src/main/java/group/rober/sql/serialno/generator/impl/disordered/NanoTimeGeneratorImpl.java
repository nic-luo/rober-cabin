package group.rober.sql.serialno.generator.impl.disordered;

import group.rober.sql.serialno.constants.GeneratorType;
import group.rober.sql.serialno.generator.SerialNoFormatter;
import group.rober.sql.serialno.generator.SerialNoGenerator;
import org.springframework.stereotype.Component;

/**
 * Created by zhulifeng on 17-12-11.
 */
@Component(GeneratorType.NANO_TIME)
public class NanoTimeGeneratorImpl implements SerialNoGenerator<String> {

    @Override
    public String next(String generatorId) {
        return next(generatorId);
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

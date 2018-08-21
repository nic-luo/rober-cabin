package group.rober.sql.serialno.generator;


/**
 * Created by zhulifeng on 17-12-7.
 */
public interface WithinDateGenerator extends SerialNoGenerator<String> {

    String getDateFormat();

    String appendDateToGeneratorId(String generatorId);
}

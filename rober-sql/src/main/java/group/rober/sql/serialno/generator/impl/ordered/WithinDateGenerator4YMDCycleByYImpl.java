package group.rober.sql.serialno.generator.impl.ordered;

import group.rober.runtime.kit.DateKit;
import group.rober.sql.serialno.constants.DateFormat;
import group.rober.sql.serialno.constants.GeneratorType;
import org.springframework.stereotype.Component;

/**
 * Created by zhulifeng on 17-12-13.
 */
@Component(GeneratorType.WITH_DATE_YMD_CYCLE_BY_Y)
public class WithinDateGenerator4YMDCycleByYImpl extends AbstractWithinDateGeneratorImpl {

    @Override
    public String getDateFormat() {
        return DateFormat.YMD.desc();
    }

    @Override
    public String appendDateToGeneratorId(String generatorId) {
        return generatorId + "-" + DateKit.format(DateKit.now(), DateFormat.Y.desc());
    }
}

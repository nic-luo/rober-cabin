package group.rober.sql.serialno.generator.impl.ordered;

import group.rober.runtime.kit.DateKit;
import group.rober.sql.serialno.constants.DateFormat;
import group.rober.sql.serialno.constants.GeneratorType;
import org.springframework.stereotype.Component;

/**
 * Created by zhulifeng on 17-12-13.
 */
@Component(GeneratorType.WITH_DATE_YM_CYCLE_BY_YM)
public class WithinDateGenerator4YMCycleByYMImpl extends AbstractWithinDateGeneratorImpl {

    @Override
    public String getDateFormat() {
        return DateFormat.YM.desc();
    }

    @Override
    public String appendDateToGeneratorId(String generatorId) {
        return generatorId + "-" + DateKit.format(DateKit.now(), DateFormat.YM.desc());
    }
}

package group.rober.sql.serialno.finder.impl;

import group.rober.runtime.holder.ApplicationContextHolder;
import group.rober.runtime.kit.StringKit;
import group.rober.sql.autoconfigure.SqlProperties;
import group.rober.sql.serialno.finder.SerialNoGeneratorFinder;
import group.rober.sql.serialno.generator.SerialNoGenerator;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerialKeyGeneratorFinderImpl implements SerialNoGeneratorFinder {

    private static final String GENERATOR_VALIDATOR_TEMPLATE =
        "流水号生成器[{0}]对应的实现Bean[{1}]不存在，并且默认的流水号生成器[{2}]也不可用";

    private SqlProperties sqlProperties;

    public SqlProperties getSqlProperties() {
        return sqlProperties;
    }

    @Autowired
    public void setSqlProperties(SqlProperties sqlProperties) {
        this.sqlProperties = sqlProperties;
    }

    public SerialNoGenerator find(String generatorId) {
        String beanName = sqlProperties.getSerialNo().getGeneratorMap().get(generatorId);
        SerialNoGenerator generator = null;
        if (StringKit.isNotBlank(beanName)) {
            generator = ApplicationContextHolder.getBean(beanName, SerialNoGenerator.class);
        }
        String defaultGenerator = sqlProperties.getSerialNo().getDefaultGenerator();
        if (generator == null) {
            generator = ApplicationContextHolder.getBean(defaultGenerator, SerialNoGenerator.class);
        }

        Validate.notNull(generator, GENERATOR_VALIDATOR_TEMPLATE, generatorId, beanName,
            defaultGenerator);

        return generator;
    }

}

package group.rober.sql.serialno.finder;


import group.rober.sql.serialno.generator.SerialNoGenerator;

/**
 * 流水号生成器实现类查找接口
 */
public interface SerialNoGeneratorFinder {
    SerialNoGenerator find(String generatorId);
}

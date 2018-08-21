package group.rober.sql.serialno.generator;


public interface SerialNoGenerator<T> {
    T next(String generatorId);

    T next(String generatorId, Object object, SerialNoFormatter formatter);

    T[] nextBatch(String generatorId, int count);

    T[] nextBatch(String generatorId, int count, Object object, SerialNoFormatter formatter);
}

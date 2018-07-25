package group.rober.runtime.kit;

import org.apache.commons.lang3.ArrayUtils;

public abstract class ArrayKit {
    public static <T> T[] concat(final T[] array1, final T... array2) {
        return ArrayUtils.addAll(array1,array2);
    }
}

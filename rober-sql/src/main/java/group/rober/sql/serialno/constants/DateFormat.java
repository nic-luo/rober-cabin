package group.rober.sql.serialno.constants;

/**
 * Created by zhulifeng on 17-12-12.
 */
public enum DateFormat {

    YMD("yyyyMMdd"), YM("yyyyMM"), Y("yyyy");

    String value;

    DateFormat(String value) {
        this.value = value;
    }

    public String desc() {
        return value;
    }
}

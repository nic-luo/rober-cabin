package group.rober.runtime.kit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;

/**
 * 数字工具类
 * Created by cytsir on 17/2/16.
 */
public abstract class NumberKit {
    /**
     * 除法计算时，的精度
     */
    public static final int DIVIDE_SCALE = 128;
    /**
     * 小数保留精度，四舍五入
     *
     * @param value 小数值
     * @param scale 小数精度
     * @return double
     */
    public static double round(Number value,int scale){
        return BigDecimal.valueOf(value.doubleValue()).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 小数保留精度，向上取整
     *
     * @param value value
     * @param scale scale
     * @return number
     */
    public static Number ceil(Number value,int scale){
        return BigDecimal.valueOf(value.doubleValue()).setScale(scale, RoundingMode.UP);
    }

    /**
     * 数保留精度，向下取整
     *
     * @param value value
     * @param scale scale
     * @return double
     */
    public static double floor(Number value,int scale){
        return BigDecimal.valueOf(value.doubleValue()).setScale(scale, RoundingMode.DOWN).doubleValue();
    }

    public static String uuid(){
        UUID uuid = UUID.randomUUID();
        String uuidValue = uuid.toString();
        uuidValue = uuidValue.toUpperCase();
        return uuidValue;
    }

    public static Number divide(Number v1,Number v2){
        return BigDecimal.valueOf(v1.doubleValue()).divide(BigDecimal.valueOf(v2.doubleValue()),DIVIDE_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 任意整数转为36进制的字符串
     *
     * @param i i
     * @return string
     */
    public static String convert36Radix(int i){
        return Integer.toString(i,36).toUpperCase();
    }
    /**
     * 任意整数转为36进制的字符串
     *
     * @param i i
     * @return string
     */
    public static String convert36Radix(long i){
        return Long.toString(i,36).toUpperCase();
    }

    /**
     * 当前时间的纳秒值，以36进制表示
     *
     * @return string
     */
    public static String nanoTime36(){
        return convert36Radix(System.nanoTime());
    }
    /**
     * 时间新纳秒值+随机数
     *
     * @param randomCount 随机数个数
     * @return string
     */
    public static String nanoTimeRandom36(int randomCount){
        StringBuffer sv = new StringBuffer(convert36Radix(System.nanoTime()));
        Random random = new Random();
        String rv = convert36Radix(random.nextInt(randomCount*36));
        return sv.append(rv).toString();
    }
}

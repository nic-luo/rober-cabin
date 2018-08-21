package group.rober.sql.jdbc.runner;

class DateTimeConverter {
    /**
     * 把毫秒转换成小时+分+秒的形式
     *
     * @param timeMillis timeMillis
     * @return String
     */
    public static String longSecond2HMS(long timeMillis){
        StringBuffer hms = new StringBuffer();//"{0}时{1}分{2}秒{3}毫秒";
        long totalSec = (long)timeMillis/1000;
        long totalMin = (long)totalSec/60;
        long totalHour = (long)totalMin/60;

        long millis = timeMillis%1000;
        long sec = totalSec%60;
        long min = totalMin%60;
        long hour = totalHour;

        if(hour>0)hms.append(hour).append("小时");
        if(hour>0||min>0)hms.append(min).append("分");
        if(hour>0||min>0||sec>0)hms.append(sec).append("秒");
        hms.append(millis).append("毫秒");

        return hms.toString();
    }
}

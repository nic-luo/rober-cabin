package group.rober.runtime.kit;

import group.rober.runtime.lang.BasicConstant;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by tisir yangsong158@qq.com on 2017-04-06
 */
public abstract class StringKit extends StringUtils {
    /**
     * 空串
     *
     * @param s  字串
     * @param s1 字串为空时，返回的字串
     * @return string
     */
    public static String nvl(Object s, String s1) {
        String ss = "";
        if (s instanceof String) {
            ss = (String) s;
        } else if (s == null) {
            return s1;
        } else {
            ss = String.valueOf(s);
        }

        if (StringUtils.isEmpty(ss)) return s1;
        return ss;
    }

//    /**
//     * 连接字串
//     * @param parts
//     * @param delimiter
//     * @return
//     */
//    public static String join(Iterable<? extends CharSequence> elements, String delimiter){
////        return Joiner.on(delimiter).join(parts);
//        String.join(delimiter,parts);
//    }
//
//    /**
//     * 连接MAP类型的字串
//     * @param map
//     * @param keyValueSeparator
//     * @param delimiter
//     * @return
//     */
//    public static String join(Map<?, ?> map, String keyValueSeparator, String delimiter){
//        Joiner.MapJoiner joiner = Joiner.on(delimiter).withKeyValueSeparator(keyValueSeparator);
//        return joiner.join(map);
//    }

    /**
     * 连接字串
     *
     * @param strList strList
     * @param delimiter delimiter
     * @return string
     */
    public static String join(String[] strList, String delimiter) {
        return join(Arrays.asList(strList), delimiter);
    }

    /**
     *
     * @param elements elements
     * @return string
     */
    public static String join(final CharSequence... elements) {
        return join8(elements, "");
    }

    /**
     * JAVA8的字串连接
     *
     * @param array array
     * @param separator separator
     * @return string
     */
    public static String join8(final CharSequence[] array, final String separator) {
        StringJoiner joiner = new StringJoiner(separator);
        for (CharSequence object : array) {
            joiner.add(object);
        }
        return joiner.toString();
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String ltrim(String str) {
        return str == null ? null : str.replaceAll("^\\\\s+", "");
    }

    public static String rtrim(String str) {
        return str == null ? null : str.replaceAll("\\\\s+$", "");
    }

    public static String clearSpace(String str) {
        return str == null ? null : str.replaceAll("\\\\s+", "");
    }

    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    // ==========================================================================
    // Perl风格的chomp和chop函数。
    // ==========================================================================

    /**
     * 删除字符串末尾的换行符。如果字符串不以换行结尾，则什么也不做。
     * <p>
     * 换行符有三种情形：&quot;<code>\n</code>&quot;、&quot;<code>\r</code>&quot;、&quot;
     * <code>\r\n</code>&quot;。
     * </p>
     * <pre>
     * chomp(null)          = null
     * chomp("")            = ""
     * chomp("abc \r")      = "abc "
     * chomp("abc\n")       = "abc"
     * chomp("abc\r\n")     = "abc"
     * chomp("abc\r\n\r\n") = "abc\r\n"
     * chomp("abc\n\r")     = "abc\n"
     * chomp("abc\n\rabc")  = "abc\n\rabc"
     * chomp("\r")          = ""
     * chomp("\n")          = ""
     * chomp("\r\n")        = ""
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 不以换行结尾的字符串，如果原始字串为<code>null</code>，则返回<code>null</code>
     */
    public static String chomp(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        if (str.length() == 1) {
            char ch = str.charAt(0);

            if (ch == '\r' || ch == '\n') {
                return BasicConstant.EMPTY_STRING;
            } else {
                return str;
            }
        }

        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last == '\r') {
        } else {
            lastIdx++;
        }

        return str.substring(0, lastIdx);
    }

//    /**
//     * 删除字符串末尾的指定字符串。如果字符串不以该字符串结尾，则什么也不做。
//     * <p/>
//     * <pre>
//     * chomp(null, *)         = null
//     * chomp("", *)           = ""
//     * chomp("foobar", "bar") = "foo"
//     * chomp("foobar", "baz") = "foobar"
//     * chomp("foo", "foo")    = ""
//     * chomp("foo ", "foo")   = "foo "
//     * chomp(" foo", "foo")   = " "
//     * chomp("foo", "foooo")  = "foo"
//     * chomp("foo", "")       = "foo"
//     * chomp("foo", null)     = "foo"
//     * </pre>
//     *
//     * @param str       要处理的字符串
//     * @param separator 要删除的字符串
//     * @return 不以指定字符串结尾的字符串，如果原始字串为<code>null</code>，则返回<code>null</code>
//     */
//    public static String chomp(String str, String separator) {
//        if (str == null || str.length() == 0 || separator == null) {
//            return str;
//        }
//
//        if (str.endsWith(separator)) {
//            return str.substring(0, str.length() - separator.length());
//        }
//
//        return str;
//    }

    /**
     * 删除最后一个字符。
     * <p>
     * 如果字符串以<code>\r\n</code>结尾，则同时删除它们。
     * </p>
     * <pre>
     * chop(null)          = null
     * chop("")            = ""
     * chop("abc \r")      = "abc "
     * chop("abc\n")       = "abc"
     * chop("abc\r\n")     = "abc"
     * chop("abc")         = "ab"
     * chop("abc\nabc")    = "abc\nab"
     * chop("a")           = ""
     * chop("\r")          = ""
     * chop("\n")          = ""
     * chop("\r\n")        = ""
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 删除最后一个字符的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String chop(String str) {
        if (str == null) {
            return null;
        }

        int strLen = str.length();

        if (strLen < 2) {
            return BasicConstant.EMPTY_STRING;
        }

        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (ret.charAt(lastIdx - 1) == '\r') {
                return ret.substring(0, lastIdx - 1);
            }
        }

        return ret;
    }

    public static String format(String tpl, Object... args) {
        return MessageFormat.format(tpl, args);
    }

    /**
     * 把驼峰转为下划线，并且全大写,例如:PersonName转为PERSON_NAME
     *
     * @param property property
     * @return string
     */
    public static String camelToUnderline(String property) {
//        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str);

//        String[] strs = splitByCharacterTypeCamelCase(str);
//        List<String> strArray = Arrays
//                .stream(strs)
//                .map(s -> s.toUpperCase())
//                .collect(Collectors.toList());
//        return join(strArray,"_");

        if (StringKit.isBlank(property)) return property;

        char[] chars = property.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            if (CharUtils.isAsciiAlphaUpper(c)) {
                sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
            } else {
                sb.append(c);
            }
        }
        String ret = sb.toString();

        //处理可能出现的多个下划线
        ret = ret.replaceAll("_+", "_");
        ret = removeStart(ret, "_");
        ret = removeEnd(ret, "_");
        ret = ret.toUpperCase();

        return ret;

    }

    /**
     * 把下划线转为驼峰,例如:PERSON_NAME转为PersonName
     *
     * @param str str
     * @return string
     */
    public static String underlineToCamel(String str) {
//        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
//        List<String> strArray = Arrays
//                .stream(strs)
//                .map(s -> lowerCase(s))
//                .map(s -> capitalize(s))
//                .collect(Collectors.toList());
        if (StringKit.isBlank(str)) return str;

        String[] strs = split(str, "_");
        for (int i = 0; i < strs.length; i++) {
            strs[i] = lowerCase(strs[i]);
            if (i > 0) {
                strs[i] = capitalize(strs[i]);
            }
        }
        return join(strs);
    }


    /**
     * 使用变量填充字串模板
     *
     * @param tpl tpl
     * @param vars vars
     * @return string
     */
    public static String fillTpl(String tpl, Map<String, String> vars) {
        Set<Map.Entry<String, String>> sets = vars.entrySet();
        for (Map.Entry<String, String> entry : sets) {
            String regex = "\\$\\{" + entry.getKey() + "\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(tpl);
            tpl = matcher.replaceAll(entry.getValue());
        }
        return tpl;
    }

    public static String uuid() {
        return uuid("");
    }

    public static String uuid(String delimiter) {
        String uuid = UUID.randomUUID().toString();	//获取UUID并转化为String对象
        uuid = uuid.replace("-", delimiter);				//因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        return uuid;
    }


    /**
     * 由于该类继承于SringUtils，而父类中，有leftPad方法
     *
     * @param str str
     * @param size size
     * @param padStr padStr
     * @return string
     */
//    public static String leftPad(String str, int size, String padStr) {
//        if(str == null) {
//            return null;
//        } else {
//            if(isEmpty(padStr)) {
//                padStr = " ";
//            }
//
//            int padLen = padStr.length();
//            int strLen = str.length();
//            int pads = size - strLen;
//            if(pads <= 0) {
//                return str;
//            } else if(padLen == 1 && pads <= 8192) {
//                return leftPad(str, size, padStr.charAt(0));
//            } else if(pads == padLen) {
//                return padStr.concat(str);
//            } else if(pads < padLen) {
//                return padStr.substring(0, pads).concat(str);
//            } else {
//                char[] padding = new char[pads];
//                char[] padChars = padStr.toCharArray();
//
//                for(int i = 0; i < pads; ++i) {
//                    padding[i] = padChars[i % padLen];
//                }
//
//                return (new String(padding)).concat(str);
//            }
//        }
//    }

}

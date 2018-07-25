package group.rober.runtime.holder;

import group.rober.runtime.kit.ValidateKit;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 国际化消息处理快速API提供
 */
public class MessageHolder {
    public static MessageSource getMessageSource(){
        MessageSource messageSource = ApplicationContextHolder.getBean(MessageSource.class);
        ValidateKit.notNull(messageSource);
        return messageSource;
    }


    /**
     *
     * @param defaultMessage : 没有设置key的时候的默认值.
     * @param code ：对应messages配置的key.
     * @param args : 数组参数.
     * @return string
     */
    public static String getMessage(String defaultMessage,String code,Object... args){
        //这里使用比较方便的方法，不依赖request.
        Locale locale = LocaleContextHolder.getLocale();
        return getMessageSource().getMessage(code, args, defaultMessage, locale);
    }
}

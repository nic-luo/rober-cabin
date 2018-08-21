package group.rober.base.tags;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import group.rober.runtime.kit.HtmlKit;

import java.util.List;

/**
 * 文本简短介绍，过滤掉标记标签，如果HTML等
 */
public class BriefPlainTextMethodModel implements TemplateMethodModelEx {
    public Object exec(List arguments) throws TemplateModelException {
        String result = "";
        if ((null != arguments) && (arguments.size() > 0)) {
            if (arguments.size() != 2) {
                throw new TemplateModelException("Wrong arguments");
            }
            String str = arguments.get(0).toString();
            Integer length = Integer.parseInt(arguments.get(1).toString());
            if (null != str) {
                result = HtmlKit.filterHtml(str);
                if(result.length()>length){
                    result = result.substring(0,length);
                }
            }
        }
        return result;
    }

}

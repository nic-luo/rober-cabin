package group.rober.dataform.tags;

import freemarker.core.Environment;
import freemarker.template.*;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.service.DataFormService;
import group.rober.runtime.holder.ApplicationContextHolder;
import group.rober.runtime.kit.ValidateKit;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * 详情表单标签
 */
public class DetailInfoTag extends DataFormTemplateDirective implements TemplateDirectiveModel {




    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        Writer writer = env.getOut();
        DataFormService dataFormService = ApplicationContextHolder.getBean(DataFormService.class);


        //将模版里的数字参数转化成int类型的方法，，其它类型的转换请看freemarker文档
        String id = getParamAsStr(params,"id");
        String bind = getParamAsStr(params,"bind");
        ValidateKit.notNull(id,"参数id未设置");
        ValidateKit.notNull(bind,"参数bind未设置");
        DataForm dataForm = dataFormService.getDataForm(bind);
//        ValidateKit.notNull(dataForm,"dataform={0}未找到",use);

        renderDataForm(dataForm,writer);

        if (body != null) {
            body.render(env.getOut());
        }
    }

    protected void renderDataForm(DataForm dataForm,Writer writer) throws IOException {
//        writer.write("输出DataForm-info");
//        Template template = getConfiguration().getTemplate("static.html");
//        writer.write("<@text id=\"idx\" name=\"个人编号x\" required=\"true\"/>");
    }
}

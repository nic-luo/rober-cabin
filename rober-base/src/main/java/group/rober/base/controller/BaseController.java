package group.rober.base.controller;

import group.rober.runtime.autoconfigure.ResourceBundleMessageAutoConfiguration;
import group.rober.runtime.kit.StringKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Api(value = "RoberApi-Base", description = "基础应用")
@Controller
@RequestMapping("/base")
public class BaseController {
    protected Logger logger = LoggerFactory.getLogger(BaseController.class);
    private static String redirectPrefix = "/base/redirect/";

    @Autowired
    private ResourceBundleMessageAutoConfiguration resourceBundleMessage;

    @ApiOperation(value = "页面服务端跳转")
    @RequestMapping(path = "/redirect/**",method = {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView redirect(HttpServletRequest request){
        String servletPath = request.getServletPath();
        if(StringKit.isNotBlank(servletPath)&&servletPath.startsWith(redirectPrefix)){
            String viewName = servletPath.substring(redirectPrefix.length());
            logger.debug("redirect:"+servletPath+"->"+viewName);
            Map<String,Object> vars = new HashMap<String,Object>();
            return new ModelAndView(viewName,vars);
        }
        return null;
    }
    @ApiOperation(value = "系统全局变量")
    @RequestMapping(path = "/profile.js",method = RequestMethod.GET)
    public ModelAndView profile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type","application/javascript");
        ModelAndView mv = new ModelAndView("global-profile");
        return mv;
    }



    @ApiOperation(value = "取特定语言环境的国际化消息")
    @RequestMapping(path = "/i18n",method = {RequestMethod.GET,RequestMethod.POST},produces="application/json")
    @ResponseBody
    public Map<String,String> getI18nProperties(HttpServletRequest request, HttpServletResponse response){
        String language= StringKit.nvl(request.getParameter("language"),"zh");
        String country= StringKit.nvl(request.getParameter("country"),"CN");
        LocaleContextHolder.setLocale(new Locale(language,country));
        Map<String,String> properties = resourceBundleMessage.getProperties();
        return properties;
    }
}

package group.rober.base.controller;

import io.swagger.annotations.Api;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tisir yangsong158@qq.com on 2017-05-01
 */
@Controller
public class PingTemplateEngineController {

    @RequestMapping("/ping/freemarker")
    public ModelAndView freemarker() {
        Map<String,Object> vars = new HashMap<String,Object>();

        vars.put("javaVersion", System.getProperty("java.version"));
        vars.put("springVersion", SpringVersion.getVersion());
        vars.put("springBootVersion", SpringBootVersion.getVersion());


        return new ModelAndView("templates/base/macro/ping/freemarker",vars);
    }

}

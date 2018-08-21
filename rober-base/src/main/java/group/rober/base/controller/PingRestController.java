package group.rober.base.controller;

import group.rober.runtime.kit.DateKit;
import group.rober.runtime.lang.MapData;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by tisir yangsong158@qq.com on 2017-04-13
 */

@Api(description = "页面基本测试")
@RestController
@RequestMapping("/ping")
public class PingRestController {

    @RequestMapping("/")
    public String status() {
        return "APPLICATION IS RUNNING";
    }

    @RequestMapping("/rest")
    public String helloWord(){
        return "HELLO SPRING BOOT IS RUNNING 4";
    }

    @RequestMapping("/json")
    public MapData json(){
        MapData ret = new MapData();

        ret.put("id","P1001");
        ret.put("code","PC8001");
        ret.put("name","艾伦");
        ret.put("gender","M");
        ret.put("birth",DateKit.parse("1992-08-19"));
        ret.put("now", DateKit.now());

        return ret;
    }
    @RequestMapping("/wild-card/**")
    public String wildCard(HttpServletRequest request){
        System.out.println(request.getRequestURI());
        System.out.println(request.getServletPath());
        return "WILD CARD IS HERE:";
    }
}

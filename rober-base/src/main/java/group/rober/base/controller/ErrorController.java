package group.rober.base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@Controller
public class ErrorController {

    @RequestMapping(path="/401",method = {RequestMethod.GET,RequestMethod.POST})
    public String http401(){
        return "base/401";
    }

    @RequestMapping(path="/404",method = {RequestMethod.GET,RequestMethod.POST})
    public String http404(){
        return "base/404";
    }

    @RequestMapping(path="/403",method = {RequestMethod.GET,RequestMethod.POST})
    public String http403(){
        return "base/404";
    }

    @RequestMapping(path="/405",method = {RequestMethod.GET,RequestMethod.POST})
    public String http405(){
        return "base/405";
    }

//  500异常交给拦截器处理了，这里就不需要了
//    @RequestMapping(path="/500",method = {RequestMethod.GET,RequestMethod.POST})
//    public String http500(){
//        return "base/500";
//    }
}

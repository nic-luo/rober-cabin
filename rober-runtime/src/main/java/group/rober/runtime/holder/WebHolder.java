package group.rober.runtime.holder;

import group.rober.runtime.lang.ValueObject;
import group.rober.runtime.web.RequestParameterMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * HTTP快速工具获取
 * Created by tisir yangsong158@qq.com on 2017-05-31
 */
public abstract class WebHolder {

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();
    private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<HttpServletResponse>();
    private static final ThreadLocal<HttpSession> sessionHolder = new ThreadLocal<HttpSession>();
    private static ServletContext SERVLET_CONTEXT = null;

    public static void setRequest(HttpServletRequest request){
        requestHolder.set(request);
    }
    public static HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    public static RequestParameterMap getRequestMapData(){
        return new RequestParameterMap(WebHolder.getRequest());
    }

    public static ValueObject getRequestParameter(String name){
        return WebHolder.getRequestMapData().getValue(name);
    }

    public static String getRequestParameterForString(String name,String defaultValue){
        return getRequestParameter(name).strValue(defaultValue);
    }
    public static Integer getRequestParameterForInt(String name,Integer defaultValue){
        return getRequestParameter(name).intValue(defaultValue);
    }
    public static Double getRequestParameterForInt(String name,Double defaultValue){
        return getRequestParameter(name).doubleValue(defaultValue);
    }
    public static Date getRequestParameterForDate(String name){
        return getRequestParameter(name).dateValue();
    }
    public static Boolean getRequestParameterForBoolean(String name,Boolean defaultValue){
        return getRequestParameter(name).boolValue(defaultValue);
    }

    public static void setResponse(HttpServletResponse response){
        responseHolder.set(response);
    }
    public static HttpServletResponse getResponse() {
        return responseHolder.get();
    }
    public static void setServletContext(ServletContext servletContext){
        SERVLET_CONTEXT = servletContext;
    }
    public static ServletContext getServletContext(){
        return SERVLET_CONTEXT;
    }
    public static void setSession(HttpSession session){
        sessionHolder.set(session);
    }
    public static HttpSession getSession(){
        return sessionHolder.get();
    }

    public static void clear(){
        requestHolder.remove();
        responseHolder.remove();
        sessionHolder.remove();
    }


//    public static WebApplicationContext getWebApplicationContext(){
//        return ContextLoader.getCurrentWebApplicationContext();
//    }


    public static RequestParameterMap getRequestParameterMap(){
        return new RequestParameterMap(getRequest());
    }



}

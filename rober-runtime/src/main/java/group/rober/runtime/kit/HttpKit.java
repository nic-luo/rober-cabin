package group.rober.runtime.kit;

import group.rober.runtime.lang.RoberException;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by tisir yangsong158@qq.com on 2017-05-31
 */
public abstract class HttpKit {
    /**
     * 把请求数据中的map转换处理下,单个字串的直接以字串给返回出来
     *
     * @param request request
     * @return map
     */
    public static Map<String,Object> getRequestParameterMap(HttpServletRequest request){
        Map<String,Object> retMap = new LinkedHashMap<String,Object>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        Iterator<String> iterator = parameterMap.keySet().iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            String[] value = request.getParameterValues(name);
            if(value!=null&&value.length==1){
                retMap.put(name,value[0]);
            }else{
                retMap.put(name,value);
            }
        }

        return retMap;
    }

    public static void renderStream(HttpServletResponse response, InputStream inputStream, String contentType, Map<String, String> headers) {
        response.reset();
        response.setContentType(contentType);
        //修改HTTP协议头
        if(headers!=null&&headers.size()>0){
            Set<Map.Entry<String,String>> entries = headers.entrySet();
            for(Map.Entry<String,String> entry:entries){
                response.addHeader(entry.getKey(),entry.getValue());
            }
        }
        //渲染输出
        ServletOutputStream outputStream = null;

        try {
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[10240];
            int len = 0;
            while((len=inputStream.read(buffer))>0){
                outputStream.write(buffer,0,len);
            }
            response.flushBuffer();
            outputStream.flush();

        } catch (IOException e) {
            throw new RoberException(e);
        } finally {
            IOKit.close(outputStream);
        }
    }

    public static void renderStream(OutputStream outputStream, InputStream inputStream) {
        //渲染输出
        try {
            byte[] buffer = new byte[10240];
            int len = 0;
            while((len=inputStream.read(buffer))>0){
                outputStream.write(buffer,0,len);
            }
        } catch (IOException e) {
            throw new RoberException(e);
        } finally {
            IOKit.close(outputStream);
        }
    }

    public static void sendRedirect(HttpServletResponse response,String location){
        response.addHeader(HttpHeaders.LOCATION,location);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return  "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static String retrieveRealView(String staticResourceProxyUrl, String url) {
        if (StringKit.isEmpty(staticResourceProxyUrl))
            return url;
        else {
            return "redirect:" + staticResourceProxyUrl + url;
        }
    }

    public static String iso8859(String str,HttpServletRequest request){
        String charset = request.getCharacterEncoding();
        if(charset==null)charset = Charset.defaultCharset().toString();
        try {
            return new String(str.getBytes(charset),"iso8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

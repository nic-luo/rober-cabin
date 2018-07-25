package group.rober.office.controller;


import group.rober.office.autoconfigure.PageOfficeProperties;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuozhengsoft.pageoffice.PageOfficeLink;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class PageOfficeResourceController {

    @RequestMapping(path = {
            "${group.rober.office.page-office.staticResourceUrl}/poserver.zz",
            "${group.rober.office.page-office.staticResourceUrl}/posetup.exe",
            "${group.rober.office.page-office.staticResourceUrl}/pageoffice.js",
            "${group.rober.office.page-office.staticResourceUrl}/jquery.min.js",
            "${group.rober.office.page-office.staticResourceUrl}/pobstyle.css",
            "${group.rober.office.page-office.staticResourceUrl}/sealsetup.exe"
    })
    public void proxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String url = request.getRequestURI().substring(request.getContextPath().length());
        int lastIndex = url.lastIndexOf("/");
        url = url.substring(0, lastIndex) + PageOfficeProperties.DIFF_PATH_SEGMENT + url.substring(lastIndex);
        request.getRequestDispatcher(url).forward(request, response);
    }
    @GetMapping("/office/pageoffice/link-code")
    @ResponseBody
    public String getPageOfficeLink(HttpServletRequest request){
    	return PageOfficeLink.openWindow(request,"http://_REPLACE_","");
    }

}

package group.rober.base.exhandle;

import group.rober.runtime.lang.ExceptionModel;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BaseErrorController implements ErrorController{
    public static final String ERROR_PATH = "/error";

    @RequestMapping(path = {BaseErrorController.ERROR_PATH}, produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        return null; // todo
    }

    @RequestMapping(path = {BaseErrorController.ERROR_PATH})
    @ResponseBody
    public ExceptionModel error() {
        ExceptionModel em =  new ExceptionModel();
        em.setType(ExceptionModel.EXCEPTION_TYPE_SYS);
        em.setMessage("NOT FOUND");
        return em;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}

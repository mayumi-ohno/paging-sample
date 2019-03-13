package jp.co.sample.emp_management.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Not Fount(404)などのエラーを処理するコントローラ.<br>
 * 
 * 例外が起こった際の処理はGrobalExceptionHandlerで行っているので
 * このクラスが呼ばれるのは404の時のみ
 * 
 * @author igamasayuki
 *
 */
@Controller
public class NotFoundController implements ErrorController {

    private static final String PATH = "/error";
    
    /* (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.web.ErrorController#getErrorPath()
     */
    @Override
    @RequestMapping(PATH)
    public String getErrorPath() {
    	System.out.println("404 not found");
        return "common/notFound";
    }

}
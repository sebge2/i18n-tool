package be.sgerard.poc.githuboauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sebastien Gerard
 */
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping(value = "/error")
    public String errorHtml() {
        return "forward:/index.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

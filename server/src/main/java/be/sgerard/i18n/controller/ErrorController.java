package be.sgerard.i18n.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Web error controller.
 *
 * @author Sebastien Gerard
 */
@Controller
@ApiIgnore
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    /**
     * Forwards the error path to the web application.
     */
    @RequestMapping(value = "/error")
    public String errorHtml() {
        return "forward:/index.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

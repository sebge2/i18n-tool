package be.sgerard.i18n.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web error controller.
 *
 * @author Sebastien Gerard
 */
@Controller
@Hidden
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

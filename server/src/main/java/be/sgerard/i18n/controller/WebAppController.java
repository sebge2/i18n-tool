package be.sgerard.i18n.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Controller for the web-application.
 *
 * @author Sebastien Gerard
 */
@Controller
@ApiIgnore
public class WebAppController {

    /**
     * Returns all deep paths to the web-app.
     */
    @RequestMapping(value = "/{[path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }

}

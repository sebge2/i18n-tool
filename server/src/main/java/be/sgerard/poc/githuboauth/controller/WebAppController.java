package be.sgerard.poc.githuboauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sebastien Gerard
 */
@Controller
public class WebAppController {

    @RequestMapping(value = "/{[path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }

}

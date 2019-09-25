package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.session.UserSessionDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Sebastien Gerard
 */
@Controller
@ApiIgnore
public class WebAppEventController {

    public WebAppEventController() {
    }

    @MessageMapping("/test")
    public void greeting(UserSessionDto message) throws Exception {
        System.out.println(message);
    }
}

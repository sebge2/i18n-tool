package be.sgerard.i18n.controller;

import be.sgerard.i18n.service.github.GitHubWebHookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Controller exposing the Github web-hook.
 *
 * @author Sebastien Gerard
 */
@Controller
@RequestMapping(path = "/api")
public class GitHubController {

    private final GitHubWebHookService webHookService;

    public GitHubController(GitHubWebHookService webHookService) {
        this.webHookService = webHookService;
    }

    /**
     * Handles a notification event coming from GitHub.
     */
    @PostMapping(path = "/git-hub/event")
    @ApiOperation(value = "GitHub Web-hook notifying events on the repository. Only called by GitHub.com")
    public Mono<String> handle(RequestEntity<String> requestEntity) {
        return webHookService.executeWebHook(requestEntity);
    }

}

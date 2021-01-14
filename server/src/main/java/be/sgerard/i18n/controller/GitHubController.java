package be.sgerard.i18n.controller;

import be.sgerard.i18n.service.repository.github.webhook.GitHubWebHookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

/**
 * Controller exposing the Github web-hook.
 *
 * @author Sebastien Gerard
 */
@Controller
@RequestMapping(path = "/api")
@Tag(name = "GitHub", description = "Controller exposing the GitHub web-hook")
public class GitHubController {

    private final GitHubWebHookService webHookService;

    public GitHubController(GitHubWebHookService webHookService) {
        this.webHookService = webHookService;
    }

    /**
     * Handles a notification event coming from GitHub.
     */
    @PostMapping(path = "/git-hub/event")
    @Operation(operationId = "handleEvent", summary = "GitHub Web-hook notifying events on the repository. Only called by GitHub.com")
    @ResponseBody
    public Mono<String> handle(RequestEntity<String> requestEntity) {
        return webHookService.executeWebHook(requestEntity);
    }

}

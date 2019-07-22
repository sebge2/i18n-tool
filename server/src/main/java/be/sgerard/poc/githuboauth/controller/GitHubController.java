package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.service.git.GitHubWebHookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sebastien Gerard
 */
@Controller
@RequestMapping(path = "/api")
public class GitHubController {

    private final GitHubWebHookService webHookService;

    public GitHubController(GitHubWebHookService webHookService) {
        this.webHookService = webHookService;
    }

    @PostMapping(path = "/git-hub/event")
    @ApiOperation(value = "GitHub Webhook notifying events on the repository. Only called by GitHub.com")
    public ResponseEntity<?> handle(RequestEntity<String> requestEntity) {
        return webHookService.executeWebHook(requestEntity);
    }

}

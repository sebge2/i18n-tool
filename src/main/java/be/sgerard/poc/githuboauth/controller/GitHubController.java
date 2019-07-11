package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.controller.support.GitHubWebHookService;
import be.sgerard.poc.githuboauth.controller.support.webhook.GitHubPullRequestEventDto;
import be.sgerard.poc.githuboauth.controller.support.webhook.WebHookCallback;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(GitHubController.class);

    private final GitHubWebHookService webHookService;

    public GitHubController(GitHubWebHookService webHookService) {
        this.webHookService = webHookService;
    }

    @PostMapping(path = "/git-hub/event")
    @ApiOperation(value = "GitHub webhook notifying events on the repository. Only called by GitHub.com")
    public ResponseEntity<?> handle(RequestEntity<String> requestEntity) {
        return webHookService.executeWebHook(
                requestEntity,
                new WebHookCallback() {
                    @Override
                    public void onPullRequest(GitHubPullRequestEventDto pullRequest) {
                        System.out.println(pullRequest.getId() + " n°" + pullRequest.getNumber() + " status " + pullRequest.getStatus());
                        logger.error(pullRequest.getId() + " n°" + pullRequest.getNumber() + " status " + pullRequest.getStatus());
                    }
                }
        );
    }

}

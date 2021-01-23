package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.model.scheduler.dto.NonRecurringScheduledTaskTriggerDto;
import be.sgerard.i18n.model.scheduler.dto.RecurringScheduledTaskTriggerDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static be.sgerard.i18n.model.scheduler.ScheduledTaskTriggerType.RECURRING;
import static be.sgerard.test.i18n.helper.ScheduledTaskTestHelper.sampleNonRecurringTask;
import static be.sgerard.test.i18n.helper.ScheduledTaskTestHelper.sampleRecurringTask;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScheduledTaskControllerTest extends AbstractControllerTest {

    @AfterEach
    public void stop() {
        scheduledTask.deleteAll();
    }

    @Nested
    @DisplayName("findDefinitions")
    class FindDefinitions extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void noRestriction() {
            scheduledTask.createOrUpdate(sampleRecurringTask());

            webClient
                    .get()
                    .uri("/api/scheduled-task/definition")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(1)))
                    .jsonPath("$[?(@.name.en=='Testable Task')]").exists()
                    .jsonPath("$[?(@.description.en=='Task used for testing purpose.')]").exists()
                    .jsonPath("$[?(@.trigger.cronExpression=='* * * * * *')]").exists()
                    .jsonPath("$[?(@.trigger.type=='RECURRING')]").exists()
                    .jsonPath("$[?(@.enabled == true)]").exists();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void enabled() {
            final ScheduledTaskDefinitionEntity scheduled = scheduledTask.createOrUpdate(firstSampleTask()).get();
            scheduledTask.createOrUpdate(secondSampleTask());

            webClient
                    .get()
                    .uri("/api/scheduled-task/definition?enabled=true")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(1)))
                    .jsonPath("$[?(@.id=='" + scheduled.getId() + "')]").exists()
                    .jsonPath("$[?(@.name.en=='First scheduled task')]").exists()
                    .jsonPath("$[?(@.description.en=='First scheduled task description')]").exists()
                    .jsonPath("$[?(@.trigger.cronExpression=='* * * * * *')]").exists()
                    .jsonPath("$[?(@.trigger.type=='RECURRING')]").exists()
                    .jsonPath("$[?(@.enabled == true)]").exists();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void disabled() {
            scheduledTask.createOrUpdate(firstSampleTask());
            final ScheduledTaskDefinitionEntity disabled = scheduledTask.createOrUpdate(secondSampleTask()).disable().get();

            webClient
                    .get()
                    .uri("/api/scheduled-task/definition?enabled=false")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(1)))
                    .jsonPath("$[?(@.id=='" + disabled.getId() + "')]").exists()
                    .jsonPath("$[?(@.name.en=='Second scheduled task')]").exists()
                    .jsonPath("$[?(@.description.en=='Second scheduled task description')]").exists()
                    .jsonPath("$[?(@.trigger.cronExpression=='* * * * * *')]").exists()
                    .jsonPath("$[?(@.trigger.type=='RECURRING')]").exists()
                    .jsonPath("$[?(@.enabled == false)]").exists();
        }
    }

    @Nested
    @DisplayName("findDefinitionById")
    class FindDefinitionById extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void enabled() {
            final ScheduledTaskDefinitionEntity taskDefinition = scheduledTask.createOrUpdate(sampleRecurringTask()).get();

            webClient
                    .get()
                    .uri("/api/scheduled-task/{id}/definition", taskDefinition.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name.en").isEqualTo("Testable Task")
                    .jsonPath("$.description.en").isEqualTo("Task used for testing purpose.")
                    .jsonPath("$.trigger.cronExpression").isEqualTo("* * * * * *")
                    .jsonPath("$.trigger.type").isEqualTo(RECURRING.name())
                    .jsonPath("$.enabled").isEqualTo(true);
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void disabled() {
            final ScheduledTaskDefinitionEntity taskDefinition = scheduledTask.createOrUpdate(sampleRecurringTask()).disable().get();

            webClient
                    .get()
                    .uri("/api/scheduled-task/{id}/definition", taskDefinition.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name.en").isEqualTo("Testable Task")
                    .jsonPath("$.description.en").isEqualTo("Task used for testing purpose.")
                    .jsonPath("$.trigger.cronExpression").isEqualTo("* * * * * *")
                    .jsonPath("$.trigger.type").isEqualTo(RECURRING.name())
                    .jsonPath("$.enabled").isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("enabled")
    class Enabled extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void alreadyEnabled() {
            final ScheduledTaskDefinitionEntity taskDefinition = scheduledTask.createOrUpdate(sampleRecurringTask()).get();

            webClient
                    .post()
                    .uri("/api/scheduled-task/{id}/definition?action=ENABLE", taskDefinition.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name.en").isEqualTo("Testable Task")
                    .jsonPath("$.description.en").isEqualTo("Task used for testing purpose.")
                    .jsonPath("$.trigger.cronExpression").isEqualTo("* * * * * *")
                    .jsonPath("$.trigger.type").isEqualTo(RECURRING.name())
                    .jsonPath("$.enabled").isEqualTo(true);
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void fromDisabled() {
            final ScheduledTaskDefinitionEntity taskDefinition = scheduledTask.createOrUpdate(sampleRecurringTask()).disable().get();

            webClient
                    .post()
                    .uri("/api/scheduled-task/{id}/definition?action=ENABLE", taskDefinition.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name.en").isEqualTo("Testable Task")
                    .jsonPath("$.description.en").isEqualTo("Task used for testing purpose.")
                    .jsonPath("$.trigger.cronExpression").isEqualTo("* * * * * *")
                    .jsonPath("$.trigger.type").isEqualTo(RECURRING.name())
                    .jsonPath("$.enabled").isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("disabled")
    class Disabled extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void alreadyDisabled() {
            final ScheduledTaskDefinitionEntity taskDefinition = scheduledTask.createOrUpdate(sampleRecurringTask()).disable().get();

            webClient
                    .post()
                    .uri("/api/scheduled-task/{id}/definition?action=DISABLE", taskDefinition.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name.en").isEqualTo("Testable Task")
                    .jsonPath("$.description.en").isEqualTo("Task used for testing purpose.")
                    .jsonPath("$.trigger.cronExpression").isEqualTo("* * * * * *")
                    .jsonPath("$.trigger.type").isEqualTo(RECURRING.name())
                    .jsonPath("$.enabled").isEqualTo(false);
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void fromEnabled() {
            final ScheduledTaskDefinitionEntity taskDefinition = scheduledTask.createOrUpdate(sampleRecurringTask()).get();

            webClient
                    .post()
                    .uri("/api/scheduled-task/{id}/definition?action=DISABLE", taskDefinition.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name.en").isEqualTo("Testable Task")
                    .jsonPath("$.description.en").isEqualTo("Task used for testing purpose.")
                    .jsonPath("$.trigger.cronExpression").isEqualTo("* * * * * *")
                    .jsonPath("$.trigger.type").isEqualTo(RECURRING.name())
                    .jsonPath("$.enabled").isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("findAllExecutions")
    class FindExecutions extends AbstractControllerTest {

        private ScheduledTaskDefinitionEntity successfulTask;
        private ScheduledTaskDefinitionEntity failingTask;

        @BeforeEach
        public void setup() {
            final ScheduledTaskExecutionResult failedResult = ScheduledTaskExecutionResult.builder().status(ScheduledTaskExecutionStatus.FAILED).build();

            successfulTask = scheduledTask.createOrUpdate(firstSampleTask()).get();
            failingTask = scheduledTask.createOrUpdate(secondSampleTask(), failedResult).get();

            scheduledTask.forTask(successfulTask).waitForExecution();
            scheduledTask.forTask(failingTask).waitForExecution();
        }

        @AfterEach
        public void stop() {
            scheduledTask.forTask(successfulTask).delete();
            scheduledTask.forTask(failingTask).delete();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void noRestriction() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(2)))
                    .jsonPath("$[?(@.definitionId=='" + successfulTask.getId() + "')]").exists()
                    .jsonPath("$[?(@.status=='" + ScheduledTaskExecutionStatus.SUCCESSFUL.name() + "')]").exists()
                    .jsonPath("$[?(@.description.en=='Task executed successfully.')]").exists()

                    .jsonPath("$[?(@.definitionId=='" + failingTask.getId() + "')]").exists()
                    .jsonPath("$[?(@.status=='" + ScheduledTaskExecutionStatus.FAILED.name() + "')]").exists()

                    .jsonPath("$[*].id").isNotEmpty()
                    .jsonPath("$[*].startTime").isNotEmpty()
                    .jsonPath("$[*].endTime").isNotEmpty()
                    .jsonPath("$[*].durationInMs").isNotEmpty();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution?status=SUCCESSFUL")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(1)))
                    .jsonPath("$[*].id").isNotEmpty()
                    .jsonPath("$[?(@.definitionId=='" + successfulTask.getId() + "')]").exists()
                    .jsonPath("$[*].status").value((Consumer<List<String>>) actual -> assertThat(actual).hasSameElementsAs(singletonList(ScheduledTaskExecutionStatus.SUCCESSFUL.name())))
                    .jsonPath("$[?(@.description.en=='Task executed successfully.')]").exists()
                    .jsonPath("$[*].startTime").isNotEmpty()
                    .jsonPath("$[*].endTime").isNotEmpty()
                    .jsonPath("$[*].durationInMs").isNotEmpty();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void failed() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution?status=FAILED")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(1)))
                    .jsonPath("$[*].id").isNotEmpty()
                    .jsonPath("$[?(@.definitionId=='" + failingTask.getId() + "')]").exists()
                    .jsonPath("$[*].status").value((Consumer<List<String>>) actual -> assertThat(actual).hasSameElementsAs(singletonList(ScheduledTaskExecutionStatus.FAILED.name())))
                    .jsonPath("$[*].startTime").isNotEmpty()
                    .jsonPath("$[*].endTime").isNotEmpty()
                    .jsonPath("$[*].durationInMs").isNotEmpty();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void taskDefinitionId() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution?taskDefinitionId={id}", successfulTask.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(1)))
                    .jsonPath("$[*].id").isNotEmpty()
                    .jsonPath("$[*].definitionId").isEqualTo(successfulTask.getId())
                    .jsonPath("$[*].status").isEqualTo(ScheduledTaskExecutionStatus.SUCCESSFUL.name())
                    .jsonPath("$[*].description.en").isEqualTo("Task executed successfully.")
                    .jsonPath("$[*].startTime").isNotEmpty()
                    .jsonPath("$[*].endTime").isNotEmpty()
                    .jsonPath("$[*].durationInMs").isNotEmpty();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void newerOrEqualThanFewSecondsAgo() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution?newerOrEqualThan={time}", Instant.now().minusSeconds(60).getEpochSecond())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(2)));
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void newerOrEqualThanFewSecondsFuture() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution?newerOrEqualThan={time}", Instant.now().plusSeconds(60).getEpochSecond())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(0)));
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void olderOrEqualThanFewSecondsAgo() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution?olderOrEqualThan={time}", Instant.now().minusSeconds(60).getEpochSecond())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(0)));
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void olderOrEqualThanFewSecondsFuture() {
            webClient
                    .get()
                    .uri("/api/scheduled-task/execution?olderOrEqualThan={time}", Instant.now().plusSeconds(60).getEpochSecond())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(greaterThanOrEqualTo(2)));
        }
    }

    @Nested
    @DisplayName("updateDefinition")
    class UpdateDefinition extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateRecurring() {
            final ScheduledTaskDefinitionEntity task = scheduledTask.createOrUpdate(sampleRecurringTask()).get();

            final String cronExpression = "*/5 * * * * *";
            final ScheduledTaskDefinitionPatchDto patchDto = ScheduledTaskDefinitionPatchDto.builder()
                    .id(task.getId())
                    .trigger(new RecurringScheduledTaskTriggerDto(cronExpression))
                    .build();

            webClient
                    .patch()
                    .uri("/api/scheduled-task/{id}/definition", task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(task.getId())
                    .jsonPath("$.trigger.cronExpression").isEqualTo(cronExpression);
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void changeTriggerType() {
            final ScheduledTaskDefinitionEntity task = scheduledTask.createOrUpdate(sampleRecurringTask()).get();

            final ScheduledTaskDefinitionPatchDto patchDto = ScheduledTaskDefinitionPatchDto.builder()
                    .id(task.getId())
                    .trigger(new NonRecurringScheduledTaskTriggerDto(Instant.now()))
                    .build();

            webClient
                    .patch()
                    .uri("/api/scheduled-task/{id}/definition", task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The trigger type of the scheduled task changed, the original one was RECURRING.");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidCron() {
            final ScheduledTaskDefinitionEntity task = scheduledTask.createOrUpdate(sampleRecurringTask()).get();

            final ScheduledTaskDefinitionPatchDto patchDto = ScheduledTaskDefinitionPatchDto.builder()
                    .id(task.getId())
                    .trigger(new RecurringScheduledTaskTriggerDto("invalid expression"))
                    .build();

            webClient
                    .patch()
                    .uri("/api/scheduled-task/{id}/definition", task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The CRON expression [invalid expression] is invalid.");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidStartTime() {
            final ScheduledTaskDefinitionEntity task = scheduledTask.createOrUpdate(sampleNonRecurringTask()).get();

            final ScheduledTaskDefinitionPatchDto patchDto = ScheduledTaskDefinitionPatchDto.builder()
                    .id(task.getId())
                    .trigger(new NonRecurringScheduledTaskTriggerDto(null))
                    .build();

            webClient
                    .patch()
                    .uri("/api/scheduled-task/{id}/definition", task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").value(containsString("The start time of the scheduled task"));
        }
    }

    private ScheduledTaskDefinition firstSampleTask() {
        return sampleRecurringTask().toBuilder()
                .id("first-testable-scheduled-task")
                .name(new LocalizedString(Locale.ENGLISH, "First scheduled task"))
                .description(new LocalizedString(Locale.ENGLISH, "First scheduled task description"))
                .build();
    }

    private ScheduledTaskDefinition secondSampleTask() {
        return sampleRecurringTask().toBuilder()
                .id("second-testable-scheduled-task")
                .name(new LocalizedString(Locale.ENGLISH, "Second scheduled task"))
                .description(new LocalizedString(Locale.ENGLISH, "Second scheduled task description"))
                .build();
    }
}

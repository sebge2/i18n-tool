package be.sgerard.i18n.service.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateServiceTest {

    @Test
    public void processJsonWithEscape() {
        final TemplateService service = new TemplateService();

        final String actual = service
                .newInlineTemplate("{\"key\": \"${value}\"}")
                .escapeJson()
                .process()
                .withParameter("value", "my value with \" quote")
                .done();

        assertThat(actual).isEqualTo("{\"key\": \"my value with \\\" quote\"}");
    }

}
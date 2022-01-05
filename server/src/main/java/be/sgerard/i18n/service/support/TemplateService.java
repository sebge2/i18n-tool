package be.sgerard.i18n.service.support;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for processing templates. A template may have parameters having the form: <code>${parameterName}</code>.
 */
@Service
public class TemplateService {

    /**
     * Starts the process of the specified template content.
     */
    public TemplateStep newInlineTemplate(String templateBody) {
        try {
            final Template template = new Template("inline", templateBody, new Configuration(Configuration.VERSION_2_3_19));

            return new TemplateStep(template);
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading inline template.", e);
        }
    }

    /**
     * Step when a template has been defined.
     */
    public static class TemplateStep {

        private final Template template;
        private Function<String, String> escape = Function.identity();

        private TemplateStep(Template template) {
            this.template = template;
        }

        /**
         * Escapes all parameter values to insert them in JSON.
         */
        public TemplateStep escapeJson() {
            // NICE find a better way to do this
            escape = StringEscapeUtils::escapeJson;
            return this;
        }

        /**
         * Starts to fill parameter values.
         */
        public TemplateProcessStep process() {
            return new TemplateProcessStep(template, escape);
        }
    }

    /**
     * Step when a template has been defined and we start to fill parameter values.
     */
    public static class TemplateProcessStep {

        private final Template template;
        private final Function<String, String> escape;
        private final Map<String, String> parameters = new HashMap<>();

        private TemplateProcessStep(Template template, Function<String, String> escape) {
            this.template = template;
            this.escape = escape;
        }

        /**
         * Specifies the parameter value.
         */
        public TemplateProcessStep withParameter(String key, String value) {
            this.parameters.put(key, escape.apply(value));
            return this;
        }

        /**
         * Specifies all parameters values.
         */
        public TemplateProcessStep withParameters(Map<String, String> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }

        /**
         * Returns the content of the processed template that does not contain parameters anymore.
         */
        public String done() {
            try {
                final StringWriter writer = new StringWriter();

                template.process(parameters, writer);

                return writer.toString();
            } catch (TemplateException | IOException e) {
                throw new IllegalStateException("Error while processing template.", e);
            }
        }
    }
}

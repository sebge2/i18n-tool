package be.sgerard.i18n.model.validation;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessagesHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Result of a validation.
 *
 * @author Sebastien Gerard
 */
public class ValidationResult implements LocalizedMessagesHolder {

    public static ValidationResult singleMessage(ValidationMessage message) {
        return ValidationResult.builder()
                .messages(message)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new {@link Collector collector} for {@link ValidationResult validation results}.
     */
    public static Collector<ValidationResult, List<ValidationResult>, ValidationResult> toValidationResult() {
        return new Collector<>() {
            @Override
            public Supplier<List<ValidationResult>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<ValidationResult>, ValidationResult> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<ValidationResult>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<List<ValidationResult>, ValidationResult> finisher() {
                return (list) -> ValidationResult.builder()
                        .merge(list)
                        .build();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return emptySet();
            }
        };
    }

    /**
     * Merges both {@link ValidationResult validation results}.
     */
    public static ValidationResult merge(ValidationResult... validationResults) {
        return ValidationResult.builder().merge(validationResults).build();
    }

    /**
     * Merges both {@link ValidationResult validation results}.
     */
    public static ValidationResult merge(List<ValidationResult> validationResults) {
        return ValidationResult.builder().merge(validationResults).build();
    }

    /**
     * Empty succesful validation result.
     */
    public static final ValidationResult EMPTY = ValidationResult.builder().build();

    private final List<ValidationMessage> messages;

    private ValidationResult(Builder builder) {
        messages = unmodifiableList(builder.messages);
    }

    @Override
    public List<LocalizedString> toLocalizedMessages() {
        return getMessages().stream()
                .map(ValidationMessage::toLocalizedMessage)
                .collect(toList());
    }

    /**
     * Returns all the generated {@link ValidationMessage messages}.
     */
    public List<ValidationMessage> getMessages() {
        return messages;
    }

    /**
     * Returns whether the validation was successful.
     */
    public boolean isSuccessful() {
        return getMessages().isEmpty();
    }

    /**
     * Builder of {@link ValidationResult validation result}.
     */
    public static final class Builder {

        private final List<ValidationMessage> messages = new ArrayList<>();

        private Builder() {
        }

        public Builder messages(List<ValidationMessage> messages) {
            this.messages.addAll(messages);
            return this;
        }

        public Builder messages(ValidationMessage... messages) {
            return messages(asList(messages));
        }

        public Builder merge(ValidationResult... validationResults) {
            return merge(asList(validationResults));
        }

        public Builder merge(List<ValidationResult> validationResults) {
            return messages(
                    validationResults.stream()
                            .flatMap(validationResult -> validationResult.getMessages().stream())
                            .collect(toList())
            );
        }

        public ValidationResult build() {
            return new ValidationResult(this);
        }
    }
}

package be.sgerard.i18n.model.support;

/**
 * Holder of a localized message. The message is identified by a key (the key must be defined in translations bundles).
 *
 * @author Sebastien Gerard
 * @see org.springframework.context.MessageSource
 */
public interface LocalizedMessageHolder {

    /**
     * Returns the key of the message (available in translations bundles files).
     */
    String getMessageKey();

    /**
     * Returns parameters of the message. A parameter can also be a {@link LocalizedMessageHolder localized message holder}.
     */
    Object[] getMessageParameters();

    /**
     * Simple implementation of a message holder.
     */
    class Simple implements LocalizedMessageHolder {

        private final String messageKey;
        private final Object[] parameters;

        public Simple(String messageKey, Object... parameters) {
            this.messageKey = messageKey;
            this.parameters = parameters;
        }

        @Override
        public String getMessageKey() {
            return messageKey;
        }

        @Override
        public Object[] getMessageParameters() {
            return parameters;
        }
    }

}

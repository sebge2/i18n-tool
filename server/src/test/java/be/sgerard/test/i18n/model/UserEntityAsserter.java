package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class UserEntityAsserter {

    public static UserEntityAsserter newAssertion() {
        return new UserEntityAsserter();
    }

    public UserEntityAsserter expectEquals(UserEntity actual, UserEntity expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getDisplayName()).isEqualTo(expected.getDisplayName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getRoles()).containsExactlyInAnyOrderElementsOf(expected.getRoles());
        assertThat(actual.getPreferences().getPreferredLocales()).containsExactlyInAnyOrderElementsOf(expected.getPreferences().getPreferredLocales());
        assertThat(actual.getPreferences().getToolLocale()).isEqualTo(expected.getPreferences().getToolLocale());

        assertThat(actual.getClass()).isEqualTo(expected.getClass());

        if(actual instanceof InternalUserEntity){
            doExpectEquals((InternalUserEntity)actual, (InternalUserEntity) expected);
        } else if(actual instanceof ExternalUserEntity){
            doExpectEquals((ExternalUserEntity)actual, (ExternalUserEntity) expected);
        } else {
            throw new UnsupportedOperationException("Unsupported user [" + actual + "].");
        }

        return this;
    }

    private void doExpectEquals(InternalUserEntity actual, InternalUserEntity expected) {
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getAvatar().orElse(new byte[0])).containsExactly(expected.getAvatar().orElse(new byte[0]));
    }

    private void doExpectEquals(ExternalUserEntity actual, ExternalUserEntity expected) {
        assertThat(actual.getExternalId()).isEqualTo(expected.getExternalId());
        assertThat(actual.getExternalAuthSystem()).isEqualTo(expected.getExternalAuthSystem());
        assertThat(actual.getAvatarUrl()).isEqualTo(expected.getAvatarUrl());
    }

}

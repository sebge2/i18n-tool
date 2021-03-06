package be.sgerard.test.i18n.support.auth.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.sgerard.test.i18n.model.UserEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithInternalUser(username = JOHN_DOE_USERNAME, password = JOHN_DOE_PASSWORD, email = JOHN_DOE_EMAIL, displayName = JOHN_DOE)
public @interface WithJohnDoeSimpleUser {
}

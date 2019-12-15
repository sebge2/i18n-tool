package be.sgerard.test.i18n.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.sgerard.test.i18n.model.UserDtoTestUtils.*;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithInternalUser(username = JANE_DOE_USERNAME, email = JANE_DOE_EMAIL, password = JANE_DOE_PASSWORD, displayName = JANE_DOE, roles = {"ADMIN"})
public @interface WithJaneDoeAdminUser {
}

package be.sgerard.test.i18n.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.sgerard.test.i18n.model.UserDtoTestUtils.*;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithInternalUser(username = JANE_DOE, email = JANE_DOE_EMAIL, password = JANE_DOE_PASSWORD, roles = {"ADMIN"})
public @interface WithJaneDoeAdminUser {
}

package be.sgerard.test.i18n.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithInternalUser(username = "admin-test", roles = {"ADMIN"})
public @interface WithAdminUser {
}

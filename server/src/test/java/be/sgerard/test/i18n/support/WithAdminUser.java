package be.sgerard.test.i18n.support;

import be.sgerard.i18n.service.user.UserManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithInternalUser(username = UserManager.ADMIN_USER_NAME, roles = {"ADMIN"})
public @interface WithAdminUser {
}

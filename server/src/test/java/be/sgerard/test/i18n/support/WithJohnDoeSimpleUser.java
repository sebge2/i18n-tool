package be.sgerard.test.i18n.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.sgerard.test.i18n.model.UserDtoTestUtils.JOHN_DOE;
import static be.sgerard.test.i18n.model.UserDtoTestUtils.JOHN_DOE_PASSWORD;

/**
 * @author Sebastien Gerard
 */
@Retention(RetentionPolicy.RUNTIME)
@WithInternalUser(username = JOHN_DOE, password = JOHN_DOE_PASSWORD)
public @interface WithJohnDoeSimpleUser {
}

package be.sgerard.test.i18n.support;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test requiring an access to GitHub. Credentials and an internet connection are needed.
 *
 * @author Sebastien Gerard
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag("GitHubCredentials")
public @interface GitHubCredentialsTest {
}

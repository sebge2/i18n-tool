package be.sgerard.test.i18n.support;

import java.io.File;

/**
 * @author Sebastien Gerard
 */
public final class TestUtils {

    private TestUtils() {
    }

    public static File currentProjectLocation() {
        return new File(TestUtils.class.getResource("/application-test.yml").getFile()).getParentFile().getParentFile().getParentFile().getParentFile();
    }
}

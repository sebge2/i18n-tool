package be.sgerard.poc.githuboauth.service.git;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * API allowing modifications on a branch.
 *
 * @author Sebastien Gerard
 */
public interface BranchModificationAPI extends BranchBrowsingAPI {

    OutputStream writeFile(File file) throws IOException;

}

package be.sgerard.poc.githuboauth.service.git;

import java.io.File;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryBrowser<T> {

    T browse(File repositoryLocation) throws Exception;

}

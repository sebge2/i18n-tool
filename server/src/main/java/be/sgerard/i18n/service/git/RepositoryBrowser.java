package be.sgerard.i18n.service.git;

import java.io.File;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryBrowser<T> {

    T browse(File repositoryLocation) throws Exception;

}

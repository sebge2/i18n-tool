package be.sgerard.poc.githuboauth.service.git;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * API allowing to browse and modify files on a branch.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryAPI extends AutoCloseable {

    void updateLocalRepository() throws RepositoryException;

    String getCurrentBranch() throws RepositoryException;

    List<String> listRemoteBranches() throws RepositoryException;

    List<String> listLocalBranches() throws RepositoryException;

    void checkout(String branch) throws RepositoryException;

    void createBranch(String branch) throws RepositoryException;

    void removeBranch(String branch) throws RepositoryException;

    Stream<File> listAllFiles(File file) throws IOException;

    Stream<File> listNormalFiles(File file) throws IOException;

    Stream<File> listDirectories(File file) throws IOException;

    InputStream openFile(File file) throws IOException;

    OutputStream writeFile(File file) throws IOException;

    void revert(File file) throws RepositoryException;

    void commitAll(String message) throws RepositoryException;

    PullRequestManager getPullRequestManager();

    boolean isClosed();
}

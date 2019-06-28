package be.sgerard.poc.githuboauth.service.git;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * API allowing to browse files on a branch.
 *
 * @author Sebastien Gerard
 */
public interface BranchBrowsingAPI {

    Stream<File> listAllFiles(File file);

    Stream<File> listNormalFiles(File file);

    Stream<File> listDirectories(File file);

    InputStream openFile(File file) throws IOException;

}
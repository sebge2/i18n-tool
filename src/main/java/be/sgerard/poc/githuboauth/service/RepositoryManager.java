package be.sgerard.poc.githuboauth.service;

import java.io.File;
import java.util.List;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryManager {

    void initializeLocalRepo(File localRepository) throws Exception;

    List<String> listBranches() throws Exception;

    void pull() throws Exception;

    void createBranch(String branchName) throws Exception;

    void checkoutBranch(String branchName) throws Exception;

    void commitAll(String message) throws Exception;

    void push() throws Exception;
}

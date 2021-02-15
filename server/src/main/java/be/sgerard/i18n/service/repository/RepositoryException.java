package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessageHolder;

import java.io.File;

/**
 * Exception thrown when accessing and doing actions on a repository.
 *
 * @author Sebastien Gerard
 */
public class RepositoryException extends RuntimeException implements LocalizedMessageHolder {

    public static RepositoryException onOpen(Throwable cause) {
        return new RepositoryException("Error while opening the repository.", "RepositoryException.open.message", cause);
    }

    public static RepositoryException onValidate(Throwable cause) {
        return new RepositoryException("Error while validating the repository.", "RepositoryException.validate.message", cause);
    }

    public static RepositoryException onUpdate(Throwable cause) {
        return new RepositoryException("Error while updating the local repository.", "RepositoryException.update.message", cause);
    }

    public static RepositoryException onDelete(Throwable cause) {
        return new RepositoryException("Error while deleting the local repository.", "RepositoryException.delete.message", cause);
    }

    public static RepositoryException onBranchNotFound(String branchName) {
        return new RepositoryException("The branch [" + branchName + "] does not exist.", "RepositoryException.branch-not-found.message", null, branchName);
    }

    public static RepositoryException onBranchListing(Throwable cause) {
        return new RepositoryException("Error while listing branches.", "RepositoryException.branch-list.message", cause);
    }

    public static RepositoryException onBranchCreation(String branchName, Throwable cause) {
        return new RepositoryException("Error while creating branch [" + branchName + "].", "RepositoryException.branch-creation.message", cause, branchName);
    }

    public static RepositoryException onBranchAlreadyExist(String branchName) {
        return new RepositoryException("The branch [" + branchName + "] already exists.", "RepositoryException.branch-already-exist.message", null, branchName);
    }

    public static RepositoryException onBranchDeletion(String branchName, Throwable cause) {
        return new RepositoryException("Error while deleting branch [" + branchName + "].", "RepositoryException.branch-deletion.message", cause, branchName);
    }

    public static RepositoryException onForbiddenBranchDeletion(String branchName) {
        return new RepositoryException("Cannot delete branch [" + branchName + "].", "RepositoryException.forbidden-branch-deletion.message", null, branchName);
    }

    public static RepositoryException onBranchSwitching(String branchName, Throwable cause) {
        return new RepositoryException("Error while switching to the branch [" + branchName + "].", "RepositoryException.branch-switching.message", cause, branchName);
    }

    public static RepositoryException onPush(Throwable cause) {
        return new RepositoryException("Error while pushing changes.", "RepositoryException.pushing.message", cause);
    }

    public static RepositoryException onRevert(Throwable cause) {
        return new RepositoryException("Error while reverting changes.", "RepositoryException.reverting.message", cause);
    }

    public static RepositoryException onLockTimeout(Throwable cause) {
        return new RepositoryException("Error while locking the repository.", "RepositoryException.locking-timeout.message", cause);
    }

    public static RepositoryException onFileListing(File location, Throwable cause) {
        return new RepositoryException("Error while listing files from location [" + location + "].", "RepositoryException.file-listing.message", cause, location);
    }

    public static RepositoryException onFileReading(File file, Throwable cause) {
        return new RepositoryException("Error while reading [" + file + "].", "RepositoryException.file-reading.message", cause, file);
    }

    public static RepositoryException onFileWriting(File file, Throwable cause) {
        return new RepositoryException("Error while writing [" + file + "].", "RepositoryException.file-writing.message", cause, file);
    }

    public static RepositoryException onBranchMerging(String branch, Throwable cause) {
        return new RepositoryException("Error while merging [" + branch + "].", "RepositoryException.branch-merging.message", cause, branch);
    }

    public static RepositoryException onFileDeletion(File file, Throwable cause) {
        return new RepositoryException("Error while deleting file [" + file + "].", "RepositoryException.file-deletion.message", cause, file);
    }

    public static RepositoryException onAccessGitHub(Throwable cause) {
        return new RepositoryException("Error while accessing GitHub.", "RepositoryException.git-hub-access.message", cause);
    }

    private final LocalizedString localizedMessage;

    public RepositoryException(String message, String messageKey, Throwable cause, Object... parameters) {
        super(message, cause);

        this.localizedMessage = LocalizedString.fromBundle("i18n/exception", messageKey, parameters);
    }

    @Override
    public LocalizedString toLocalizedMessage() {
        return localizedMessage;
    }
}

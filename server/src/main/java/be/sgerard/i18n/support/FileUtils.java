package be.sgerard.i18n.support;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Bunch of utility methods for files.
 *
 * @author Sebastien Gerard
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Creates a temporary directory with the specified prefix name.
     */
    public static File createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix).toFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temporary directory.", e);
        }
    }

    /**
     * Deletes the specified directory (not necessarily empty).
     */
    public static void deleteDirectory(File directory) {
        try {
            org.apache.commons.io.FileUtils.forceDelete(directory);
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete directory", e);
        }
    }

    /**
     * Zips the content of the specified directory to the specified ZIP file. This file can be optionally
     * encrypted (password can be <tt>null</tt>).
     */
    public static void zipDirectory(File directory, File zipFile, String encryptionPassword) throws ZipException {
        final ZipFile zip = new ZipFile(zipFile);

        final ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.ULTRA);

        if (encryptionPassword != null) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.AES);
            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zip.setPassword(encryptionPassword.toCharArray());
        }

        for (File file : Optional.ofNullable(directory.listFiles()).orElse(new File[0])) {
            if (file.isDirectory()) {
                zip.addFolder(file, parameters);
            } else {
                zip.addFile(file, parameters);
            }
        }
    }

    /**
     * Unzips the content of the specified ZIP file to the specified directory. This file can be optionally
     * encrypted (password can be <tt>null</tt>).
     */
    public static void unzipDirectory(File directory, File zipFile, String encryptionPassword) throws ZipException {
        final ZipFile zip = new ZipFile(zipFile);

        if (zip.isEncrypted() && (encryptionPassword != null)) {
            zip.setPassword(encryptionPassword.toCharArray());
        }

        zip.extractAll(directory.getPath());
    }
}

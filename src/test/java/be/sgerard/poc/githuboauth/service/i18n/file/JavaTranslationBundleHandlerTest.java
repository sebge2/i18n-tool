package be.sgerard.poc.githuboauth.service.i18n.file;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

import static be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils.removeParentFile;

/**
 * @author Sebastien Gerard
 */
public class JavaTranslationBundleHandlerTest {

    @Test
    public void check() throws IOException {
        System.out.println(Locale.CANADA_FRENCH.getDisplayName());
        System.out.println(Locale.CANADA_FRENCH.toString());
        System.out.println(Locale.CANADA_FRENCH.toLanguageTag());
        System.out.println("");
//        final Locale x = Locale.forLanguageTag(Locale.CANADA_FRENCH.toString());
//        System.out.println(x);

//        final JavaTranslationBundleHandler handler = new JavaTranslationBundleHandler(null);

//        final File directory = new File("/home/sgerard/sandboxes/github-oauth/src/test/");

//        System.out.println(removeParentFile(Paths.get("/home/sgerard").toFile(), directory));

//        final boolean collect = handler.continueScanning(directory);
//
//        System.out.println(collect);
    }

    @Test
    public void test() throws IOException {
//        final JavaTranslationBundleHandler handler = new JavaTranslationBundleHandler(null);

//        final List<ScannedBundleFileDto> collect = handler.scanBundles(new File("/home/sgerard/sandboxes/github-oauth/src/test/resources/be/sgerard/poc/githuboauth/service/i18n/file"), browseAPI).collect(Collectors.toList());
//
//        System.out.println(collect);
    }

    @Test
    public void second() throws IOException {
//        final JavaTranslationBundleHandler handler = new JavaTranslationBundleHandler(null);

//        final List<ScannedBundleFileDto> collect = handler.scanBundles(new File("/home/sgerard/sandboxes/github-oauth/src/test/resources/be/sgerard/poc/githuboauth/service/i18n/file"), browseAPI).collect(Collectors.toList());
//
//
//        System.out.println(collect);


//        final List<ScannedBundleFileKeyDto> collect1 = handler.scanKeys(collect.get(0), browseAPI).collect(Collectors.toList());
//
//        System.out.println(collect1);
    }

}
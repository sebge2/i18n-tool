package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileLocation;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.*;
import java.nio.file.Files;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class JavaPropertiesBundleHandlerTest {

    // TODO
    @Test
    public void check() throws IOException {
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

    @Test
    public void updateTranslationsUtf8Encoding() throws Exception {
        final File tmpDir = Files.createTempDirectory("java-properties-handler").toFile();

        try {
            final TestableTranslationRepositoryWriteApi api = new TestableTranslationRepositoryWriteApi();
            final ScannedBundleFileLocation bundleFileLocation = new ScannedBundleFileLocation(tmpDir, "my-bundle");
            final TranslationLocaleEntity locale = createEnglishLocale();

            final Flux<Pair<String, String>> translations = Flux.just(
                    Pair.of("first.second", "il l'est"),
                    Pair.of("first.third", "value 2\nnewline"),
                    Pair.of("fourth", "évaluation")
            );

            final JavaPropertiesBundleHandler handler = new JavaPropertiesBundleHandler(new AppProperties());

            StepVerifier
                    .create(handler.updateTranslations(bundleFileLocation, locale, translations, api))
                    .verifyComplete();

            final String actual = FileUtils.readFileToString(new File(tmpDir, "my-bundle_en.properties"));

            assertThat(actual).isEqualTo("first.second=il l'est\n" +
                    "first.third=value 2\\nnewline\n" +
                    "fourth=évaluation\n");
        } finally {
            FileUtils.forceDelete(tmpDir);
        }
    }

    @Test
    public void updateTranslationsDefaultEncoding() throws Exception {
        final File tmpDir = Files.createTempDirectory("java-properties-handler").toFile();

        try {
            final TestableTranslationRepositoryWriteApi api = new TestableTranslationRepositoryWriteApi();
            final ScannedBundleFileLocation bundleFileLocation = new ScannedBundleFileLocation(tmpDir, "my-bundle");
            final TranslationLocaleEntity locale = createEnglishLocale();

            final Flux<Pair<String, String>> translations = Flux.just(
                    Pair.of("first.second", "il l'est"),
                    Pair.of("first.third", "value 2\nnewline"),
                    Pair.of("fourth", "évaluation")
            );

            final AppProperties appProperties = new AppProperties();
            appProperties.getRepository().getJavaProperties().setUtf8Encoding(false);

            final JavaPropertiesBundleHandler handler = new JavaPropertiesBundleHandler(appProperties);

            StepVerifier
                    .create(handler.updateTranslations(bundleFileLocation, locale, translations, api))
                    .verifyComplete();

            final String actual = FileUtils.readFileToString(new File(tmpDir, "my-bundle_en.properties"));

            assertThat(actual).isEqualTo("first.second=il l'est\n" +
                    "first.third=value 2\\nnewline\n" +
                    "fourth=\\u00E9valuation\n");
        } finally {
            FileUtils.forceDelete(tmpDir);
        }
    }

    private TranslationLocaleEntity createEnglishLocale() {
        return new TranslationLocaleEntity("en", null, emptyList(), "English", "en");
    }

    private static final class TestableTranslationRepositoryWriteApi implements TranslationRepositoryWriteApi {

        @Override
        public Mono<File> openAsTemp(File file) throws RepositoryException {
            return Mono.just(file);
        }

        @Override
        public Mono<OutputStream> openOutputStream(File file) throws RepositoryException {
            try {
                return Mono.just(new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                throw RepositoryException.onFileWriting(file, e);
            }
        }
    }

}

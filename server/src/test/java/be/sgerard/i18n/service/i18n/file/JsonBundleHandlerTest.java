package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileLocation;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.*;
import java.nio.file.Files;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sebastien Gerard
 */
public class JsonBundleHandlerTest {

    // TODO
    @Test
    @Ignore
    public void scanBundles() throws IOException {
        final File directory = new File("/be/sgerard/i18n/service/i18n/file");

        final GitRepositoryApi repositoryAPI = mock(GitRepositoryApi.class);
        when(repositoryAPI.listNormalFiles(directory))
                .thenReturn(Stream.of(new File(directory, "fr.json")));

        final JsonBundleHandler handler = new JsonBundleHandler();

//        final List<ScannedBundleFileDto> actual = handler.scanBundles(directory, repositoryAPI).collect(toList());
//
//        assertThat(actual).hasSize(1);
//        assertThat(actual.get(0).getName()).isEqualTo("file");
//        assertThat(actual.get(0).getType()).isEqualTo(BundleType.JSON_ICU);
//        assertThat(actual.get(0).getLocationDirectory()).isEqualTo(directory);
//        assertThat(actual.get(0).getLocales()).containsExactly(Locale.FRENCH);
//        assertThat(actual.get(0).getFiles()).containsExactly(new File(directory, "fr.json"));
    }

    @Test
    public void scanKeys() throws IOException {
//        final File directory = new File("/be/sgerard/i18n/service/i18n/file");
//        final ScannedBundleFile bundleFile = new ScannedBundleFile(
//                "file",
//                BundleType.JSON_ICU,
//                directory,
//                singletonList(Locale.FRENCH),
//                singletonList(new File(directory, "fr.json"))
//        );
//
//        final GitRepositoryApi repositoryAPI = mock(GitRepositoryApi.class);
//        when(repositoryAPI.openInputStream(new File(directory, "fr.json")))
//                .then(invocationOnMock -> getClass().getResourceAsStream(invocationOnMock.getArgument(0).toString()));
//
//        final JsonBundleHandler handler = new JsonBundleHandler();

//        final Collection<ScannedBundleFileKeyDto> actual = handler.scanKeys(bundleFile, repositoryAPI);
//
//        assertThat(actual).hasSize(3);
//        assertThat(actual).element(0).extracting(ScannedBundleFileKeyDto::getKey).isEqualTo("first-root.first");
//        assertThat(actual).element(0).extracting(ScannedBundleFileKeyDto::getTranslations).isEqualTo(singletonMap(Locale.FRENCH, "first value"));
//
//        assertThat(actual).element(1).extracting(ScannedBundleFileKeyDto::getKey).isEqualTo("first-root.second");
//        assertThat(actual).element(1).extracting(ScannedBundleFileKeyDto::getTranslations).isEqualTo(singletonMap(Locale.FRENCH, "second value"));
//
//        assertThat(actual).element(2).extracting(ScannedBundleFileKeyDto::getKey).isEqualTo("second-root.sub-level.first");
//        assertThat(actual).element(2).extracting(ScannedBundleFileKeyDto::getTranslations).isEqualTo(singletonMap(Locale.FRENCH, "another value"));
    }

    @Test
    public void updateTranslations() throws Exception {
        final File tmpDir = Files.createTempDirectory("json-handler").toFile();

        try {
            final TestableTranslationRepositoryWriteApi api = new TestableTranslationRepositoryWriteApi();
            final ScannedBundleFileLocation bundleFileLocation = new ScannedBundleFileLocation(tmpDir, "my-bundle");
            final TranslationLocaleEntity locale = createEnglishLocale();

            final Flux<Pair<String, String>> translations = Flux.just(
                    Pair.of("first.second", "value"),
                    Pair.of("first.third", "value 2"),
                    Pair.of("fourth", "value 3")
            );

            final JsonBundleHandler handler = new JsonBundleHandler();

            StepVerifier
                    .create(handler.updateTranslations(bundleFileLocation, locale, translations, api))
                    .verifyComplete();

            final String actual = FileUtils.readFileToString(new File(tmpDir, "en.json"));

            assertThat(actual).isEqualTo("{\n" +
                    "  \"first\": {\n" +
                    "    \"second\": \"value\",\n" +
                    "    \"third\": \"value 2\"\n" +
                    "  },\n" +
                    "  \"fourth\": \"value 3\"\n" +
                    "}");
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

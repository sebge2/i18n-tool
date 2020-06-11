package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sebastien Gerard
 */
public class JsonICUTranslationBundleHandlerTest {

    @Test
    @Ignore
    public void scanBundles() throws IOException {
        final File directory = new File("/be/sgerard/i18n/service/i18n/file");

        final GitRepositoryApi repositoryAPI = mock(GitRepositoryApi.class);
        when(repositoryAPI.listNormalFiles(directory))
                .thenReturn(Stream.of(new File(directory, "fr.json")));

        final JsonICUTranslationBundleHandler handler = new JsonICUTranslationBundleHandler(new AppProperties());

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
        final File directory = new File("/be/sgerard/i18n/service/i18n/file");
        final ScannedBundleFileDto bundleFile = new ScannedBundleFileDto(
                "file",
                BundleType.JSON,
                directory,
                singletonList(Locale.FRENCH),
                singletonList(new File(directory, "fr.json"))
        );

        final GitRepositoryApi repositoryAPI = mock(GitRepositoryApi.class);
        when(repositoryAPI.openInputStream(new File(directory, "fr.json")))
                .then(invocationOnMock -> getClass().getResourceAsStream(invocationOnMock.getArgument(0).toString()));

        final JsonICUTranslationBundleHandler handler = new JsonICUTranslationBundleHandler(new AppProperties());

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

}

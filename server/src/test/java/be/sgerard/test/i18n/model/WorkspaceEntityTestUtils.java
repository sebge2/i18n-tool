package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.GitHubReviewEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;

import java.time.Instant;

import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.I18N_TOOL_GITHUB_ID;
import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.I18N_TOOL_GIT_ID;
import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.ENGLISH_ID;
import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.FRANCAIS_ID;
import static java.util.Arrays.asList;

/**
 * @author Sebastien Gerard
 */
public final class WorkspaceEntityTestUtils {

    public static final String MASTER_I18N_TOOL_GITHUB_ID = "1d4aa341-1beb-4f1c-8afc-452b95fadcaf";
    public static final String DEVELOP_I18N_TOOL_GITHUB_ID = "c9bc1848-7b69-4f19-94f4-41095e9089d9";
    public static final String RELEASE_2020_08_I18N_TOOL_GITHUB_ID = "54578b49-1a5c-47fc-9da5-6bde267c5eb6";
    public static final String MASTER_I18N_TOOL_GIT_ID = "603693c7-e2cd-4eaf-8725-d0fb072c231f";
    public static final String DEVELOP_I18N_TOOL_GIT_ID = "4cadd939-8592-4488-91ea-0a4561b80963";
    public static final String RELEASE_2020_08_I18N_TOOL_GIT_ID = "2b10c334-152d-4e71-ab87-28929cfe6b42";

    public static final String DEVELOP_EXCEPTIONS_FILE_I18N_TOOL_GITHUB_ID = "9f46e01a-d590-45e6-a696-ccbb7a0951a5";
    public static final String DEVELOP_I18N_FILE_I18N_TOOL_GITHUB_ID = "4a5fbabc-b747-48bb-b34d-ff784940969b";

    private WorkspaceEntityTestUtils() {
    }

    public static WorkspaceEntity masterI18nToolsGitHub() {
        return new WorkspaceEntity(I18N_TOOL_GITHUB_ID, "master")
                .setId(MASTER_I18N_TOOL_GITHUB_ID)
                .setStatus(WorkspaceStatus.INITIALIZED)
                .setFiles(asList(
                        new BundleFileEntity(
                                "exception",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/exception_fr.properties")
                                                .setId("bbfad91b-2c78-4d69-a13f-fa955aff9d25"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/exception_en.properties")
                                                .setId("eb92a280-830c-4bdf-bcd9-0b4dfc7011de")
                                )
                        )
                                .setId("93e7ac43-510b-4074-a7c0-fc77e9e3caef")
                                .setNumberKeys(52),
                        new BundleFileEntity(
                                "validation",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/validation_fr.properties")
                                                .setId("acf5731c-68ca-45dd-b16a-dd6040d32fbd"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/validation_en.properties")
                                                .setId("ca325cb1-79d3-4839-a410-2f7f8ed028ed")
                                )
                        )
                                .setId("80ace8e7-20cc-48eb-a7b6-513042a9320d")
                                .setNumberKeys(12),
                        new BundleFileEntity(
                                "i18n",
                                "front/src/main/web/src/assets/i18n",
                                BundleType.JSON_ICU,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "front/src/main/web/src/assets/i18n/fr.properties")
                                                .setId("68d594e0-b599-4218-804f-d1db7c195f56"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "front/src/main/web/src/assets/i18n/en.properties")
                                                .setId("908eafae-41f6-43d9-b16c-d39e57a42468")
                                )
                        )
                                .setId("61813489-30ef-47f5-9663-c9ce83c1c80f")
                                .setNumberKeys(12)
                ))
                .setLastSynchronization(Instant.ofEpochSecond(1606760823))
                .setReview(null);
    }

    public static WorkspaceEntity developI18nToolsGitHub() {
        return new WorkspaceEntity(I18N_TOOL_GITHUB_ID, "develop")
                .setId(DEVELOP_I18N_TOOL_GITHUB_ID)
                .setStatus(WorkspaceStatus.IN_REVIEW)
                .setFiles(asList(
                        new BundleFileEntity(
                                "exception",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/exception_fr.properties")
                                                .setId("fae4868b-b4bd-4d92-8bfc-316725a2df1d"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/exception_en.properties")
                                                .setId("7730a61f-0f3c-4d8f-b2b3-d3d5bb97e034")
                                )
                        )
                                .setId(DEVELOP_EXCEPTIONS_FILE_I18N_TOOL_GITHUB_ID)
                                .setNumberKeys(56),
                        new BundleFileEntity(
                                "validation",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/validation_fr.properties")
                                                .setId("1ba47715-9ad1-4718-be73-733b1a142237"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/validation_en.properties")
                                                .setId("5ea80307-34c1-4adb-8f8b-f572996d63aa")
                                )
                        )
                                .setId("f11604d8-212f-4005-b162-a4b3fbce2067")
                                .setNumberKeys(16),
                        new BundleFileEntity(
                                "i18n",
                                "front/src/main/web/src/assets/i18n",
                                BundleType.JSON_ICU,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "front/src/main/web/src/assets/i18n/fr.properties")
                                                .setId("26f2ebc8-84d9-4263-b8d3-3e7a9e572cf1"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "front/src/main/web/src/assets/i18n/en.properties")
                                                .setId("a70b8d46-9a9b-4f3f-a046-fb89381d9936")
                                )
                        )
                                .setId(DEVELOP_I18N_FILE_I18N_TOOL_GITHUB_ID)
                                .setNumberKeys(16)
                ))
                .setLastSynchronization(Instant.ofEpochSecond(1606760823))
                .setReview(new GitHubReviewEntity("develop_i18n_2020-11-08", 60));
    }

    public static WorkspaceEntity release20208I18nToolsGitHub() {
        return new WorkspaceEntity(I18N_TOOL_GITHUB_ID, "release/2020.08")
                .setId(RELEASE_2020_08_I18N_TOOL_GITHUB_ID)
                .setStatus(WorkspaceStatus.NOT_INITIALIZED)
                .setReview(null);
    }

    public static WorkspaceEntity masterI18nToolsGit() {
        return new WorkspaceEntity(I18N_TOOL_GIT_ID, "master")
                .setId(MASTER_I18N_TOOL_GIT_ID)
                .setStatus(WorkspaceStatus.INITIALIZED)
                .setFiles(asList(
                        new BundleFileEntity(
                                "exception",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/exception_fr.properties")
                                                .setId("b77a6065-2105-4492-a77b-6224df1e93c4"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/exception_en.properties")
                                                .setId("55401be8-41ee-4d35-8afa-29f57e46f904")
                                )
                        )
                                .setId("0fa3df29-6526-4742-835c-2a4af7b2788d")
                                .setNumberKeys(52),
                        new BundleFileEntity(
                                "validation",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/validation_fr.properties")
                                                .setId("14efe4b0-0968-48db-96ec-d117a5bf4880"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/validation_en.properties")
                                                .setId("eb822354-f2a8-4029-923e-59d92161ce65")
                                )
                        )
                                .setId("dd692682-94aa-471b-ae24-5dfca86cbfc0")
                                .setNumberKeys(12),
                        new BundleFileEntity(
                                "i18n",
                                "front/src/main/web/src/assets/i18n",
                                BundleType.JSON_ICU,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "front/src/main/web/src/assets/i18n/fr.properties")
                                                .setId("dd6320d0-019c-43ae-b713-49106e6ed5a4"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "front/src/main/web/src/assets/i18n/en.properties")
                                                .setId("5b00ea77-8a04-4a50-b3d2-016f34515645")
                                )
                        )
                                .setId("6d2730c7-4356-4e7b-8cf0-bf5923692294")
                                .setNumberKeys(12)
                ))
                .setLastSynchronization(Instant.ofEpochSecond(1606760823))
                .setReview(null);
    }

    public static WorkspaceEntity developI18nToolsGit() {
        return new WorkspaceEntity(I18N_TOOL_GIT_ID, "develop")
                .setId(DEVELOP_I18N_TOOL_GIT_ID)
                .setStatus(WorkspaceStatus.IN_REVIEW)
                .setFiles(asList(
                        new BundleFileEntity(
                                "exception",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/exception_fr.properties")
                                                .setId("fae4868b-b4bd-4d92-8bfc-316725a2df1d"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/exception_en.properties")
                                                .setId("a72fb0fa-8392-48db-939b-2c5790e88482")
                                )
                        )
                                .setId("33ddec87-e1bd-4fa7-9c98-d06bdf5fab8f")
                                .setNumberKeys(56),
                        new BundleFileEntity(
                                "validation",
                                "server/src/main/resources/i18n",
                                BundleType.JAVA_PROPERTIES,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "server/src/main/resources/i18n/validation_fr.properties")
                                                .setId("d875d337-41d8-4267-a435-52087514c553"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "server/src/main/resources/i18n/validation_en.properties")
                                                .setId("eae40153-6249-499f-8c02-d13c262d72d8")
                                )
                        )
                                .setId("f83dc589-7744-4a68-874a-5edac8ec9b97")
                                .setNumberKeys(16),
                        new BundleFileEntity(
                                "i18n",
                                "front/src/main/web/src/assets/i18n",
                                BundleType.JSON_ICU,
                                asList(
                                        new BundleFileEntryEntity(FRANCAIS_ID, "front/src/main/web/src/assets/i18n/fr.properties")
                                                .setId("daf785b0-5425-4c5f-8054-6188a804d87e"),
                                        new BundleFileEntryEntity(ENGLISH_ID, "front/src/main/web/src/assets/i18n/en.properties")
                                                .setId("62de2153-a990-4718-be5e-9435e226bc7c")
                                )
                        )
                                .setId("a5f03753-c8a4-4fce-8bc7-23b0b7eb899a")
                                .setNumberKeys(16)
                ))
                .setLastSynchronization(Instant.ofEpochSecond(1606760823))
                .setReview(new GitHubReviewEntity("develop_i18n_2020-11-08", 60));
    }

    public static WorkspaceEntity release20208I18nToolsGit() {
        return new WorkspaceEntity(I18N_TOOL_GIT_ID, "release/2020.08")
                .setId(RELEASE_2020_08_I18N_TOOL_GIT_ID)
                .setStatus(WorkspaceStatus.NOT_INITIALIZED)
                .setReview(null);
    }
}

package be.sgerard.poc.githuboauth.model.i18n;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "translation_bundle_file")
public class TranslationBundleFileEntity {

    @Id
    private String id;

    @ManyToOne(optional = false)
    private TranslationWorkspaceEntity workspace;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String location;

    @Version
    private int version;

    TranslationBundleFileEntity() {
    }

    public TranslationBundleFileEntity(TranslationWorkspaceEntity workspace, String name, String location) {
        this.id = UUID.randomUUID().toString();
        this.workspace = workspace;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TranslationWorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(TranslationWorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

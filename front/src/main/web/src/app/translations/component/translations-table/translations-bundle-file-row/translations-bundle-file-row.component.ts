import {Component, Input} from '@angular/core';
import {BundleFile, BundleType} from "../../../model/workspace/bundle-file.model";

@Component({
    selector: 'app-translations-bundle-file-row',
    templateUrl: './translations-bundle-file-row.component.html',
    styleUrls: ['./translations-bundle-file-row.component.css']
})
export class TranslationsBundleFileRowComponent {

    @Input() public bundleFile: BundleFile;

    constructor() {
    }

    public get fileTypeClass(): string {
        if (!this.bundleFile) {
            return "";
        }

        switch (this.bundleFile.type) {
            case BundleType.JAVA_PROPERTIES:
                return 'app-icon-java-file';
            case BundleType.JSON_ICU:
                return 'app-icon-json-file';
            default:
                return '';
        }
    }

    public get name(): string {
        if (!this.bundleFile) {
            return "";
        }

        switch (this.bundleFile.type) {
            case BundleType.JSON_ICU:
                return `${this.bundleFile.location}`;
            case BundleType.JAVA_PROPERTIES:
            default:
                return `${this.bundleFile.location}/${this.bundleFile.name}`;
        }
    }
}

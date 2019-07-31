import {BundleFile} from './bundle-file.model';

export class BundleKeysPage {

    files: BundleFile[];
    lastKey: String;
    workspaceId: String;

    constructor(bundleKeysPage: BundleKeysPage = <BundleKeysPage>{}) {
        this.files = bundleKeysPage.files.map(file => new BundleFile(file));
        this.lastKey = bundleKeysPage.lastKey;
        this.workspaceId = bundleKeysPage.workspaceId;
    }
}

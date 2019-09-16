import {BundleFile} from './bundle-file.model';

export class BundleKeysPage {

    files: BundleFile[];
    lastKey: String;
    workspaceId: String;

    constructor(bundleKeysPage: BundleKeysPage = <BundleKeysPage>{}) {
        Object.assign(this, bundleKeysPage);
        this.files = bundleKeysPage.files.map(file => new BundleFile(file));
    }
}

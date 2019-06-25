import {BundleKey} from "./bundle-key.model";

export class BundleFile {

    id: String;
    keys: BundleKey[];
    location: String;
    name: String;

    constructor(bundleFile: BundleFile = <BundleFile>{}) {
        Object.assign(this, bundleFile);
        this.keys = bundleFile.keys.map(key => new BundleKey(key));
    }
}

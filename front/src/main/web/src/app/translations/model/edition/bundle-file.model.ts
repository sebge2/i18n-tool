import {BundleKey} from "./bundle-key.model";

export class BundleFile {

    id: String;
    keys: BundleKey[];
    location: String;
    name: String;

    constructor(bundleFile: BundleFile = <BundleFile>{}) {
        this.id = bundleFile.id;
        this.keys = bundleFile.keys.map(key => new BundleKey(key));
        this.location = bundleFile.location;
        this.name = bundleFile.name;
    }
}

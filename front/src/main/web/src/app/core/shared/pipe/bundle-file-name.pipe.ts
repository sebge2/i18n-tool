import {Pipe, PipeTransform} from '@angular/core';
import {BundleFile, BundleType} from "../../../translations/model/workspace/bundle-file.model";

@Pipe({
    name: 'bundleFileName'
})
export class BundleFileNamePipe implements PipeTransform {

    transform(bundleFile: BundleFile): unknown {
        switch (bundleFile.type) {
            case BundleType.JSON_ICU:
                return `${bundleFile.location}`;
            case BundleType.JAVA_PROPERTIES:
            default:
                return `${bundleFile.location}/${bundleFile.name}`;
        }
    }

}

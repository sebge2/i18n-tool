import { Pipe, PipeTransform } from '@angular/core';
import { BundleFile, BundleType } from '../model/workspace/bundle-file.model';

@Pipe({
  name: 'bundleFileName',
})
export class BundleFileNamePipe implements PipeTransform {
  transform(bundleFile: BundleFile): string {
    if (!bundleFile) {
      return '';
    }

    switch (bundleFile.type) {
      case BundleType.JSON_ICU:
        return `${bundleFile.location}`;
      case BundleType.JAVA_PROPERTIES:
      default:
        return `${bundleFile.location}/${bundleFile.name}`;
    }
  }
}

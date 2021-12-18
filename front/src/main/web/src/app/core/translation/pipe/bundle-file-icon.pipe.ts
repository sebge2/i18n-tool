import { Pipe, PipeTransform } from '@angular/core';
import { BundleFile, BundleType } from '../model/workspace/bundle-file.model';

@Pipe({
  name: 'bundleFileIcon',
})
export class BundleFileIconPipe implements PipeTransform {
  transform(bundleFile: BundleFile): string {
    if (!bundleFile) {
      return '';
    }

    switch (bundleFile.type) {
      case BundleType.JAVA_PROPERTIES:
        return 'app-icon-java-file';
      case BundleType.JSON_ICU:
        return 'app-icon-json-file';
      default:
        return '';
    }
  }
}

import { BundleConfigurationDto } from '../../../../api';

export class BundleConfiguration {
  static fromDto(dto: BundleConfigurationDto): BundleConfiguration {
    return new BundleConfiguration(dto.includedPaths, dto.ignoredPaths);
  }

  constructor(public includedPaths: string[], public ignoredPaths: string[]) {}
}

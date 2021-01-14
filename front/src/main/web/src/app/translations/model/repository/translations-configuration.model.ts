import {BundleConfiguration} from "./bundle-configuration.model";
import {TranslationsConfigurationDto} from "../../../api";

export class TranslationsConfiguration {

    public static fromDto(dto: TranslationsConfigurationDto): TranslationsConfiguration {
        return new TranslationsConfiguration(
            dto.ignoredKeys,
            BundleConfiguration.fromDto(dto.jsonIcu),
            BundleConfiguration.fromDto(dto.javaProperties),
        );
    }

    constructor(public ignoredKeys: string[],
                public jsonConfiguration: BundleConfiguration,
                public javaPropertiesConfiguration: BundleConfiguration) {
    }
}
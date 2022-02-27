import {ExternalTranslatorConfig} from "./external-translator-config.model";
import {ExternalTranslatorGenericRestConfigDto} from "../../api/model/externalTranslatorGenericRestConfigDto";
import {HttpMethod} from "@i18n-core-shared";

export class ExternalTranslatorGenericRestConfig extends ExternalTranslatorConfig {

    public static fromDto(dto: ExternalTranslatorGenericRestConfigDto): ExternalTranslatorGenericRestConfig {
        return new ExternalTranslatorGenericRestConfig(
            dto.id,
            dto.label,
            dto.linkUrl,
            dto.method as HttpMethod,
            dto.url,
            dto.queryParameters,
            dto.queryHeaders,
            dto.queryExtractor,
            dto.bodyTemplate
        )
    }

    constructor(
        public id: string,
        public label: string,
        public linkUrl: string,
        public method: HttpMethod,
        public url: string,
        public queryParameters: { [key: string]: string; },
        public queryHeaders: { [key: string]: string; },
        public queryExtractor: string,
        public bodyTemplate?: string,
    ) {
        super(id, label, linkUrl);
    }

    toDto(): ExternalTranslatorGenericRestConfigDto {
        return {
            id: this.id,
            label: this.label,
            linkUrl: this.linkUrl,
            method: this.method,
            url: this.url,
            queryParameters: this.queryParameters,
            queryHeaders: this.queryHeaders,
            queryExtractor: this.queryExtractor,
            bodyTemplate: this.bodyTemplate
        };
    }
}
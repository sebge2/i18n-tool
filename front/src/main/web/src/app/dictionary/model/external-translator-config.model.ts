import {ExternalTranslatorConfigDto} from "../../api/model/externalTranslatorConfigDto";

export abstract class ExternalTranslatorConfig {

    protected constructor(public id: string,
                          public label: string,
                          public linkUrl: string) {
    }

    abstract toDto(): ExternalTranslatorConfigDto;
}
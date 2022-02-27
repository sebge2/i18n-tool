import {Injectable} from '@angular/core';
import {SynchronizedCollection} from "@i18n-core-shared";
import {ExternalTranslatorConfigDto} from "../../api/model/externalTranslatorConfigDto";
import {ExternalTranslatorConfig} from "../model/external-translator-config.model";
import {Events, EventService} from "@i18n-core-event";
import {ExternalTranslatorService as ApiExternalTranslatorService,} from '../../api';
import {Observable} from "rxjs";
import * as _ from "lodash";
import {ExternalTranslatorGenericRestConfig} from "../model/external-translator-generic-rest-config.model";
import {ExternalTranslatorGenericRestConfigDto} from "../../api/model/externalTranslatorGenericRestConfigDto";
import {map} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class ExternalTranslatorService {

    private readonly _synchronizedConfigs: SynchronizedCollection<ExternalTranslatorConfigDto, ExternalTranslatorConfig>;

    constructor(private _eventService: EventService,
                private _apiExternalTranslatorService: ApiExternalTranslatorService) {
        this._synchronizedConfigs = new SynchronizedCollection<ExternalTranslatorConfigDto, ExternalTranslatorConfig>(
            () => _apiExternalTranslatorService.findAll5(),
            this._eventService.subscribeDto(Events.ADDED_EXTERNAL_TRANSLATOR_CONFIG),
            this._eventService.subscribeDto(Events.UPDATED_EXTERNAL_TRANSLATOR_CONFIG),
            this._eventService.subscribeDto(Events.DELETED_EXTERNAL_TRANSLATOR_CONFIG),
            this._eventService.reconnected(),
            (dto) => ExternalTranslatorService.fromDto(dto),
            (first, second) => first.id === second.id
        );
    }

    getConfigs$(): Observable<ExternalTranslatorConfig[]> {
        return this._synchronizedConfigs.collection;
    }

    createConfig$(config: ExternalTranslatorGenericRestConfigDto): Observable<ExternalTranslatorConfig> {
        return this._apiExternalTranslatorService
            .createTranslatorConfig(config)
            .pipe(map(dto => ExternalTranslatorService.fromDto(dto)));
    }

    createGoogleConfig$(apiKey: string): Observable<ExternalTranslatorConfig> {
        return this._apiExternalTranslatorService
            .createGoogleTranslatorConfig({apiKey: apiKey})
            .pipe(map(dto => ExternalTranslatorService.fromDto(dto)));
    }

    createAzureConfig$(subscriptionKey: string, subscriptionRegion: string): Observable<ExternalTranslatorConfig> {
        return this._apiExternalTranslatorService
            .createAzureTranslatorConfig({subscriptionRegion: subscriptionRegion, subscriptionKey: subscriptionRegion})
            .pipe(map(dto => ExternalTranslatorService.fromDto(dto)));
    }

    createITranslateConfig$(bearerToken: string): Observable<ExternalTranslatorConfig> {
        return this._apiExternalTranslatorService
            .createITranslateConfig({bearerToken: bearerToken})
            .pipe(map(dto => ExternalTranslatorService.fromDto(dto)));
    }

    update$(config: ExternalTranslatorConfig): Observable<ExternalTranslatorConfig> {
        return this._apiExternalTranslatorService
            .update1(config.toDto(), config.id)
            .pipe(map(dto => ExternalTranslatorService.fromDto(dto)));
    }

    delete$(id: string): Observable<any> {
        return this._apiExternalTranslatorService
            .delete4(id);
    }

    private static fromDto(dto: ExternalTranslatorConfigDto): ExternalTranslatorConfig {
        if (_.isNil(dto)) {
            return null;
        } else if (dto.type === 'EXTERNAL_GENERIC_REST') {
            return ExternalTranslatorGenericRestConfig.fromDto(<ExternalTranslatorGenericRestConfigDto>dto);
        } else {
            throw new Error(`Unsupported external translator config [${dto.type}].`);
        }
    }

    private static toDto(config: ExternalTranslatorConfig): ExternalTranslatorConfigDto {
        if (_.isNil(config)) {
            return null;
        } else {
            return config.toDto();
        }
    }
}

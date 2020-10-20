import {ActivatedRoute, Params, Router} from "@angular/router";
import * as _ from 'lodash';
import {filterOutUnavailableElementsByKey} from "./collection-utils";

export function getRouterParams(paramName: string, params: Params): any[] {
    if (!_.has(params, paramName)) {
        return [];
    } else if (_.isArray(params[paramName])) {
        return params[paramName];
    } else {
        return [params[paramName]];
    }
}

export function getRouterParamsCollection<T extends { [key: string]: any }, K extends keyof T>(paramName: string,
                                                                                               params: Params,
                                                                                               availableValues: T[],
                                                                                               defaultValues: T[],
                                                                                               key: K): T[] {
    const routeParams = getRouterParams(paramName, params);

    if (_.some(routeParams)) {
        return filterOutUnavailableElementsByKey(availableValues, routeParams, key);
    } else {
        return defaultValues;
    }
}

export function getRouteParamEnum<T extends { [key: number]: string | number }>(paramName: string, type: T, defaultValue: keyof T, params: Params): T[keyof T] | undefined {
    const routeParams = getRouterParams(paramName, params);

    if (_.some(routeParams) && _.has(type, routeParams[0])) {
        return type[<keyof T>routeParams[0]];
    } else {
        return type[defaultValue];
    }
}

export function updateRouteParams(rawParams: [string, string[]][], route: ActivatedRoute, router: Router): Promise<any> {
    let params: Params = {};

    for (const param of rawParams) {
        params = _.set(params, param[0], param[1]);
    }

    return router.navigate(
        [],
        {
            relativeTo: route,
            queryParams: params,
            queryParamsHandling: 'merge',
        });
}

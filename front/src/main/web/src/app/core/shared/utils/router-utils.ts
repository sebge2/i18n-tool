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

export function getRouteParamsCollection<T extends { [key: string]: any }, K extends keyof T>(paramName: string,
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

export function getRouteParamSimpleValue<T>(paramName: string,
                                            params: Params,
                                            defaultValue: T,
                                            mapper?: (rawValue: string) => T): T | undefined {
    const routeParams = getRouterParams(paramName, params);

    if (_.some(routeParams)) {
        return mapper ? mapper(routeParams[0]) : routeParams[0];
    } else {
        return defaultValue;
    }
}

export function getRouteParamObject<T extends { [key: string]: any }, K extends keyof T>(paramName: string,
                                                                                         params: Params,
                                                                                         availableValues: T[],
                                                                                         defaultValue: T,
                                                                                         key: K): T | undefined {
    const routeParams = getRouterParams(paramName, params);

    if (_.some(routeParams)) {
        const filteredElements = filterOutUnavailableElementsByKey(availableValues, routeParams, key);

        return _.some(filteredElements)
            ? filteredElements[0]
            : null;
    } else {
        return defaultValue;
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

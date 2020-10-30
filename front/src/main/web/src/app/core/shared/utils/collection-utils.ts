import * as _ from 'lodash';

export function filterOutUnavailableElements<T, K extends keyof T>(availableElements: T[],
                                                                   actualElements: T[],
                                                                   key: K): T[] {
    return actualElements
        .filter(actualElement =>
            _.some(availableElements, availableElement => _.eq(_.get(availableElement, key), _.get(actualElement, key)))
        );
}

export function filterOutUnavailableElementsByKey<T extends { [key: string]: any }, K extends keyof T>(availableElements: T[],
                                                                                                       actualElementsIds: string[],
                                                                                                       key: K): T[] {
    return actualElementsIds
        .map(actualElementId => _.find(availableElements, availableElement => _.eq(_.get(availableElement, key), actualElementId)))
        .filter(element => !!element);
}

export function mapAll<KV, T extends { [key: string]: any }, K extends keyof T>(collection: T[], key: K): KV[] {
    return _.isNil(collection)
        ? null
        : collection.map(element => <KV>(_.get(element, key)));
}

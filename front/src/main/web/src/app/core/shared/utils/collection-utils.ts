import * as _ from 'lodash';

export function filterOutUnavailableElements<T, K extends keyof T>(availableElements: T[],
                                                                   actualElements: T[],
                                                                   key: K): T[] {
    return _.filter(
        actualElements,
        actualElement =>
            _.some(
                availableElements,
                    availableElement => _.eq(_.get(availableElement, key), _.get(actualElement, key))
            )
    );
}

export function filterOutUnavailableElementsByKey<T extends { [key: string]: any }, K extends keyof T>(availableElements: T[],
                                                                                                       actualElementsIds: string[],
                                                                                                       key: K): T[] {
    return _.filter(
        _.map(
            actualElementsIds,
            actualElementId =>
                _.find(availableElements,
                    availableElement => _.eq(_.get(availableElement, key), actualElementId)
                )
        ),
        element => !!element
    );
}

export function mapAll<KV, T extends { [key: string]: any }, K extends keyof T>(collection: T[], key: K): KV[] {
    return _.map(collection, element => <KV>(_.get(element, key)));
}

export function mapToSingleton<KV, T extends { [key: string]: any }, K extends keyof T>(object: T, key: K): KV[] {
    return _.isNil(object)
        ? []
        : [<KV>(_.get(object, key))];
}

import * as _ from "lodash";

export function updateOriginalCollection<E>(originalElements: E[], updatedElements: E[], field: keyof E): E[] {
    return updatedElements
        .filter(updatedElement =>
            _.some(originalElements, originalElement =>
                _.isEqual(
                    _.get(originalElement, field),
                    _.get(updatedElement, field)
                )
            )
        );
}

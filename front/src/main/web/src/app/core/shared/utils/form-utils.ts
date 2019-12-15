import {AbstractControl} from "@angular/forms";
import * as _ from "lodash";

export function getStringValue(formControl: AbstractControl): string {
    let value: string = formControl.value;

    if (_.isEmpty(value)) {
        return null;
    }

    value = value.trim();
    if (_.isEmpty(value)) {
        return null;
    }

    return value;
}

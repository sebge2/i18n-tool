import {FormArray, FormGroup} from "@angular/forms";
import {CellType} from "./cell-type.model";

export class ColumnDefinition {
    columnDef: string;
    header: string;
    cell: (FormArray) => (FormGroup | string);
    cellType: (FormArray) => CellType;

    constructor(columnDef: string,
                header: string,
                cell: (FormArray) => (FormGroup | string),
                cellType: (FormArray) => CellType) {
        this.columnDef = columnDef;
        this.header = header;
        this.cell = cell;
        this.cellType = cellType;
    }

    // TODO improve the number of calls, pipe?

    isEmpty(formArray: FormArray): boolean {
        return this.cellType(formArray) == CellType.EMPTY;
    }

    isTranslation(formArray: FormArray): boolean {
        return this.cellType(formArray) == CellType.TRANSLATION;
    }

    isKey(formArray: FormArray): boolean {
        return this.cellType(formArray) == CellType.KEY;
    }
}

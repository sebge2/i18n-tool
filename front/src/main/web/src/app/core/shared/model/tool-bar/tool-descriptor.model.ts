import {Type} from "@angular/core";

export const TOOL_DESCRIPTOR_PRIORITIES: { [key: string]: number } = {
    'DICTIONARY': 0
};

export class ToolDescriptor {

    constructor(public readonly id: string,
                public readonly priority: number,
                public readonly fontIcon: string,
                public readonly title: string,
                public readonly description: string,
                public readonly component: Type<any>) {
    }
}
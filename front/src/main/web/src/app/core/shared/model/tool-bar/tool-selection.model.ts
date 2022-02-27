import {ToolDescriptor} from './tool-descriptor.model';

export class ToolSelection {

    constructor(public toolDescriptor: ToolDescriptor,
                public properties: { [key: string]: string } = {}) {
    }
}
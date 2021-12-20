export class ToolSelectionRequest {

    constructor(public toolId: string,
                public properties: { [key: string]: string } = {}) {
    }

}
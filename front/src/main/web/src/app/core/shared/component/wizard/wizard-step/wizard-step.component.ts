import {Component, Input, TemplateRef, ViewChild} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-wizard-step',
    templateUrl: './wizard-step.component.html',
    styleUrls: ['./wizard-step.component.css']
})
export class WizardStepComponent {

    @Input() public form: FormGroup;
    @Input() public name: String;
    @Input() public editable: boolean = true;
    @Input() public nextVisible: boolean = true;
    @Input() public previousVisible: boolean = true;

    @ViewChild(TemplateRef, {static: false}) template: TemplateRef<any>;

    constructor() {
    }

    public get previousAllowed(): boolean {
      return true;
    }

    public get nextAllowed(): boolean {
        return this.form.valid;
    }

}

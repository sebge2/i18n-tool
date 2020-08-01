import {AfterContentInit, Component, ContentChildren, Input, OnInit, QueryList, ViewChild} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {WizardStepComponent} from "./wizard-step/wizard-step.component";
import {MatStepper} from "@angular/material/stepper";

@Component({
    selector: 'app-wizard',
    templateUrl: './wizard.component.html',
    styleUrls: ['./wizard.component.css']
})
export class WizardComponent implements OnInit, AfterContentInit {

    @Input() public form: FormGroup;
    @ContentChildren(WizardStepComponent) public stepComponents: QueryList<WizardStepComponent>;
    @ViewChild('stepper', {static: true}) public stepper: MatStepper;

    public steps: WizardStepComponent[] = [];

    constructor() {
    }

    public ngOnInit() {
    }

    public ngAfterContentInit(): void {
        setTimeout(() => {
            this.steps = this.stepComponents.toArray();
        });
    }

    public get selectedIndex(): number {
        return this.stepper.selectedIndex;
    }

    public nextStep() {
        this.stepper.next();
    }
}

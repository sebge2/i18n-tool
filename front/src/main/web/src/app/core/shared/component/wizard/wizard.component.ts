import {
    AfterContentInit,
    Component,
    ContentChildren,
    EventEmitter,
    Input, OnDestroy,
    OnInit,
    Output,
    QueryList,
    ViewChild
} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {WizardStepComponent} from "./wizard-step/wizard-step.component";
import {MatStepper} from "@angular/material/stepper";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

export class StepChangeEvent {
    public constructor(public originalStepIndex: number, public nextStepIndex: number){
    }
}

@Component({
    selector: 'app-wizard',
    templateUrl: './wizard.component.html',
    styleUrls: ['./wizard.component.css']
})
export class WizardComponent implements OnInit, OnDestroy, AfterContentInit {

    @Input() public form: FormGroup;
    @Output() public stepChange = new EventEmitter<StepChangeEvent>();
    @Output() public close = new EventEmitter<void>();

    @ContentChildren(WizardStepComponent) public stepComponents: QueryList<WizardStepComponent>;
    @ViewChild('stepper', {static: true}) public stepper: MatStepper;

    public steps: WizardStepComponent[] = [];

    private _destroyed$ = new Subject<void>();

    constructor() {
    }

    public ngOnInit() {
        this.stepper.selectionChange
            .pipe(takeUntil(this._destroyed$))
            .subscribe(change => this.stepChange.emit(new StepChangeEvent(change.previouslySelectedIndex, change.selectedIndex)));
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public ngAfterContentInit(): void {
        setTimeout(() => {
            this.steps = this.stepComponents.toArray();
        });
    }

    public get selectedIndex(): number {
        return this.stepper.selectedIndex;
    }

    public get previousAllowed(): boolean {
        return (this.selectedIndex > 0) && this.steps[this.selectedIndex - 1].editable;
    }

    public get nextAllowed(): boolean {
        return this.selectedIndex + 1 < this.steps.length;
    }

    public nextStep() {
        this.stepper.next();
    }

    public onClose() {
        this.close.emit();
    }
}

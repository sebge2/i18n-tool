import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-wizard-step',
  templateUrl: './wizard-step.component.html',
  styleUrls: ['./wizard-step.component.css']
})
export class WizardStepComponent implements OnInit {

  @Input() public form: FormGroup;
  @Input() public name: String;
  @Input() public editable : boolean = true;
  @Input() public nextVisible : boolean = true;

  @ViewChild(TemplateRef, {static: false}) template: TemplateRef<any>;

  constructor() { }

  ngOnInit() {
  }

}

import {Component, Input, OnInit} from '@angular/core';
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {expectedNameValidator} from "../../workspace-table/confirm-deletion/confirm-workspace-deletion.component";

@Component({
  selector: 'app-locale-view-card',
  templateUrl: './locale-view-card.component.html',
  styleUrls: ['./locale-view-card.component.css']
})
export class LocaleViewCardComponent implements OnInit {

  private _locale: TranslationLocale;

  public readonly form: FormGroup;
  public loading: boolean = false;

  constructor(private formBuilder: FormBuilder) {
    this.form = this.formBuilder.group(
        {
          language: this.formBuilder.control('', [Validators.required]),
          region: this.formBuilder.control('', []),
          variants: this.formBuilder.control('', []),
          icon: this.formBuilder.control('', [Validators.required]), // TODO validators
        }
    );
  }

  ngOnInit() {
  }

  @Input()
  public get locale(){
    return this._locale;
  }

  public set locale(value: TranslationLocale){
    this._locale = value;

    this.form.controls['language'].setValue(this.locale.language);
    this.form.controls['region'].setValue(this.locale.region);
    this.form.controls['variants'].setValue(this.locale.variants);
    this.form.controls['icon'].setValue(this.locale.icon);
  }

  public get iconClass() : string {
    return `flag-icon ${this.form.controls['icon'].value}`;
  }

  public onSave() {
    this.loading = true;
  }
}

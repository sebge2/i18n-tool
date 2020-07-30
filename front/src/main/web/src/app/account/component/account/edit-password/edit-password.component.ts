import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-edit-password',
  templateUrl: './edit-password.component.html',
  styleUrls: ['./edit-password.component.css']
})
export class EditPasswordComponent implements OnInit {

  public readonly form: FormGroup;
  public loading = false;

  constructor(private formBuilder: FormBuilder) {
    this.form = this.formBuilder.group(
        {
          currentPassword: this.formBuilder.control('', [Validators.required]),
          newPassword: this.formBuilder.control('', [Validators.minLength(6), Validators.required]),
          confirmedPassword: this.formBuilder.control('', []) // check confirmed
        }
    );
  }

  public ngOnInit() {
  }

  public onSave() {

  }

  public resetForm() {

  }
}

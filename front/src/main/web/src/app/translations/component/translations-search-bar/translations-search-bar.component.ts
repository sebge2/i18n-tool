import { Component, OnInit } from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-translations-search-bar',
  templateUrl: './translations-search-bar.component.html',
  styleUrls: ['./translations-search-bar.component.css']
})
export class TranslationsSearchBarComponent implements OnInit {

  formGroup : FormGroup;

  constructor() { }

  ngOnInit() {
  }

}

import { Component, OnInit } from '@angular/core';
import {TranslationLocaleService} from "../../../translations/service/translation-locale.service";

@Component({
  selector: 'app-locales',
  templateUrl: './locales.component.html',
  styleUrls: ['./locales.component.css']
})
export class LocalesComponent implements OnInit {

  constructor(public translationLocaleService: TranslationLocaleService) { }

  ngOnInit() {
  }

}

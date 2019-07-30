import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TranslationsSearchCriterion} from "../../../model/translations-search-criterion.model";

@Component({
  selector: 'app-translation-criterion-selector',
  templateUrl: './translation-criterion-selector.component.html',
  styleUrls: ['./translation-criterion-selector.component.css']
})
export class TranslationCriterionSelectorComponent implements OnInit {

  @Output() valueChange: EventEmitter<TranslationsSearchCriterion> = new EventEmitter<TranslationsSearchCriterion>();

  @Input()
  value: TranslationsSearchCriterion;

  availableCriterion = TranslationsSearchCriterion;

  constructor() {
  }

  ngOnInit() {
  }

}

import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {TranslationsSearchCriterion} from "../../../model/translations-search-criterion.model";

@Component({
  selector: 'app-translation-criterion-selector',
  templateUrl: './translation-criterion-selector.component.html',
  styleUrls: ['./translation-criterion-selector.component.css']
})
export class TranslationCriterionSelectorComponent implements OnInit {

  @Output('valueChange') valueChange: EventEmitter<TranslationsSearchCriterion> = new EventEmitter<TranslationsSearchCriterion>();

  private _value: TranslationsSearchCriterion;

  availableCriterion = TranslationsSearchCriterion;

  constructor() {
    this.value = TranslationsSearchCriterion.MISSING_TRANSLATIONS;
  }

  ngOnInit() {
  }

  get value(): TranslationsSearchCriterion {
    return this._value;
  }

  set value(criterion: TranslationsSearchCriterion) {
    this._value = criterion;
    this.valueChange.emit(this._value);
  }

}

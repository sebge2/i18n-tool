import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TranslationKey} from "@i18n-core-shared";

export interface CardSelectorItem {
    id: string;
    icon: string;
    label: string | TranslationKey;
}

@Component({
    selector: 'app-card-selector',
    templateUrl: './card-selector.component.html',
    styleUrls: ['./card-selector.component.scss']
})
export class CardSelectorComponent {

    @Input() items: CardSelectorItem[] = [];
    @Output() select = new EventEmitter<string>();

    constructor() {
    }

    onSelect(item: CardSelectorItem): void {
        this.select.emit(item.id);
    }
}

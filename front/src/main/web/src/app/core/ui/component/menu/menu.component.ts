import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatIconRegistry} from '@angular/material';
import {DomSanitizer} from '@angular/platform-browser';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit, OnDestroy {

  // private _categories: RecipeCategory[] = [];
  private categoriesSubscription: Subscription;

  constructor(private matIconRegistry: MatIconRegistry,
              private domSanitizer: DomSanitizer/*,
              private recipeService: RecipeService*/) {
    // this.categoriesSubscription = recipeService.getCategories().subscribe(
    //   (categories: RecipeCategory[]) => {
    //     for (const category of categories) {
    //       this.matIconRegistry
    //         .addSvgIcon(
    //           'icon_recipe_' + category.code,
    //           this.domSanitizer.bypassSecurityTrustResourceUrl(category.icon)
    //         );
    //     }
    //
    //     this._categories = categories;
    //   }
    // );
  }

  ngOnInit() {
    this.matIconRegistry
      .addSvgIcon(
        'icon_recipe_all',
        this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icon/all.svg')
      );
  }

  ngOnDestroy(): void {
    // this.categoriesSubscription.unsubscribe();
  }

  // get categories(): RecipeCategory[] {
  //   return this._categories;
  // }
}

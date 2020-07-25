import {Directive, HostBinding, Input} from '@angular/core';

@Directive({
  selector: 'img[default]',
  host: {
    '(error)': 'updateUrl()',
    '(load)': 'load()',
    '[src]': 'src'
  }
})
// https://medium.com/@sub.metu/angular-fallback-for-broken-images-5cd05c470f08
export class ImagePreloadDirective {
  @Input() src: string;
  @Input() default: string;

  public updateUrl() {
    this.src = this.default;
  }

  public load() {
  }
}

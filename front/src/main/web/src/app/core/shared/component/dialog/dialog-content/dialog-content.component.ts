import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dialog-content',
  template: '<ng-content></ng-content>'
})
export class DialogContentComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}

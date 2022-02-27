import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css'],
})
export class DialogComponent {
  @Input() public title: string;
  @Input() public dialogWidth: string;
  @Input() public dialogHeight: string;

  constructor() {}
}

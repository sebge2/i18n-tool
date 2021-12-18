import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-main-message',
  templateUrl: './main-message.component.html',
  styleUrls: ['./main-message.component.css'],
})
export class MainMessageComponent implements OnInit {
  @Input('title') title: String;
  @Input('subTitle') subTitle: String;

  constructor() {}

  ngOnInit() {}
}

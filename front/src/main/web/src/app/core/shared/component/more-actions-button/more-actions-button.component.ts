import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-more-actions-button',
  templateUrl: './more-actions-button.component.html',
  styleUrls: ['./more-actions-button.component.css']
})
export class MoreActionsButtonComponent implements OnInit {

  @Input() public disabled : boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}

import {Component, Input, OnInit} from '@angular/core';
import {ErrorMessagesDto} from "../../../../api";

@Component({
  selector: 'app-error-message-list',
  templateUrl: './error-message-list.component.html',
  styleUrls: ['./error-message-list.component.css']
})
export class ErrorMessageListComponent implements OnInit {

  @Input() public errorMessages: ErrorMessagesDto;
  @Input() public whiteFont = false;

  constructor() { }

  ngOnInit() {
  }

}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Repository} from "../../../../translations/model/repository.model";

@Component({
  selector: 'app-repository-view-card',
  templateUrl: './repository-view-card.component.html',
  styleUrls: ['./repository-view-card.component.css']
})
export class RepositoryViewCardComponent implements OnInit {

  @Input() public repository: Repository;
  @Output() public save = new EventEmitter<Repository>();

  constructor() { }

  ngOnInit() {
  }

}

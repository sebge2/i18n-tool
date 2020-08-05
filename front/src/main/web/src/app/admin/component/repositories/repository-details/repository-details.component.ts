import {Component, Input, OnInit} from '@angular/core';
import {Repository} from "../../../../translations/model/repository.model";

@Component({
  selector: 'app-repository-details',
  templateUrl: './repository-details.component.html',
  styleUrls: ['./repository-details.component.css']
})
export class RepositoryDetailsComponent implements OnInit {

  @Input() public repository: Repository;

  constructor() { }

  ngOnInit() {
  }

}

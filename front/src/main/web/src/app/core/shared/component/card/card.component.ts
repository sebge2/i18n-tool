import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.css']
})
export class CardComponent implements OnInit {

  @Input() public title : string;
  @Input() public subTitle : string;
  @Input() public headerMatIcon : string;
  @Input() public headerImg : string;
  @Input() public headerClassIcon : string;
  @Input() public maxSizePx : number;

  constructor() { }

  ngOnInit(): void {
  }

}

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-error-standard',
  templateUrl: './error-standard.component.html',
  styleUrls: ['./error-standard.component.css'],
})
export class ErrorStandardComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}

  private _statusCode: Observable<String>;

  ngOnInit() {
    this._statusCode = this.route.params.pipe(map((params: Params) => params['statusCode']));
  }

  get statusCode(): Observable<String> {
    return this._statusCode;
  }
}

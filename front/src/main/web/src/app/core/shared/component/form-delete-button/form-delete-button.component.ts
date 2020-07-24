import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'app-form-delete-button',
    templateUrl: './form-delete-button.component.html',
    styleUrls: ['./form-delete-button.component.css']
})
export class FormDeleteButtonComponent implements OnInit {

    @Input() public loading: boolean;
    @Output() public delete = new EventEmitter<void>();

    // public confirmationInProgress = false;
    // public progressbarValue = 100;
    // private curSec: number = 0;

    constructor() {
    }

    ngOnInit() {
    }

    public onDelete() {
        // this.confirmationInProgress = true;

        // this.startTimer(3);

        // TODO
        this.delete.emit();
    }


    // startTimer(seconds: number) {
    //   const timer$ = interval(100);
    //
    //   const sub = timer$.subscribe((ms) => {
    //     console.log(ms);
    //     this.progressbarValue = 100 - (ms * 1000  / seconds);
    //     this.curSec = ms;
    //
    //     if ((this.curSec * 10) === seconds) {
    //       sub.unsubscribe();
    //     }
    //   });
    // }
}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'app-countdown',
    templateUrl: './countdown.component.html',
    styleUrls: ['./countdown.component.css']
})
export class CountdownComponent implements OnInit {

    @Input() public delayInSec: number = 6;
    @Input() public size: number = 100;
    @Input() public color: string = 'black';
    @Output() public elapsed = new EventEmitter();

    public counter: number;
    public strokeWith: number = 8;

    constructor() {
    }

    public ngOnInit(): void {
        this.counter = 0;

        setInterval(
            () => {
                if (this.countDown > 1) {
                    this.counter++;
                } else {
                    this.elapsed.emit();
                }
            },
            1000
        );
    }

    public get countDown(): number {
        return this.delayInSec - this.counter;
    }

    public get strokeDashoffset(): number {
        // clockwize: this.circumference - ((this.counter) * (this.circumference / this.delayInSec));
        return this.circumference - ((this.countDown - 1) * (this.circumference / this.delayInSec));
    }

    public get radius(): number {
        return this.size / 2;
    }

    public get circumference(): number {
        return this.calculateCircumference(this.radius);
    }

    public calculateCircumference(radius: number): number {
        return 2 * Math.PI * radius;
    }

}

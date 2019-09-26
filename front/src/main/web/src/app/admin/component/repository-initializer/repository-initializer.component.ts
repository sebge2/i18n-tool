import {Component, OnDestroy, OnInit} from '@angular/core';
import {RepositoryService} from "../../../translations/service/repository.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {Repository} from "../../../translations/model/repository.model";

@Component({
    selector: 'app-repository-initializer',
    templateUrl: './repository-initializer.component.html',
    styleUrls: ['./repository-initializer.component.css']
})
export class RepositoryInitializerComponent implements OnInit, OnDestroy {

    repository: Repository;

    private destroy$ = new Subject();

    constructor(private repositoryService: RepositoryService) {
    }

    ngOnInit() {
        this.repositoryService.getRepository()
            .pipe(takeUntil(this.destroy$))
            .subscribe((repository: Repository) => {
                this.repository = repository;
            });
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    initialize(){
        this.repositoryService.initialize();
    }
}

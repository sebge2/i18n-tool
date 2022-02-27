import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Snapshot } from '../../../model/snapshot/snapshot.model';
import { Subject } from 'rxjs';
import { SnapshotService } from '../../../service/snapshot.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '@i18n-core-notification';
import * as _ from 'lodash';
import { AuthenticationService } from '@i18n-core-auth';

@Component({
  selector: 'app-snapshots',
  templateUrl: './snapshots.component.html',
  styleUrls: ['./snapshots.component.scss'],
})
export class SnapshotsComponent implements OnInit, OnDestroy {
  public readonly dataSource = new MatTableDataSource<Snapshot>();

  public restoreInProgress = false;
  public deleteInProgress = false;

  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _snapshotService: SnapshotService,
    private _notificationService: NotificationService,
    private _authenticationService: AuthenticationService
  ) {}

  ngOnInit(): void {
    this._snapshotService
      .getSnapshots()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((snapshots) => (this.dataSource.data = _.orderBy(snapshots, ['createdOn'], ['desc'])));
  }

  public ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  public onDownload(snapshot: Snapshot) {
    this._snapshotService.downloadSnapshot(snapshot);
  }

  public onRestore(snapshot: Snapshot) {
    this.restoreInProgress = true;

    this._snapshotService
      .restore(snapshot)
      .toPromise()
      .then(() => this._authenticationService.logout())
      .catch((error) => {
        console.error('Error while restoring the snapshot.', error);
        this._notificationService.displayErrorMessage('ADMIN.SNAPSHOTS.ERROR.RESTORE', error);
      })
      .finally(() => (this.restoreInProgress = false));
  }

  public onDelete(snapshot: Snapshot) {
    this.deleteInProgress = true;

    this._snapshotService
      .delete(snapshot)
      .toPromise()
      .catch((error) => {
        console.error('Error while deleting the snapshot.', error);
        this._notificationService.displayErrorMessage('ADMIN.SNAPSHOTS.ERROR.DELETE', error);
      })
      .finally(() => (this.deleteInProgress = false));
  }
}

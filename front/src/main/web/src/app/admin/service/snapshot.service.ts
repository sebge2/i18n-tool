import { Injectable } from '@angular/core';
import { SynchronizedCollection } from '@i18n-core-shared';
import { SnapshotDto, SnapshotService as ApiSnapshotService } from '../../api';
import { Observable, of } from 'rxjs';
import { Snapshot } from '../model/snapshot/snapshot.model';
import { Events } from '@i18n-core-event';
import { catchError, map, tap } from 'rxjs/operators';
import { EventService } from '@i18n-core-event';
import { NotificationService } from '@i18n-core-notification';

@Injectable({
  providedIn: 'root',
})
export class SnapshotService {
  private readonly _synchronizedSnapshots: SynchronizedCollection<SnapshotDto, Snapshot>;
  private readonly _snapshots$: Observable<Snapshot[]>;

  constructor(
    private apiSnapshotService: ApiSnapshotService,
    private eventService: EventService,
    private notificationService: NotificationService
  ) {
    this._synchronizedSnapshots = new SynchronizedCollection<SnapshotDto, Snapshot>(
      () => apiSnapshotService.findAll2(),
      this.eventService.subscribeDto(Events.ADDED_SNAPSHOT),
      of(),
      this.eventService.subscribeDto(Events.DELETED_SNAPSHOT),
      this.eventService.reconnected(),
      (dto) => Snapshot.fromDto(dto),
      (first, second) => first.id === second.id
    );

    this._snapshots$ = this._synchronizedSnapshots.collection.pipe(
      catchError((reason) => {
        console.error('Error while retrieving snapshots.', reason);
        this.notificationService.displayErrorMessage('ADMIN.SNAPSHOTS.ERROR.GET_ALL');
        return [];
      })
    );
  }

  public getSnapshots(): Observable<Snapshot[]> {
    return this._snapshots$;
  }

  public createSnapshot(comment?: string, encryptionPassword?: string): Observable<Snapshot> {
    return this.apiSnapshotService.create1({ comment: comment, encryptionPassword: encryptionPassword }).pipe(
      map((dto) => Snapshot.fromDto(dto)),
      tap((snapshot) => this._synchronizedSnapshots.add(snapshot))
    );
  }

  public downloadSnapshot(snapshot: Snapshot) {
    window.open(`/api/snapshot/${snapshot.id}/file`, '_blank');
  }

  public restore(snapshot: Snapshot): Observable<Snapshot> {
    return this.apiSnapshotService.restore(snapshot.id, 'RESTORE').pipe(map((dto) => Snapshot.fromDto(dto)));
  }

  public delete(snapshot: Snapshot): Observable<void> {
    return this.apiSnapshotService.delete2(snapshot.id).pipe(tap(() => this._synchronizedSnapshots.delete(snapshot)));
  }

  public importSnapshot(file: Blob, encryptionPassword?: string): Observable<Snapshot> {
    return this.apiSnapshotService.importZipForm(encryptionPassword, file).pipe(
      map((dto) => Snapshot.fromDto(dto)),
      tap((snapshot) => this._synchronizedSnapshots.add(snapshot))
    );
  }
}

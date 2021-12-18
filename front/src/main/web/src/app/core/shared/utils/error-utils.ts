import { ErrorMessagesDto } from '../../../api';
import { HttpErrorResponse } from '@angular/common/http';
import * as _ from 'lodash';

export function instanceOfErrorMessages(object: any): object is ErrorMessagesDto {
  return object && 'messages' in object && 'id' in object && 'time' in object;
}

export function instanceOfHttpError(error: any): error is HttpErrorResponse {
  return _.get(error, 'name') === 'HttpErrorResponse';
}

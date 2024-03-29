/**
 * i18n Tool
 * Web API of the i18n tool
 *
 * OpenAPI spec version: 1.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */ /* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse, HttpEvent } from '@angular/common/http';
import { CustomHttpUrlEncodingCodec } from '../encoder';

import { Observable } from 'rxjs';

import { BundleFileDto } from '../model/bundleFileDto';
import { ErrorMessagesDto } from '../model/errorMessagesDto';
import { WorkspaceDto } from '../model/workspaceDto';
import { WorkspacesPublishRequestDto } from '../model/workspacesPublishRequestDto';

import { BASE_PATH, COLLECTION_FORMATS } from '../variables';
import { Configuration } from '../configuration';

@Injectable()
export class WorkspaceService {
  protected basePath = 'http://127.0.0.1:8080';
  public defaultHeaders = new HttpHeaders();
  public configuration = new Configuration();

  constructor(
    protected httpClient: HttpClient,
    @Optional() @Inject(BASE_PATH) basePath: string,
    @Optional() configuration: Configuration
  ) {
    if (basePath) {
      this.basePath = basePath;
    }
    if (configuration) {
      this.configuration = configuration;
      this.basePath = basePath || configuration.basePath || this.basePath;
    }
  }

  /**
   * @param consumes string[] mime-types
   * @return true: consumes contains 'multipart/form-data', false: otherwise
   */
  private canConsumeForm(consumes: string[]): boolean {
    const form = 'multipart/form-data';
    for (const consume of consumes) {
      if (form === consume) {
        return true;
      }
    }
    return false;
  }

  /**
   * Deletes the workspace having the specified id.
   *
   * @param id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deleteWorkspace(id: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
  public deleteWorkspace(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
  public deleteWorkspace(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
  public deleteWorkspace(id: string, observe: any = 'body', reportProgress: boolean = false): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling deleteWorkspace.');
    }

    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = [];

    return this.httpClient.request<any>(
      'delete',
      `${this.basePath}/api/repository/workspace/${encodeURIComponent(String(id))}`,
      {
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      }
    );
  }

  /**
   * Publishes all modifications made on the specified workspace.
   *
   * @param id
   * @param message Message required when publishing, it describe the changes.
   * @param action
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public executeAction(
    id: string,
    message: string,
    action?: string,
    observe?: 'body',
    reportProgress?: boolean
  ): Observable<WorkspaceDto>;
  public executeAction(
    id: string,
    message: string,
    action?: string,
    observe?: 'response',
    reportProgress?: boolean
  ): Observable<HttpResponse<WorkspaceDto>>;
  public executeAction(
    id: string,
    message: string,
    action?: string,
    observe?: 'events',
    reportProgress?: boolean
  ): Observable<HttpEvent<WorkspaceDto>>;
  public executeAction(
    id: string,
    message: string,
    action?: string,
    observe: any = 'body',
    reportProgress: boolean = false
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling executeAction.');
    }

    if (message === null || message === undefined) {
      throw new Error('Required parameter message was null or undefined when calling executeAction.');
    }

    let queryParameters = new HttpParams({ encoder: new CustomHttpUrlEncodingCodec() });
    if (action !== undefined && action !== null) {
      queryParameters = queryParameters.set('action', <any>action);
    }
    if (message !== undefined && message !== null) {
      queryParameters = queryParameters.set('message', <any>message);
    }

    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = [];

    return this.httpClient.request<WorkspaceDto>(
      'post',
      `${this.basePath}/api/repository/workspace/${encodeURIComponent(String(id))}/do`,
      {
        params: queryParameters,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      }
    );
  }

  /**
   * Returns registered workspaces.
   *
   * @param id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public findAll4(id: string, observe?: 'body', reportProgress?: boolean): Observable<Array<WorkspaceDto>>;
  public findAll4(
    id: string,
    observe?: 'response',
    reportProgress?: boolean
  ): Observable<HttpResponse<Array<WorkspaceDto>>>;
  public findAll4(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<WorkspaceDto>>>;
  public findAll4(id: string, observe: any = 'body', reportProgress: boolean = false): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling findAll4.');
    }

    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = [];

    return this.httpClient.request<Array<WorkspaceDto>>(
      'get',
      `${this.basePath}/api/repository/${encodeURIComponent(String(id))}/workspace`,
      {
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      }
    );
  }

  /**
   * Returns registered workspaces.
   *
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public findAll5(observe?: 'body', reportProgress?: boolean): Observable<Array<WorkspaceDto>>;
  public findAll5(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<WorkspaceDto>>>;
  public findAll5(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<WorkspaceDto>>>;
  public findAll5(observe: any = 'body', reportProgress: boolean = false): Observable<any> {
    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = [];

    return this.httpClient.request<Array<WorkspaceDto>>('get', `${this.basePath}/api/repository/workspace`, {
      withCredentials: this.configuration.withCredentials,
      headers: headers,
      observe: observe,
      reportProgress: reportProgress,
    });
  }

  /**
   * Returns the workspace having the specified id.
   *
   * @param id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public findById3(id: string, observe?: 'body', reportProgress?: boolean): Observable<WorkspaceDto>;
  public findById3(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<WorkspaceDto>>;
  public findById3(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<WorkspaceDto>>;
  public findById3(id: string, observe: any = 'body', reportProgress: boolean = false): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling findById3.');
    }

    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = [];

    return this.httpClient.request<WorkspaceDto>(
      'get',
      `${this.basePath}/api/repository/workspace/${encodeURIComponent(String(id))}`,
      {
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      }
    );
  }

  /**
   * Returns bundle files composing the specified workspace.
   *
   * @param id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public findWorkspaceBundleFiles(
    id: string,
    observe?: 'body',
    reportProgress?: boolean
  ): Observable<Array<BundleFileDto>>;
  public findWorkspaceBundleFiles(
    id: string,
    observe?: 'response',
    reportProgress?: boolean
  ): Observable<HttpResponse<Array<BundleFileDto>>>;
  public findWorkspaceBundleFiles(
    id: string,
    observe?: 'events',
    reportProgress?: boolean
  ): Observable<HttpEvent<Array<BundleFileDto>>>;
  public findWorkspaceBundleFiles(id: string, observe: any = 'body', reportProgress: boolean = false): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling findWorkspaceBundleFiles.');
    }

    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = [];

    return this.httpClient.request<Array<BundleFileDto>>(
      'get',
      `${this.basePath}/api/repository/workspace/${encodeURIComponent(String(id))}/bundle-file`,
      {
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      }
    );
  }

  /**
   * Initializes the specified workspace.
   *
   * @param body
   * @param action
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public publish(
    body: WorkspacesPublishRequestDto,
    action?: string,
    observe?: 'body',
    reportProgress?: boolean
  ): Observable<Array<WorkspaceDto>>;
  public publish(
    body: WorkspacesPublishRequestDto,
    action?: string,
    observe?: 'response',
    reportProgress?: boolean
  ): Observable<HttpResponse<Array<WorkspaceDto>>>;
  public publish(
    body: WorkspacesPublishRequestDto,
    action?: string,
    observe?: 'events',
    reportProgress?: boolean
  ): Observable<HttpEvent<Array<WorkspaceDto>>>;
  public publish(
    body: WorkspacesPublishRequestDto,
    action?: string,
    observe: any = 'body',
    reportProgress: boolean = false
  ): Observable<any> {
    if (body === null || body === undefined) {
      throw new Error('Required parameter body was null or undefined when calling publish.');
    }

    let queryParameters = new HttpParams({ encoder: new CustomHttpUrlEncodingCodec() });
    if (action !== undefined && action !== null) {
      queryParameters = queryParameters.set('action', <any>action);
    }

    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = ['application/json'];
    const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
    if (httpContentTypeSelected != undefined) {
      headers = headers.set('Content-Type', httpContentTypeSelected);
    }

    return this.httpClient.request<Array<WorkspaceDto>>('post', `${this.basePath}/api/repository/workspace/do`, {
      body: body,
      params: queryParameters,
      withCredentials: this.configuration.withCredentials,
      headers: headers,
      observe: observe,
      reportProgress: reportProgress,
    });
  }

  /**
   * Executes an action on workspaces of a particular repository.
   *
   * @param repositoryId
   * @param action
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public synchronize(
    repositoryId: string,
    action?: string,
    observe?: 'body',
    reportProgress?: boolean
  ): Observable<Array<WorkspaceDto>>;
  public synchronize(
    repositoryId: string,
    action?: string,
    observe?: 'response',
    reportProgress?: boolean
  ): Observable<HttpResponse<Array<WorkspaceDto>>>;
  public synchronize(
    repositoryId: string,
    action?: string,
    observe?: 'events',
    reportProgress?: boolean
  ): Observable<HttpEvent<Array<WorkspaceDto>>>;
  public synchronize(
    repositoryId: string,
    action?: string,
    observe: any = 'body',
    reportProgress: boolean = false
  ): Observable<any> {
    if (repositoryId === null || repositoryId === undefined) {
      throw new Error('Required parameter repositoryId was null or undefined when calling synchronize.');
    }

    let queryParameters = new HttpParams({ encoder: new CustomHttpUrlEncodingCodec() });
    if (action !== undefined && action !== null) {
      queryParameters = queryParameters.set('action', <any>action);
    }

    let headers = this.defaultHeaders;

    // to determine the Accept header
    let httpHeaderAccepts: string[] = ['*/*'];
    const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    if (httpHeaderAcceptSelected != undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = [];

    return this.httpClient.request<Array<WorkspaceDto>>(
      'post',
      `${this.basePath}/api/repository/${encodeURIComponent(String(repositoryId))}/workspace/do`,
      {
        params: queryParameters,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      }
    );
  }
}

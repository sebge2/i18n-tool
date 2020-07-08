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
 *//* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent }                           from '@angular/common/http';
import { CustomHttpUrlEncodingCodec }                        from '../encoder';

import { Observable }                                        from 'rxjs';

import { ErrorMessagesDtoDto } from '../model/errorMessagesDtoDto';
import { WorkspaceDtoDto } from '../model/workspaceDtoDto';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class WorkspaceService {

    protected basePath = 'http://127.0.0.1:8080';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
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
    public deleteWorkspace(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling deleteWorkspace.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<any>('delete',`${this.basePath}/api/repository/workspace/${encodeURIComponent(String(id))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
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
    public findAll3(id: string, observe?: 'body', reportProgress?: boolean): Observable<Array<WorkspaceDto>>;
    public findAll3(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<WorkspaceDto>>>;
    public findAll3(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<WorkspaceDto>>>;
    public findAll3(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling findAll3.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<WorkspaceDto>>('get',`${this.basePath}/api/repository/${encodeURIComponent(String(id))}/workspace`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Returns registered workspaces.
     * 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public findAll4(observe?: 'body', reportProgress?: boolean): Observable<Array<WorkspaceDto>>;
    public findAll4(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<WorkspaceDto>>>;
    public findAll4(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<WorkspaceDto>>>;
    public findAll4(observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<WorkspaceDto>>('get',`${this.basePath}/api/repository/workspace`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Returns the workspace having the specified id.
     * 
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public findById4(id: string, observe?: 'body', reportProgress?: boolean): Observable<WorkspaceDto>;
    public findById4(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<WorkspaceDto>>;
    public findById4(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<WorkspaceDto>>;
    public findById4(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling findById4.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<WorkspaceDto>('get',`${this.basePath}/api/repository/workspace/${encodeURIComponent(String(id))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Executes an action on the specified workspace.
     * 
     * @param id 
     * @param message 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public initializeWorkspace1(id: string, message: string, observe?: 'body', reportProgress?: boolean): Observable<WorkspaceDto>;
    public initializeWorkspace1(id: string, message: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<WorkspaceDto>>;
    public initializeWorkspace1(id: string, message: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<WorkspaceDto>>;
    public initializeWorkspace1(id: string, message: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling initializeWorkspace1.');
        }

        if (message === null || message === undefined) {
            throw new Error('Required parameter message was null or undefined when calling initializeWorkspace1.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (message !== undefined && message !== null) {
            queryParameters = queryParameters.set('message', <any>message);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<WorkspaceDto>('post',`${this.basePath}/api/repository/workspace/${encodeURIComponent(String(id))}/do`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Executes an action on workspaces of a particular repository.
     * 
     * @param repositoryId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public synchronizeWorkspaces(repositoryId: string, observe?: 'body', reportProgress?: boolean): Observable<Array<WorkspaceDto>>;
    public synchronizeWorkspaces(repositoryId: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<WorkspaceDto>>>;
    public synchronizeWorkspaces(repositoryId: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<WorkspaceDto>>>;
    public synchronizeWorkspaces(repositoryId: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (repositoryId === null || repositoryId === undefined) {
            throw new Error('Required parameter repositoryId was null or undefined when calling synchronizeWorkspaces.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<WorkspaceDto>>('post',`${this.basePath}/api/repository/${encodeURIComponent(String(repositoryId))}/workspace/do`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}

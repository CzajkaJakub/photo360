import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Constants} from "../properties/properties";
import {environment} from "../../../environments/environment";

@Injectable()
export class NetworkService {

    constructor(private httpClient: HttpClient) {
    }

    /**
     *
     * @param url server url
     * @param headers custom header in request, if null passed, default will be loaded
     * @param params http params
     * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
     */
    sendGetRequest<T>(url: string, headers: HttpHeaders | null, params: Map<string, string> | null, extraPathVariable: number[] | string[] | null) {
        return this.httpClient.get<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), {
            headers: headers ?? Constants.headersApplicationJson,
            params: this.buildRequestParamWithVariables(params)
        })
    }

    /**
     *
     * @param url server url
     * @param body body of request
     * @param headers custom header in request, if null passed, default will be loaded
     * @param params http params
     * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
     */
    sendPostRequest<T>(url: string, body: any, headers: HttpHeaders | null, params: Map<string, string> | null, extraPathVariable: number[] | string[] | null) {
        return this.httpClient.post<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), body, {
            headers: headers ?? Constants.headersApplicationJson,
            params: this.buildRequestParamWithVariables(params)
        })
    }

    /**
     *
     * @param url server url
     * @param headers custom header in request, if null passed, default will be loaded
     * @param params http params
     * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
     */
    sendDeleteRequest<T>(url: string, headers: HttpHeaders | null, params: Map<string, string> | null, extraPathVariable: number[] | string[] | null) {
        return this.httpClient.delete<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), {
            headers: headers ?? Constants.headersApplicationJson,
            params: this.buildRequestParamWithVariables(params)
        })
    }

    /**
     *
     * @param url server url
     * @param body body of request
     * @param headers custom header in request, if null passed, default will be loaded
     * @param params http params
     * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
     */
    sendPutRequest<T>(url: string, body: any, headers: HttpHeaders | null, params: Map<string, string> | null, extraPathVariable: number[] | string[] | null) {
        return this.httpClient.put<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), body, {
            headers: headers ?? Constants.headersApplicationJson,
            params: this.buildRequestParamWithVariables(params)
        })
    }

    private buildRequestUrlWithVariables(url: string, extraPathVariables: number[] | string[] | null): string {
        extraPathVariables = extraPathVariables ?? []
        return environment.REST_APP_HOST.concat(url) + (extraPathVariables && extraPathVariables.length > 0 ? '/' + extraPathVariables.join('/') : '');
    }

    private buildRequestParamWithVariables(params: Map<string, string> | null): HttpParams {
        let httpParams = new HttpParams();
        if (params != null) {
            params.forEach((value, key) => {
                httpParams.set(key, value)
            });
        }
        return httpParams
    }
}

import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
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
   * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
   */
  sendGetRequest<T>(url: string, headers: HttpHeaders | null, extraPathVariable: number[] | string[] | null) {
    return this.httpClient.get<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), {
      headers: headers == null ? Constants.headersApplicationJson : headers,
    })
  }

  /**
   *
   * @param url server url
   * @param body body of request
   * @param headers custom header in request, if null passed, default will be loaded
   * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
   */
  sendPostRequest<T>(url: string, body: any, headers: HttpHeaders | null, extraPathVariable: number[] | string[] | null) {
    return this.httpClient.post<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), body, {
      headers: headers == null ? Constants.headersApplicationJson : headers,
    })
  }

  /**
   *
   * @param url server url
   * @param headers custom header in request, if null passed, default will be loaded
   * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
   */
  sendDeleteRequest<T>(url: string, headers: HttpHeaders | null, extraPathVariable: number[] | string[] | null) {
    return this.httpClient.delete<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), {
      headers: headers == null ? Constants.headersApplicationJson : headers,
    })
  }

  /**
   *
   * @param url server url
   * @param body body of request
   * @param headers custom header in request, if null passed, default will be loaded
   * @param extraPathVariable extra path variable, if not null passed, variable will be added into url -> (url/variable)
   */
  sendPutRequest<T>(url: string, body: any, headers: HttpHeaders | null, extraPathVariable: number[] | string[] | null) {
    return this.httpClient.put<T>(this.buildRequestUrlWithVariables(url, extraPathVariable), body, {
      headers: headers == null ? Constants.headersApplicationJson : headers,
    })
  }

  private buildRequestUrlWithVariables(url: string, extraPathVariables: number[] | string[] | null): string {
    extraPathVariables = extraPathVariables == null ? [] : extraPathVariables
    return environment.REST_APP_HOST.concat(url) + (extraPathVariables && extraPathVariables.length > 0 ? '/' + extraPathVariables.join('/') : '');
  }
}

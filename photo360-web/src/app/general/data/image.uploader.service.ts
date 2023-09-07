import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ToastComponent} from "../toast/toast.component";
import {environment} from "../../../environments/environment";
import {ConnectionConstants, Constants} from "../properties/properties";
import {catchError} from "rxjs";
import {ResponseStatusHandler} from "../response-status/response-status.service";
import {UploadImagesConfig} from "../interface/interface";

@Injectable()
export class ImageUploaderService {

  constructor(private httpClient: HttpClient,
              private toast: ToastComponent,
              private responseStatusHandler: ResponseStatusHandler) {
  }

  uploadFilesWithImages(uploadImagesConfig: UploadImagesConfig) {

    const formData = new FormData();
    formData.append('isPublic', uploadImagesConfig.isPublic.toString());
    formData.append('description', uploadImagesConfig.description.toString());
    formData.append('zipFile', uploadImagesConfig.zipFile, uploadImagesConfig.zipFile.name);

    return this.httpClient.post<any>(environment.REST_APP_HOST.concat(ConnectionConstants.uploadPhotosUrl), formData)
      .pipe(catchError((resultData) => this.responseStatusHandler.handleRequestError(resultData))
      ).subscribe(
        response => {
          this.responseStatusHandler.handleStatusMessage(response)
        }
      );
  }

  fetchGif(number: number) {
    return this.httpClient.get<any>(environment.REST_APP_HOST.concat(ConnectionConstants.downloadGifUrl.concat(number.toString())),
      {
        headers: Constants.headersApplicationJson,
        responseType: 'blob' as 'json'
      }
    ).pipe(catchError((resultData) => this.responseStatusHandler.handleRequestError(resultData)));
  }
}

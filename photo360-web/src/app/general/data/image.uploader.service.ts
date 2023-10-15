import {Injectable} from "@angular/core";
import {ConnectionConstants} from "../properties/properties";
import {RequestResponse, UploadImagesConfig} from "../interface/interface";
import {NetworkService} from "../network/network-server.service";

@Injectable()
export class ImageUploaderService {

  constructor(private networkService: NetworkService) {
  }

  uploadFilesWithImages(uploadImagesConfig: UploadImagesConfig) {

    const formData = new FormData();
    formData.append('isPublic', uploadImagesConfig.isPublic.toString());
    formData.append('description', uploadImagesConfig.description.toString());
    formData.append('zipFile', uploadImagesConfig.zipFile, uploadImagesConfig.zipFile.name);

    this.networkService.sendPostRequest<RequestResponse>(ConnectionConstants.uploadPhotosUrl, formData, null, null);
  }
}

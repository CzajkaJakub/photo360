import {Injectable} from "@angular/core";
import {ConnectionConstants, Constants} from "../properties/properties";
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
    if (uploadImagesConfig.photosZipFile360 != null) {
      formData.append('photosZipFile360', uploadImagesConfig.photosZipFile360, uploadImagesConfig.photosZipFile360.name);
    }
    if (uploadImagesConfig.photosZipFile != null) {
      formData.append('photosZipFile', uploadImagesConfig.photosZipFile, uploadImagesConfig.photosZipFile.name);
    }
    formData.append('title', uploadImagesConfig.title.toString());
    if (uploadImagesConfig.backgroundColor != null) {
      formData.append('backgroundColor', uploadImagesConfig.backgroundColor.toString());
    }

    this.networkService.sendPostRequest<RequestResponse>(ConnectionConstants.uploadPhotosUrl, formData, Constants.headersWithFormData, null, null).subscribe(cokolwiek => console.log(cokolwiek));
  }
}

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
    formData.append('savePhoto360', 'true');
    formData.append('savePhotos', 'true');
    formData.append('description', uploadImagesConfig.description.toString());
    formData.append('zipFile', uploadImagesConfig.zipFile, uploadImagesConfig.zipFile.name);
    formData.append('title', uploadImagesConfig.title.toString());
    // formData.append('savePhotos', uploadImagesConfig.savePhotos.toString());
    // formData.append('savePhoto360', uploadImagesConfig.savePhoto360.toString());
    // formData.append('backgroundColor', uploadImagesConfig.backgroundColor.toString());

    this.networkService.sendPostRequest<RequestResponse>(ConnectionConstants.uploadPhotosUrl, formData, Constants.headersWithFormData, null, null).subscribe(cokolwiek => console.log(cokolwiek));
  }
}

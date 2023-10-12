import {Injectable} from "@angular/core";
import {NetworkService} from "../network/network-server.service";
import {GitDataDto, RequestResponse} from "../interface/interface";
import {ConnectionConstants} from "../properties/properties";

@Injectable()
export class GifService {

  constructor(private networkService: NetworkService) {
  }

  fetchGif(number: number) {
    this.networkService.sendGetRequest<GitDataDto>(ConnectionConstants.downloadGifUrl, null, [number]);
  }

  fetchPublicGifs() {
    this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadPublicGifs, null, null);
  }

  fetchPrivateGifs() {
    this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadPrivateGifs, null, null);
  }

  fetchAllGifs() {
    this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadAllGifsUrl, null, null);
  }

  removeGif(gifData: GitDataDto) {
    this.networkService.sendGetRequest<RequestResponse>(ConnectionConstants.removeGifUrl, null, [gifData.gifId]);
  }

  removeGifFromFavourite(gifData: GitDataDto) {
    this.networkService.sendGetRequest<RequestResponse>(ConnectionConstants.removeFromFavouriteUrl, null, [gifData.gifId]);
  }

  fetchFavouriteGifs() {
    this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.getFavouriteGifsUrl, null, null);
  }

  addGifToFavourite(gifData: GitDataDto) {
    this.networkService.sendGetRequest<RequestResponse>(ConnectionConstants.addToFavouriteUrl, null, [gifData.gifId]);
  }
}

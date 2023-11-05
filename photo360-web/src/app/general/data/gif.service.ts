import {Injectable} from "@angular/core";
import {NetworkService} from "../network/network-server.service";
import {GitDataDto, RequestResponse} from "../interface/interface";
import {ConnectionConstants} from "../properties/properties";

@Injectable()
export class GifService {

    constructor(private networkService: NetworkService) {
    }

    fetchGif(number: number) {
        return this.networkService.sendGetRequest<GitDataDto>(ConnectionConstants.downloadGifUrl, null, null, [number]);
    }

    fetchPublicGifs() {
        let previewMode = new Map<string, string>();
        previewMode.set("previewMode", 'True');
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadPublicGifs, null, previewMode, null);
    }

    fetchPrivateGifs() {
        let previewMode = new Map<string, string>();
        previewMode.set("previewMode", 'True');
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadPrivateGifs, null, previewMode, null);
    }

    fetchAllGifs() {
        let previewMode = new Map<string, string>();
        previewMode.set("previewMode", 'True');
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadAllGifsUrl, null, previewMode, null);
    }

    removeGif(gifData: GitDataDto) {
        return this.networkService.sendDeleteRequest<RequestResponse>(ConnectionConstants.removeGifUrl, null, null, [gifData.gifId]);
    }

    removeGifFromFavourite(gifData: GitDataDto) {
        return this.networkService.sendDeleteRequest<RequestResponse>(ConnectionConstants.removeFromFavouriteUrl, null, null, [gifData.gifId]);
    }

    fetchFavouriteGifs() {
        let previewMode = new Map<string, string>();
        previewMode.set("previewMode", 'True');
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.getFavouriteGifsUrl, null, previewMode, null);
    }

    addGifToFavourite(gifData: GitDataDto) {
        return this.networkService.sendPutRequest<RequestResponse>(ConnectionConstants.addToFavouriteUrl, null, null, null,[gifData.gifId]);
    }
}

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
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadPublicGifs, null, null, null);
    }

    fetchPrivateGifs() {
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadPrivateGifs, null, null, null);
    }

    fetchAllGifs() {
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.downloadAllGifsUrl, null, null, null);
    }

    removeGif(gifData: GitDataDto) {
        return this.networkService.sendDeleteRequest<RequestResponse>(ConnectionConstants.removeGifUrl, null, null, [gifData.gifId]);
    }

    removeGifFromFavourite(gifData: GitDataDto) {
        return this.networkService.sendDeleteRequest<RequestResponse>(ConnectionConstants.removeFromFavouriteUrl, null, null, [gifData.gifId]);
    }

    fetchFavouriteGifs() {
        return this.networkService.sendGetRequest<Array<GitDataDto>>(ConnectionConstants.getFavouriteGifsUrl, null, null, null);
    }

    addGifToFavourite(gifData: GitDataDto) {
        return this.networkService.sendGetRequest<RequestResponse>(ConnectionConstants.addToFavouriteUrl, null, null, [gifData.gifId]);
    }
}

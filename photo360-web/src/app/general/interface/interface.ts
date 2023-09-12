export interface RequestResponse {
  responseMessage: string,
  statusType: string,
  statusCode: number
}

export interface AuthResponseData {
  email: string,
  _token: string,
  _tokenExpirationDate: string,
  _lastLoggedDatetime: Date
}

export interface UploadImagesConfig {
  zipFile: File,
  isPublic: Boolean,
  description: String
}

export interface GitDataDto {
  gif: String,
  gifId: number,
  userLogin: String,
  isPublic: Boolean,
  description: String,
  uploadDateTime: number,
}

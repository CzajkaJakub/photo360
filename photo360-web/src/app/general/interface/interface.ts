export interface RequestResponse {
  responseMessage: string,
  statusType: string,
  statusCode: number
}

export interface LoginRequestDto {
  login: string,
  password: string,
}

export interface RegisterRequestDto {
  login: string,
  email: string,
  password: string,
}

export interface ChangePasswordRequestDto {
  oldPassword: string,
  newPassword: string,
}

export interface ResetPasswordConfirmationRequestDto {
  email: string,
  newPassword: string,
  resetPasswordToken: string
}

export interface ResetPasswordRequestDto {
  email: string,
}

export interface UserEmailVerificationDto {
  emailVerificationToken: string,
}

export interface AuthResponseDataDto {
  email: string,
  _token: string,
  _tokenExpirationDate: string,
  _lastLoggedDatetime: Date,
  userRolesList: string[]
}

export interface UploadImagesConfig {
  photosZipFile360: File,
  photosZipFile: File,
  isPublic: Boolean,
  description: String,
  title: String,
  backgroundColor: String
}

export interface GitDataDto {
  gif: string,
  gifId: number,
  userLogin: string,
  isPublic: Boolean,
  description: string,
  uploadDateTime: number,
  title: string,
  listOfPhotos: string[],
  headPhoto: string
}

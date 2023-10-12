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
  roles: string[]
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

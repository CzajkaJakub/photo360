import {HttpHeaders} from "@angular/common/http";

export const ConnectionConstants = {

  //authorization controller
  authorizationUrl: "photo360/authorization/login",
  createNewUserUrl: "photo360/authorization/register",
  changePasswordUrl: "photo360/authorization/changePassword",
  resetPasswordRequestUrl: "photo360/authorization/resetPasswordRequest",
  emailConfirmationRequestUrl: "photo360/authorization/emailConfirmationRequest",
  resetPasswordConfirmationUrl: "photo360/authorization/resetPasswordConfirmation",
  confirmEmailWithCodeRequestUrl: "photo360/authorization/confirmEmailWithCodeRequest",

  //system controller
  removeGifUrl: "photo360/removeGif",
  downloadGifUrl: "photo360/downloadGif",
  uploadPhotosUrl: "photo360/uploadPhotos",
  addToFavouriteUrl: "photo360/addToFavourite",
  getFavouriteGifsUrl: "photo360/getFavourites",
  downloadAllGifsUrl: "photo360/downloadAllGifs",
  downloadPublicGifs: "photo360/downloadPublicGifs",
  downloadPrivateGifs: "photo360/downloadPrivateGifs",
  removeFromFavouriteUrl: "photo360/removeFromFavourite",
}

export const Constants = {

  /**
   *              Simple dictionary which stores all classes of specific language's flags.
   *              It is used only to create flags {@link getFlagClass} next to labels in language picker.
   */
  flagsDictionary: new Map(
    [
      ["Polski", "flag-icon flag-icon-pl"],
      ["English", "flag-icon flag-icon-gb"],
      ["Français", "flag-icon flag-icon-fr"],
    ]
  ),

  publicApiKey: "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJpt3Bs6fwMc2S7h5cpIP6nkG9DsISp0MfKTpwtt31/a1ZF2+Pv8I0f64CIcBj4GPWP4PWWe9nI4WSUKkf5CdxT6sUh4toHvBemfQiSw3sCaHfgL0WBrdqhqIxYUwsedb9ZuCXRp6acmbvqttNI2r5V8rsuT0nTDYCnVTl5OgnQIDAQAB",
  serverStatusTranslatePrefix: "status.",
  toastDisplayTime: 4000,

  /**
   *  Value of time [ms], used to skip loading panel if time of request is very short.
   */
  timeOfDataRequestWithoutShowingPanel: 2000,

  headersApplicationJson: new HttpHeaders({
    'Content-Type': 'application/json'
  }),
}

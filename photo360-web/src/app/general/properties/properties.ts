import {HttpHeaders} from "@angular/common/http";

export const ConnectionConstants = {

  //authorization
  authorizationUrl: "photo360/authorization/login",
  createNewUserUrl: "photo360/authorization/register",
  changePasswordUrl: "photo360/authorization/change-password",
  uploadPhotosUrl: "photo360/uploadPhotos",
  downloadGifUrl: "photo360/downloadGif/",
  downloadPublicGifs: "photo360/downloadPublicGifs",
  downloadPrivateGifs: "photo360/downloadPrivateGifs",
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

  headersApplicationJson: new HttpHeaders({
    'Content-Type': 'application/json'
  }),
}

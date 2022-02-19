export type AppIconsId =
  | "azur"
  | "backward-solid"
  | "ban-solid"
  | "box-open-solid"
  | "check-double-solid"
  | "exclamation-solid"
  | "forward-solid"
  | "git"
  | "github"
  | "google"
  | "itranslate-bw"
  | "itranslate"
  | "java-file"
  | "json-file"
  | "key-solid"
  | "paper-plane-solid"
  | "rest-api"
  | "select-all"
  | "unselect-all"
  | "window-close-regular";

export type AppIconsKey =
  | "Azur"
  | "BackwardSolid"
  | "BanSolid"
  | "BoxOpenSolid"
  | "CheckDoubleSolid"
  | "ExclamationSolid"
  | "ForwardSolid"
  | "Git"
  | "Github"
  | "Google"
  | "ItranslateBw"
  | "Itranslate"
  | "JavaFile"
  | "JsonFile"
  | "KeySolid"
  | "PaperPlaneSolid"
  | "RestApi"
  | "SelectAll"
  | "UnselectAll"
  | "WindowCloseRegular";

export enum AppIcons {
  Azur = "azur",
  BackwardSolid = "backward-solid",
  BanSolid = "ban-solid",
  BoxOpenSolid = "box-open-solid",
  CheckDoubleSolid = "check-double-solid",
  ExclamationSolid = "exclamation-solid",
  ForwardSolid = "forward-solid",
  Git = "git",
  Github = "github",
  Google = "google",
  ItranslateBw = "itranslate-bw",
  Itranslate = "itranslate",
  JavaFile = "java-file",
  JsonFile = "json-file",
  KeySolid = "key-solid",
  PaperPlaneSolid = "paper-plane-solid",
  RestApi = "rest-api",
  SelectAll = "select-all",
  UnselectAll = "unselect-all",
  WindowCloseRegular = "window-close-regular",
}

export const APP_ICONS_CODEPOINTS: { [key in AppIcons]: string } = {
  [AppIcons.Azur]: "61697",
  [AppIcons.BackwardSolid]: "61698",
  [AppIcons.BanSolid]: "61699",
  [AppIcons.BoxOpenSolid]: "61700",
  [AppIcons.CheckDoubleSolid]: "61701",
  [AppIcons.ExclamationSolid]: "61702",
  [AppIcons.ForwardSolid]: "61703",
  [AppIcons.Git]: "61704",
  [AppIcons.Github]: "61705",
  [AppIcons.Google]: "61706",
  [AppIcons.ItranslateBw]: "61707",
  [AppIcons.Itranslate]: "61708",
  [AppIcons.JavaFile]: "61709",
  [AppIcons.JsonFile]: "61710",
  [AppIcons.KeySolid]: "61711",
  [AppIcons.PaperPlaneSolid]: "61712",
  [AppIcons.RestApi]: "61713",
  [AppIcons.SelectAll]: "61714",
  [AppIcons.UnselectAll]: "61715",
  [AppIcons.WindowCloseRegular]: "61716",
};

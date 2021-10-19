export type AppIconsId =
  | "backward-solid"
  | "ban-solid"
  | "box-open-solid"
  | "check-double-solid"
  | "exclamation-solid"
  | "forward-solid"
  | "git"
  | "github"
  | "google"
  | "java-file"
  | "json-file"
  | "key-solid"
  | "paper-plane-solid"
  | "select-all"
  | "unselect-all"
  | "window-close-regular";

export type AppIconsKey =
  | "BackwardSolid"
  | "BanSolid"
  | "BoxOpenSolid"
  | "CheckDoubleSolid"
  | "ExclamationSolid"
  | "ForwardSolid"
  | "Git"
  | "Github"
  | "Google"
  | "JavaFile"
  | "JsonFile"
  | "KeySolid"
  | "PaperPlaneSolid"
  | "SelectAll"
  | "UnselectAll"
  | "WindowCloseRegular";

export enum AppIcons {
  BackwardSolid = "backward-solid",
  BanSolid = "ban-solid",
  BoxOpenSolid = "box-open-solid",
  CheckDoubleSolid = "check-double-solid",
  ExclamationSolid = "exclamation-solid",
  ForwardSolid = "forward-solid",
  Git = "git",
  Github = "github",
  Google = "google",
  JavaFile = "java-file",
  JsonFile = "json-file",
  KeySolid = "key-solid",
  PaperPlaneSolid = "paper-plane-solid",
  SelectAll = "select-all",
  UnselectAll = "unselect-all",
  WindowCloseRegular = "window-close-regular",
}

export const APP_ICONS_CODEPOINTS: { [key in AppIcons]: string } = {
  [AppIcons.BackwardSolid]: "61697",
  [AppIcons.BanSolid]: "61698",
  [AppIcons.BoxOpenSolid]: "61699",
  [AppIcons.CheckDoubleSolid]: "61700",
  [AppIcons.ExclamationSolid]: "61701",
  [AppIcons.ForwardSolid]: "61702",
  [AppIcons.Git]: "61703",
  [AppIcons.Github]: "61704",
  [AppIcons.Google]: "61705",
  [AppIcons.JavaFile]: "61706",
  [AppIcons.JsonFile]: "61707",
  [AppIcons.KeySolid]: "61708",
  [AppIcons.PaperPlaneSolid]: "61709",
  [AppIcons.SelectAll]: "61710",
  [AppIcons.UnselectAll]: "61711",
  [AppIcons.WindowCloseRegular]: "61712",
};

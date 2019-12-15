export class MouseEventUtils {
  public static stopPropagation(event: MouseEvent): void {
    event.stopPropagation();
    event.stopImmediatePropagation();
  }
}

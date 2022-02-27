/**
 * i18n Tool
 * Web API of the i18n tool
 *
 * OpenAPI spec version: 1.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

/**
 * Trigger definition of a task.
 */
export interface ScheduledTaskTriggerDto {
  /**
   * The type of this trigger.
   */
  type: ScheduledTaskTriggerDto.TypeDtoEnum;
}
export namespace ScheduledTaskTriggerDto {
  export type TypeDtoEnum = 'RECURRING' | 'NON_RECURRING';
  export const TypeDtoEnum = {
    RECURRING: 'RECURRING' as TypeDtoEnum,
    NONRECURRING: 'NON_RECURRING' as TypeDtoEnum,
  };
}

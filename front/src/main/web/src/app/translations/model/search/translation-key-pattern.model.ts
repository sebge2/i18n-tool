import {TranslationStringPatternStrategy} from "./translation-string-pattern-strategy.enum";
import {TranslationKeyPatternDto} from "../../../api";

const DTO_STATUS_MAPPING: { [key: string]: TranslationKeyPatternDto.StrategyDtoEnum } = {
    [TranslationStringPatternStrategy.STARTS_WITH]: TranslationKeyPatternDto.StrategyDtoEnum.STARTSWITH,
    [TranslationStringPatternStrategy.CONTAINS]: TranslationKeyPatternDto.StrategyDtoEnum.CONTAINS,
    [TranslationStringPatternStrategy.EQUALS]: TranslationKeyPatternDto.StrategyDtoEnum.EQUALS,
    [TranslationStringPatternStrategy.ENDS_WITH]: TranslationKeyPatternDto.StrategyDtoEnum.ENDSWITH,
}

export class TranslationKeyPattern {

    static toDto(pattern: TranslationKeyPattern): TranslationKeyPatternDto {
        if (!pattern) {
            return null;
        }

        return {
            pattern: pattern.pattern,
            strategy: DTO_STATUS_MAPPING[pattern.strategy],
        };
    }

    constructor(public strategy: TranslationStringPatternStrategy, public pattern: string) {
    }
}

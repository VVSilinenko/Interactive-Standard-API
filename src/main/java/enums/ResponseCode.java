package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS_200 (200),
    ERROR_400 (400),
    ERROR_500 (500);

    private final int code;
}

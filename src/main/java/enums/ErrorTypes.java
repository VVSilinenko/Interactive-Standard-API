package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorTypes {
    INTERNAL_SERVER_ERROR ("Internal Server Error"),
    BAD_REQUEST("Bad Request");

    private final String type;
}

package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    FEMALE ("female"),
    MALE ("male"),
    ANY ("any");

    private final String gender;
}

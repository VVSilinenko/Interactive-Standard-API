package requests;

import io.restassured.response.Response;

import static enums.ResponseCode.ERROR_500;
import static enums.ResponseCode.SUCCESS_200;
import static io.restassured.RestAssured.given;

public class GetRequests {

    private static final String USERS_LIST_URL = "https://hr-challenge.interactivestandard.com/api/test/users";
    private static final String USER_INFO_URL = "https://hr-challenge.interactivestandard.com/api/test/user/";

    public static Response getUsersList(String gender) {
        return getUsersListRequest(gender, SUCCESS_200.getCode());
    }

    public static Response getUsersListWithError(Object gender) {
        return getUsersListRequest(gender, ERROR_500.getCode());
    }

    public static Response getUsersListWithoutParamGender() {
        return getUsersListRequestWithoutParam();
    }

    public static Response getUser(Object userId) {
        return getUserRequest(userId, SUCCESS_200.getCode());
    }

    public static Response getUserWithError(Object userId) {
        return given()
                .when()
                .get(USER_INFO_URL + userId)
                .then()
                .extract().response();
    }

    public static Response getUserRequestWithoutId() {
        return getUserWithoutId();
    }

    private static Response getUsersListRequest(Object gender, int responseCode) {
        return given()
                .queryParam("gender", gender)
                .when()
                .get(USERS_LIST_URL)
                .then()
                .statusCode(responseCode)
                .extract().response();
    }

    private static Response getUsersListRequestWithoutParam() {
        return given()
                .when()
                .get(USERS_LIST_URL)
                .then()
                .extract().response();
    }

    private static Response getUserRequest(Object userId, int responseCode) {
        return given()
                .when()
                .get(USER_INFO_URL + userId)
                .then()
                .statusCode(responseCode)
                .extract().response();
    }

    private static Response getUserWithoutId() {
        return given()
                .when()
                .get(USER_INFO_URL)
                .then()
                .extract().response();
    }
}

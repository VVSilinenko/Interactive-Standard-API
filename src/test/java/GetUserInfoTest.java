import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.stream.Stream;

import static enums.Gender.FEMALE;
import static enums.Gender.MALE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static requests.GetRequests.*;

public class GetUserInfoTest extends TestHelper {

    @Test
    @DisplayName("Проверка getUserInfo при корректном id (gender=male)")
    public void checkGetUserMethodForMaleUser() {
        int randomMaleUserId = 15;
        JSONObject responseBody = new JSONObject(getUser(randomMaleUserId).asString());
        assertAll(
                () -> assertEquals(true, responseBody.get("isSuccess")),
                () -> assertEquals(0, responseBody.get("errorCode")),
                () -> assertEquals(randomMaleUserId, responseBody.getJSONObject("user").get("id")),
                () -> assertEquals("Gogol", responseBody.getJSONObject("user").get("name")),
                () -> assertEquals(MALE.getGender(), responseBody.getJSONObject("user").get("gender")),
                () -> assertEquals(210, responseBody.getJSONObject("user").get("age")),
                () -> assertEquals("Mirgorod", responseBody.getJSONObject("user").get("city")),
                () -> assertEquals("1809-04-01T00:00:00", responseBody.getJSONObject("user").get("registrationDate"))
        );
    }

    @Test
    @DisplayName("Проверка getUserInfo при корректном id (gender=female)")
    public void checkGetUserMethodForFemaleUser() {
        int randomFemaleUserId = 5;
        JSONObject responseBody = new JSONObject(getUser(randomFemaleUserId).asString());
        assertAll(
                () -> assertEquals(true, responseBody.get("isSuccess")),
                () -> assertEquals(0, responseBody.get("errorCode")),
                () -> assertEquals(randomFemaleUserId, responseBody.getJSONObject("user").get("id")),
                () -> assertEquals("Ann", responseBody.getJSONObject("user").get("name")),
                () -> assertEquals(FEMALE.getGender(), responseBody.getJSONObject("user").get("gender")),
                () -> assertEquals(22, responseBody.getJSONObject("user").get("age")),
                () -> assertEquals("Novosibirsk", responseBody.getJSONObject("user").get("city")),
                () -> assertEquals("2017-04-12T18:30:01.000000021", responseBody.getJSONObject("user").get("registrationDate"))

        );
    }

    @ParameterizedTest
    @MethodSource("getMaleUsersIdList")
    @DisplayName("Проверка полученных данных (gender=male) метода getUserInfo")
    public void checkMaleUsersInfo(List<Object> ids) {
        checkUserInfo(ids, MALE.getGender());
    }

    @ParameterizedTest
    @MethodSource("getFemaleUsersIdList")
    @DisplayName("Проверка полученных данных (gender=female) метода getUserInfo")
    public void checkFemaleUsersInfo(List<Object> ids) {
        checkUserInfo(ids, FEMALE.getGender());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100, -10001, -2147483648})
    @DisplayName("Проверка getUserInfo при вводе отрицательных значений id")
    public void getUserInfoWithNegativeId(int userId) {
        assertEquals(400, getUserWithError(userId).getStatusCode());
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Проверка getUserInfo при вводе id=null")
    public void getUserInfoWithNullId(Object userId) {
        Response response = getUserWithError(userId);
        assertEquals(400, response.getStatusCode());
        JSONObject responseBody = new JSONObject(response.asString());
        assertAll(
                () -> assertEquals(false, responseBody.get("isSuccess")),
                () -> assertEquals("NumberFormatException: For input string: \"null\"", responseBody.get("errorMessage"))
        );
    }

    @ParameterizedTest
    @ValueSource (strings = {"user", "male", "fffqerqww", "%^&*^", "5345,44"})
    @DisplayName("Проверка getUserInfo при вводе некорректных символов в значении id")
    public void getUserInfoWithIncorrectSymbol(Object userId) {
        Response response = getUserWithError(userId);
        assertEquals(400, response.getStatusCode());
        JSONObject responseBody = new JSONObject(response.asString());
        assertAll(
                () -> assertEquals(false, responseBody.get("isSuccess")),
                () -> assertEquals("NumberFormatException: For input string: \"" + userId + "\"", responseBody.get("errorMessage"))
        );
    }

    @Test
    @DisplayName("Проверка getUserInfo при вводе id=0")
    public void getUserInfoWithZero() {
        Response response = getUserWithError(0);
        assertEquals(500, response.getStatusCode());
    }

    @Test
    @DisplayName("Проверка getUserInfo при вводе id=' ' (пробел)")
    public void getUserInfoWithProbel() {
        Response response = getUserWithError(" ");
        assertEquals(500, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 11, 299})
    @DisplayName("Проверка getUserInfo при вводе несуществующих значений id")
    public void getUserInfoWithNonExistentValueId(int userId) {
        assertEquals(404, getUserWithError(userId).getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(doubles = {4.33, 99.321, -14.33})
    @DisplayName("Проверка getUserInfo при вводе числа с плавающей точкой")
    public void getUserInfoWithDoubleValueId(double userId) {
        Response response = getUserWithError(userId);
        assertEquals(400, response.getStatusCode());
        JSONObject responseBody = new JSONObject(response.asString());
        assertAll(
                () -> assertEquals(false, responseBody.get("isSuccess")),
                () -> assertEquals("NumberFormatException: For input string: \"" + userId + "\"", responseBody.get("errorMessage"))
        );
    }

    @Test
    @DisplayName("Выполнение запроса без передачи id")
    public void getUserInfoWithoutId() {
        Response response = getUserRequestWithoutId();
        assertEquals(404, response.getStatusCode());
        JSONObject responseBody = new JSONObject(response.asString());
        assertAll(
                () -> assertEquals("Not Found", responseBody.get("error")),
                () -> assertEquals("No message available", responseBody.get("message")),
                () -> assertEquals("/api/test/user/", responseBody.get("path"))
        );
    }

    private void checkUserInfo(List<Object> ids, String gender) {
        int i = 0;
        for(Object id: ids) {
            JSONObject userInfo = new JSONObject(getUser((int)id).asString());
            try {
                assertAll(
                        () -> assertEquals(true, userInfo.get("isSuccess")),
                        () -> assertEquals(0, userInfo.get("errorCode")),
                        () -> assertEquals(id, userInfo.getJSONObject("user").get("id")),
                        () -> assertEquals(gender, userInfo.getJSONObject("user").get("gender"))
                );
                System.out.println(id + " - OK");
            } catch (AssertionError ex) {
                System.out.println(id + " - FAIL");
                System.out.println(ex.getMessage());
                i++;
            }
        }
        if (i > 0) {
            throw new AssertionFailedError();
        }
    }

    static Stream<List<Object>> getMaleUsersIdList() {
        return Stream.of(getUsersIdList(MALE.getGender()));
    }

    static Stream<List<Object>> getFemaleUsersIdList() {
        return Stream.of(getUsersIdList(FEMALE.getGender()));
    }
}

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.AssertionFailedError;

import java.util.List;

import static enums.ErrorTypes.BAD_REQUEST;
import static enums.ErrorTypes.INTERNAL_SERVER_ERROR;
import static enums.Gender.*;
import static enums.ResponseCode.SUCCESS_200;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static requests.GetRequests.*;

public class GetUsersListTest extends TestHelper {

    @Test
    @DisplayName("Проверка getUsersList при корректном gender=male")
    public void getMaleUsersListTest() {
        JSONObject responseBody = new JSONObject(getUsersList(MALE.getGender()).asString());
        assertAll(
                () -> assertEquals(6, responseBody.getJSONArray("idList").length()),
                () -> assertEquals(true, responseBody.get("isSuccess")),
                () -> assertEquals(0, responseBody.get("errorCode"))
        );
    }

    @Test
    @DisplayName("Проверка getUsersList при корректном gender=female")
    public void getFemaleUsersListTest() {
        JSONObject responseBody = new JSONObject(getUsersList(FEMALE.getGender()).asString());
        assertAll(
                ()-> assertEquals(6, responseBody.getJSONArray("idList").length()),
                ()-> assertEquals(true, responseBody.get("isSuccess")),
                ()-> assertEquals(0, responseBody.get("errorCode"))
        );
    }

    @Test
    @DisplayName("Проверка getUsersList при корректном gender=any")
    public void getAnyUsersListTest() {
        JSONObject responseBody = new JSONObject(getUsersList(ANY.getGender()).asString());
        assertAll(
                ()-> assertEquals(13, responseBody.getJSONArray("idList").length()),
                ()-> assertEquals(true, responseBody.get("isSuccess")),
                ()-> assertEquals(0, responseBody.get("errorCode"))
        );
    }

    @Test
    @DisplayName("Проверка полученных данных (gender=male) метода getUsersList")
    public void validationDataMaleTest() {
        checkUserGender(getUsersIdList(MALE.getGender()), MALE.getGender());
    }

    @Test
    @DisplayName("Проверка полученных данных (gender=female) метода getUsersList")
    public void validationDataFemaleTest() {
        checkUserGender(getUsersIdList(FEMALE.getGender()), FEMALE.getGender());
    }

    @Test
    @DisplayName("Проверка полученных данных (gender=any) метода getUsersList")
    public void validationDataAnyTest() {
        checkResponseCode(getUsersIdList(ANY.getGender()), SUCCESS_200.getCode());
    }

    @ParameterizedTest
    @ValueSource (strings = {"1123", "females", "$%^&", " ", "McCloud", "magic"})
    @DisplayName("Проверка getUsersList при вводе некорректных значений параметра gender (string)")
    public void getUsersListWithIncorrectGender(String gender) {
        checkGetUsersListWithIncorrectData(gender);
    }

    @ParameterizedTest
    @ValueSource (ints = {-1, 0, 1, 100, 2432522})
    @DisplayName("Проверка getUsersList при вводе некорректных значений параметра gender (int)")
    public void getUsersListWithIncorrectGender(int gender) {
        checkGetUsersListWithIncorrectData(gender);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Проверка getUsersList при вводе gender=null")
    public void getUsersListWithGenderIsNull(String gender) {
        JSONObject responseBody = new JSONObject(getUsersListWithError(gender).asString());
        assertAll(
                ()-> assertEquals("/api/test/users", responseBody.get("path")),
                ()-> assertEquals(INTERNAL_SERVER_ERROR.getType(), responseBody.get("error")),
                ()-> assertEquals("No enum constant com.coolrocket.app.api.test.qa.Gender." , responseBody.get("message")),
                ()-> assertEquals(500, responseBody.get("status"))
        );
    }

    @Test
    @DisplayName("Выполнение запроса без параметра gender")
    public void getUsersListWithoutParam() {
        JSONObject responseBody = new JSONObject(getUsersListWithoutParamGender().asString());
        assertAll(
                ()-> assertEquals("/api/test/users", responseBody.get("path")),
                ()-> assertEquals(BAD_REQUEST.getType(), responseBody.get("error")),
                ()-> assertEquals("Required String parameter 'gender' is not present" , responseBody.get("message")),
                ()-> assertEquals(400, responseBody.get("status"))
        );
    }

    private void checkGetUsersListWithIncorrectData(Object gender) {
        JSONObject responseBody = new JSONObject(getUsersListWithError(gender).asString());
        assertAll(
                ()-> assertEquals("/api/test/users", responseBody.get("path")),
                ()-> assertEquals(INTERNAL_SERVER_ERROR.getType(), responseBody.get("error")),
                ()-> assertEquals("No enum constant com.coolrocket.app.api.test.qa.Gender." + gender , responseBody.get("message")),
                ()-> assertEquals(500, responseBody.get("status"))
        );
    }

    private void checkUserGender(List<Object> ids, String gender) {
        int i = 0;
        for (Object id: ids) {
            JSONObject userInfo = new JSONObject(getUser((int)id).asString());
            try {
                assertEquals(gender, userInfo.getJSONObject("user").get("gender"));
                System.out.println(id + " - OK");
            } catch (AssertionFailedError ex) {
                System.out.println(id + " - FAIL");
                System.out.println("UserId: " + id + " Expected: " + ex.getExpected() + ", " + "Actual: " + ex.getActual());
                i++;
            }
        }
        if (i > 0) {
            throw new AssertionFailedError();
        }
    }

    private void checkResponseCode(List<Object> ids, int responseCode) {
        int i = 0;
        for (Object id: ids) {
            try {
                assertEquals(responseCode, getUser((int)id).getStatusCode());
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
}

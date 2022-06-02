import org.json.JSONObject;

import java.util.List;

import static requests.GetRequests.getUsersList;

public class TestHelper {

    protected static List<Object> getUsersIdList(String gender) {
        JSONObject responseBody = new JSONObject(getUsersList(gender).asString());
        return responseBody.getJSONArray("idList").toList();
    }
}

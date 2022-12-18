import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.EmailAndName;
import models.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.UserClient;
import utils.UserGenerator;

public class ChangeUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;
    private EmailAndName emailAndName;

    @Before
    public void setup(){
        accessToken = "";
        user = UserGenerator.getUnique();
        userClient = new UserClient();
        ValidatableResponse response = userClient.create(user);
        int code = response.extract().statusCode();
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.replaceFirst("Bearer ","");
        emailAndName = EmailAndName.from(user);
        user = UserGenerator.getUnique();
        emailAndName = EmailAndName.from(user);
    }
    @After
    public void cleanUp(){
        userClient.delete(accessToken);
    }
    @Test
    @DisplayName("Successful user change test")
    @Description("Check if user's email and name can be changed")
    public void successfulUserChangeTest(){
        ValidatableResponse response = userClient.patch(emailAndName, accessToken);
        Assert.assertEquals("User information was not changed",200,response.extract().statusCode());
        Assert.assertEquals("Email was not changed",emailAndName.getEmail(),response.extract().path("user.email"));
        Assert.assertEquals("Name was not changed",emailAndName.getName(),response.extract().path("user.name"));
    }
    @Test
    @DisplayName("Unsuccessful user change without authorisation test")
    @Description("Check if user can't be changed without authorisation")
    public void unsuccessfulUserChangeWithoutAuthorization(){
        ValidatableResponse response = userClient.patchWithoutAuth(emailAndName);
        Assert.assertEquals("Problems with authorization on user change endpoint",401,response.extract().statusCode());
        Assert.assertEquals("Message was incorrect","You should be authorised",response.extract().path("message"));
    }

}

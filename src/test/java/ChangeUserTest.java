import io.restassured.response.ValidatableResponse;
import models.EmailAndName;
import models.User;
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
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.replaceFirst("Bearer","");
        emailAndName = EmailAndName.from(user);
    }
    @Test
    public void successfulUserChangeTest(){
        user = UserGenerator.getUnique();
        emailAndName = EmailAndName.from(user);
        ValidatableResponse response = userClient.patch(emailAndName, accessToken);
        Assert.assertEquals("User information was not changed",200,response.extract().statusCode());
        Assert.assertEquals("Email was not changed",emailAndName.getName(),response.extract().path("user.email"));
        Assert.assertEquals("Name was not changed",emailAndName.getName(),response.extract().path("user.name"));
    }
}

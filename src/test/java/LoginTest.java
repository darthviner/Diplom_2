import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.Credentials;
import models.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.UserClient;
import utils.UserGenerator;

public class LoginTest {
    private User user;
    private String accessToken;
    private UserClient userClient;
    private Credentials credentials;

    @Before
    public void setup(){
        accessToken = "";
        userClient = new UserClient();
        user = UserGenerator.getUnique();
        credentials = Credentials.from(user);
    }
    @After
    public void cleanUp(){
        if (!accessToken.equals(""))
            userClient.delete(accessToken);
    }
    @Test
    @DisplayName("Successful login test")
    @Description("Check if user can login")
    public void successfulLoginTest(){
        userClient.create(user);
        ValidatableResponse response = userClient.login(credentials);
        Assert.assertEquals("Unsuccessful login",200,response.extract().statusCode());
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.replaceFirst("Bearer","");
    }

    @Test
    @DisplayName("Unsuccessful login with invalid credentials test")
    @Description("Check if user can't login with invalid credentials of unexisting user")
    public void unsuccessfulLoginWithInvalidCredentialsTest(){
        ValidatableResponse response = userClient.login(credentials);
        Assert.assertEquals("Successful login with invalid credentials",401,response.extract().statusCode());
        Assert.assertEquals("Incorrect message","email or password are incorrect",response.extract().path("message"));
    }
    @Test
    @DisplayName("Unsuccessful login without email test")
    @Description("Check if user can't login without email")
    public void unsuccessfulLoginWithoutEmailTest(){
        user = UserGenerator.getUserWithEmptyEmail();
        credentials = Credentials.from(user);
        ValidatableResponse response = userClient.login(credentials);
        Assert.assertEquals("Successful login without email",401,response.extract().statusCode());
        Assert.assertEquals("Incorrect message","email or password are incorrect",response.extract().path("message"));
    }
    @Test
    @DisplayName("Unsuccessful login without password test")
    @Description("Check if user can't login without password")
    public void unsuccessfulLoginWithoutPasswordTest(){
        user = UserGenerator.getUserWithEmptyPassword();
        credentials = Credentials.from(user);
        ValidatableResponse response = userClient.login(credentials);
        Assert.assertEquals("Successful login without email",401,response.extract().statusCode());
        Assert.assertEquals("Incorrect message","email or password are incorrect",response.extract().path("message"));
    }
}

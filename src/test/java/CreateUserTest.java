import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.UserClient;
import utils.UserGenerator;

public class CreateUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setup(){
        userClient = new UserClient();
        accessToken = "";
    }
    @After
    public void cleanUp(){
        if (!accessToken.equals("")){
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Successful user registration test")
    @Description("Check if unique user can be created")
    public void successfulUserCreationTest(){
        user = UserGenerator.getUnique();
        ValidatableResponse response = userClient.create(user);
        Assert.assertEquals("User was not created",200,response.extract().statusCode());
        Assert.assertEquals("Wrong name", response.extract().path("user.name"),user.getName());
        Assert.assertEquals("Wrong email",response.extract().path("user.email"),user.getEmail());
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.replaceFirst("Bearer","");
    }
    @Test
    @DisplayName("Existing user can't be created test")
    @Description("Check if existing user can't be created test")
    public void existingUserCanNotBeCreatedTest(){
        user = UserGenerator.getUnique();
        user = UserGenerator.getUnique();
        ValidatableResponse response = userClient.create(user);
        Assert.assertEquals("User was not created",200,response.extract().statusCode());
        Assert.assertEquals("Wrong name",user.getName(),response.extract().path("user.name"));
        Assert.assertEquals("Wrong email",user.getEmail(),response.extract().path("user.email"));
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.replaceFirst("Bearer","");

        user.setName("123");
        user.setPassword("123");
        response = userClient.create(user);
        Assert.assertEquals("Wrong status code",403,response.extract().statusCode());
        Assert.assertEquals("Wrong message","User already exists",response.extract().path("message"));
    }
    @Test
    @DisplayName("User can't ve created without email field")
    @Description("Check if user can't be created without email field")
    public void userWithoutEmailCanNotBeCreatedTest(){
        user = UserGenerator.getUserWithEmptyEmail();
        ValidatableResponse response = userClient.create(user);
        Assert.assertEquals("Wrong status code",response.extract().statusCode(),403);
        Assert.assertEquals("Wrong message",response.extract().path("message"),"Email, password and name are required fields");
    }

    @Test
    @DisplayName("User can't ve created without name field")
    @Description("Check if user can't be created without name field")
    public void userWithoutNameCanNotBeCreatedTest(){
        user = UserGenerator.getUserWithEmptyName();
        ValidatableResponse response = userClient.create(user);
        Assert.assertEquals("Wrong status code",response.extract().statusCode(),403);
        Assert.assertEquals("Wrong message",response.extract().path("message"),"Email, password and name are required fields");
    }
    @Test
    @DisplayName("User can't ve created without password field")
    @Description("Check if user can't be created without password field")
    public void userWithoutPasswordCanNotBeCreatedTest(){
        user = UserGenerator.getUserWithEmptyPassword();
        ValidatableResponse response = userClient.create(user);
        Assert.assertEquals("Wrong status code",response.extract().statusCode(),403);
        Assert.assertEquals("Wrong message",response.extract().path("message"),"Email, password and name are required fields");
    }


}

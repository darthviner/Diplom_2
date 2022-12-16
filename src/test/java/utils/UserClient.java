package utils;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.Credentials;
import models.EmailAndName;
import models.User;

import static io.restassured.RestAssured.given;

@SuppressWarnings("ALL")
public class UserClient extends Client {
    private final String USER_CREATE_PATH = "/api/auth/register";
    private final String USER_LOGIN_PATH = "/api/auth/login";
    private final String USER_PATH = "/api/auth/user";

    @Step("Create user")
    public ValidatableResponse create(User user){
        ValidatableResponse responce = given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(USER_CREATE_PATH)
                .then();
        return responce;
    }
    @Step("Delete user")
    public ValidatableResponse delete(String token) {

        return given()
                .spec(getSpec())
                .auth().oauth2(token)
                .when()
                .delete(USER_PATH)
                .then();
    }
    @Step("Login")
    public ValidatableResponse login(Credentials credentials){
        return given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(USER_LOGIN_PATH)
                .then();
    }
    @Step("Change user information")
    public ValidatableResponse patch(EmailAndName emailAndName, String token) {
        return given()
                .spec(getSpec())
                .auth().oauth2(token)
                .when()
                .body(emailAndName)
                .patch(USER_PATH)
                .then();
    }

    @Step("Attempt to change user information wothout authorization")
    public ValidatableResponse patchWithoutAuth(Credentials credentials){
        return  given()
                .spec(getSpec())
                .when()
                .body(credentials)
                .patch(USER_PATH)
                .then();
    }

}

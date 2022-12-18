import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.IngredientsClient;
import utils.OrderClient;
import utils.UserClient;
import utils.UserGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderTest {
    private User user;
    private UserClient userClient;
    private IngredientsClient ingredientsClient;
    private OrderClient orderClient;
    private String token;

    @Before
    public void setUp() {
        token = "";
        ingredientsClient = new IngredientsClient();
        user = UserGenerator.getUnique();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (!token.isEmpty()) {
            userClient.delete(token);
        }
    }

    @Test
    @DisplayName("Successful creation of an order without authorization")
    @Description("Check if order can be created without authorization")
    public void createOrderWithoutAuthorization() {
        List<String> ingredientsIDs = ingredientsClient.getIngredientsIDs();

        List<String> orderIngredients = new ArrayList<>();

        orderIngredients.add(ingredientsIDs.get(0));
        orderIngredients.add(ingredientsIDs.get(6));

        Order order = new Order(orderIngredients);

        ValidatableResponse response = orderClient.createWithoutAuthorization(order);

        assertEquals("Order was not created", 200, response.extract().statusCode());
    }
    @Test
    @DisplayName("Successful creation of order with authorization test")
    @Description("Check if order can be created with authorization")
    public void createOrderWithAuthorizationAndIngredients() {
        ValidatableResponse response = userClient.create(user);

        String rawToken = response.extract().path("accessToken");

        token = rawToken.replaceFirst("Bearer ", "");

        List<String> ingredientsIDs = ingredientsClient.getIngredientsIDs();

        List<String> orderIngredients = new ArrayList<>();

        orderIngredients.add(ingredientsIDs.get(0));
        orderIngredients.add(ingredientsIDs.get(6));

        Order order = new Order(orderIngredients);

        ValidatableResponse createOrderResponse = orderClient.createWithAuthorization(order, token);

        int statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Status code incorrect", 200, statusCode);

        boolean orderCreated = createOrderResponse.extract().path("success");
        assertTrue("Order is not created", orderCreated);

    }

    @Test
    @DisplayName("Unsuccessful creation of order without ingredients ")
    @Description("Check if order cannot be created without ingredients")
    public void createOrderWithoutIngredients() {
        List<String> orderIngredients = new ArrayList<>();

        Order order = new Order(orderIngredients);

        ValidatableResponse createOrderResponse = orderClient.createWithoutAuthorization(order);

        int statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Status code incorrect", 400, statusCode);

        boolean orderCreated = createOrderResponse.extract().path("success");
        assertTrue("Wrong response", !orderCreated);

        String responseMessage = createOrderResponse.extract().path("message");
        assertEquals("Status code incorrect", "Ingredient ids must be provided", responseMessage);
    }

    @Test
    @DisplayName("Unsuccessful creation of order with wrong hash of ingredients")
    @Description("Check if order cannot be created with wrong hash of ingredients")
    public void createOrderWithWrongHashOfIngredients() {
        List<String> orderIngredients = new ArrayList<>();

        Order order = new Order(orderIngredients);

        orderIngredients.add("123");
        orderIngredients.add("456");

        ValidatableResponse createOrderResponse = orderClient.createWithoutAuthorization(order);

        int statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Status code incorrect", 500, statusCode);

        String orderResponseBody = createOrderResponse.toString();
        assertNotNull("Body is null", orderResponseBody);
    }

    @Test
    @DisplayName("Get orders from authorized user test")
    @Description("Get orders from authorized user")
    public void getOrdersFromAuthorizedUser() {
        ValidatableResponse response = userClient.create(user);

        String rawToken = response.extract().path("accessToken");

        token = rawToken.replaceFirst("Bearer ", "");

        List<String> ingredientsIDs = ingredientsClient.getIngredientsIDs();

        List<String> orderIngredients = new ArrayList<>();

        orderIngredients.add(ingredientsIDs.get(2));
        orderIngredients.add(ingredientsIDs.get(3));

        Order order = new Order(orderIngredients);

        orderClient.createWithAuthorization(order, token);

        ValidatableResponse userOrdersResponse = orderClient.getWithAuthorization(token);

        int statusCode = userOrdersResponse.extract().statusCode();
        assertEquals("Status code incorrect", 200, statusCode);

        List<String> ordersCount = userOrdersResponse.extract().path("orders");
        assertNotNull("Orders is empty", ordersCount);

    }

    @Test
    @DisplayName("Get orders without authorization test")
    @Description("Attempt to orders without authorization")
    public void getOrdersWithoutAuthorization() {
        ValidatableResponse userOrdersResponse = orderClient.getWithoutAuthorization();

        int statusCode = userOrdersResponse.extract().statusCode();
        assertEquals("Status code incorrect", 401, statusCode);

        String userOrdersResponseMessage = userOrdersResponse.extract().path("message");
        assertEquals("Wrong message", "You should be authorised", userOrdersResponseMessage);
    }
}
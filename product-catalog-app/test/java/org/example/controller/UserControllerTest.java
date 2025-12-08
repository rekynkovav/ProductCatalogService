import org.example.dto.BasketDTO;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;
    private UserAdminController userAdminController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        userAdminController = new UserAdminController(userService);
    }

    @Test
    void testGetCurrentUserProfile() {
        String token = "Bearer validtoken";
        UserDTO.UserInfo userInfo = new UserDTO.UserInfo(1L, "testuser", org.example.model.entity.Role.USER);

        when(userService.getCurrentUserProfile(token)).thenReturn(userInfo);

        ResponseEntity<UserDTO.UserInfo> response = userController.getCurrentUserProfile(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(org.example.model.entity.Role.USER, response.getBody().getRole());
    }

    @Test
    void testGetUserBasket() {
        // 1. Подготовка тестовых данных
        String token = "Bearer validtoken";

        // 2. Создание тестового объекта BasketDTO
        BasketDTO basketDTO = new BasketDTO();

        // 3. Создаем товары для корзины
        Map<Long, BasketDTO.ProductItem> items = new HashMap<>();

        BasketDTO.ProductItem product1 = new BasketDTO.ProductItem();
        product1.setId(1L);
        product1.setName("Ноутбук");
        product1.setQuantity(1);
        product1.setPrice(50000);
        product1.setCategoryId(1L);

        BasketDTO.ProductItem product2 = new BasketDTO.ProductItem();
        product2.setId(2L);
        product2.setName("Мышь");
        product2.setQuantity(2);
        product2.setPrice(1500);
        product2.setCategoryId(1L);

        // Добавляем товары в карту
        items.put(1L, product1);
        items.put(2L, product2);

        // Устанавливаем товары в корзину
        basketDTO.setItems(items);

        // 4. Настройка поведения мока
        when(userService.getUserBasketDto(token)).thenReturn(basketDTO);

        // 5. Вызов тестируемого метода
        ResponseEntity<BasketDTO> response = userController.getUserBasket(token);

        // 6. Проверки результатов
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверяем, что ответ не пустой
        BasketDTO responseBody = response.getBody();
        assertNotNull(responseBody);

        // Проверяем товары в корзине
        Map<Long, BasketDTO.ProductItem> responseItems = responseBody.getItems();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());

        // Проверяем первый товар
        BasketDTO.ProductItem returnedProduct1 = responseItems.get(1L);
        assertNotNull(returnedProduct1);
        assertEquals("Ноутбук", returnedProduct1.getName());
        assertEquals(1, returnedProduct1.getQuantity());
        assertEquals(50000, returnedProduct1.getPrice());

        // Проверяем второй товар
        BasketDTO.ProductItem returnedProduct2 = responseItems.get(2L);
        assertNotNull(returnedProduct2);
        assertEquals("Мышь", returnedProduct2.getName());
        assertEquals(2, returnedProduct2.getQuantity());
        assertEquals(1500, returnedProduct2.getPrice());
    }

    @Test
    void testAddToBasket() {
        String token = "Bearer validtoken";
        Long productId = 1L;
        BasketDTO.AddToBasketRequest request = new BasketDTO.AddToBasketRequest();
        request.setQuantity(2);

        Map<String, String> responseMap = Map.of("message", "Товар добавлен в корзину");

        when(userService.addToBasketDto(token, productId, request)).thenReturn(responseMap);

        ResponseEntity<Map<String, String>> response = userController.addToBasket(token, productId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Товар добавлен в корзину", response.getBody().get("message"));
    }

    @Test
    void testRemoveFromBasket() {
        String token = "Bearer validtoken";
        Long productId = 1L;

        Map<String, Object> responseMap = Map.of(
                "message", "Товар удален из корзины",
                "quantity_returned", 2
        );

        when(userService.removeFromBasketDto(token, productId)).thenReturn(responseMap);

        ResponseEntity<Map<String, Object>> response = userController.removeFromBasket(token, productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Товар удален из корзины", response.getBody().get("message"));
        assertEquals(2, response.getBody().get("quantity_returned"));
    }

    @Test
    void testClearUserBasket() {
        String token = "Bearer validtoken";
        Map<String, String> responseMap = Map.of("message", "Корзина успешно очищена");

        when(userService.clearUserBasketDto(token)).thenReturn(responseMap);

        ResponseEntity<Map<String, String>> response = userController.clearUserBasket(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Корзина успешно очищена", response.getBody().get("message"));
    }

    @Test
    void testGetAllUsers_AdminAccess() {
        String token = "Bearer admintoken";

        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setUserName("admin");

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setUserName("user");

        List<UserDTO> users = Arrays.asList(userDTO1, userDTO2);

        when(userService.getAllUsersForAdmin(token)).thenReturn(users);

        ResponseEntity<?> response = userAdminController.getAllUsers(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserDTO> responseBody = (List<UserDTO>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals("admin", responseBody.get(0).getUserName());
    }

    @Test
    void testGetAllUsers_AccessDenied() {
        String token = "Bearer usertoken";

        when(userService.getAllUsersForAdmin(token))
                .thenThrow(new SecurityException("Доступ запрещен"));

        ResponseEntity<?> response = userAdminController.getAllUsers(token);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Доступ запрещен", ((Map<?, ?>) response.getBody()).get("error"));
    }
}
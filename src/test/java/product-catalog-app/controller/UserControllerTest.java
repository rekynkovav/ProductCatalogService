package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productCatalogService.controller.UserController;
import com.productCatalogService.dto.BasketDTO;
import com.productCatalogService.dto.UserDTO;
import com.productCatalogService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCurrentUserProfile_WithValidToken_ShouldReturnUserInfo() throws Exception {
        // Arrange
        UserDTO.UserInfo userInfo = UserDTO.UserInfo.builder()
                .id(1L)
                .userName("testuser")
                .build();

        when(userService.getCurrentUserProfile("Bearer valid-token")).thenReturn(userInfo);

        // Act & Assert
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getCurrentUserProfile("Bearer valid-token");
    }



    @Test
    void removeFromBasket_WithValidProductId_ShouldReturnSuccess() throws Exception {
        // Arrange
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product removed from basket");

        when(userService.removeFromBasketDto("Bearer valid-token", 1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/user/basket/remove/1")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product removed from basket"));

        verify(userService, times(1)).removeFromBasketDto("Bearer valid-token", 1L);
    }

    @Test
    void clearUserBasket_WithValidToken_ShouldReturnSuccess() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("message", "Basket cleared successfully");

        when(userService.clearUserBasketDto("Bearer valid-token")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/user/basket/clear")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Basket cleared successfully"));

        verify(userService, times(1)).clearUserBasketDto("Bearer valid-token");
    }

    @Test
    void getUserBasket_WithValidToken_ShouldReturnBasket() throws Exception {
        // Arrange
        Map<Long, BasketDTO.BasketItemDTO> items = new HashMap<>();

        // Создаем BasketItemDTO без Builder
        BasketDTO.BasketItemDTO basketItem = new BasketDTO.BasketItemDTO();
        basketItem.setId(1L);
        basketItem.setName("Product 1");
        basketItem.setPrice(1000);
        basketItem.setQuantity(1);
        basketItem.setItemTotal(1000);
        basketItem.setAvailable(true);

        items.put(1L, basketItem);

        BasketDTO basketDTO = new BasketDTO();
        basketDTO.setItems(items);
        basketDTO.setTotalItems(1);
        basketDTO.setTotalPrice(1000);

        when(userService.getUserBasketDto("Bearer valid-token")).thenReturn(basketDTO);

        // Act & Assert
        mockMvc.perform(get("/api/user/basket")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPrice").value(1000))
                .andExpect(jsonPath("$.items['1'].name").value("Product 1"))
                .andExpect(jsonPath("$.items['1'].price").value(1000));

        verify(userService, times(1)).getUserBasketDto("Bearer valid-token");
    }

    @Test
    void addToBasket_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Arrange
        // Создаем AddToBasketRequest без Builder
        BasketDTO.AddToBasketRequest request = new BasketDTO.AddToBasketRequest();
        request.setQuantity(2);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Product added to basket");

        when(userService.addToBasketDto(eq("Bearer valid-token"), eq(1L), any()))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/user/basket/add/1")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product added to basket"));

        verify(userService, times(1)).addToBasketDto(eq("Bearer valid-token"), eq(1L), any());
    }

    @Test
    void getBasketSummary_WithValidToken_ShouldReturnSummary() throws Exception {
        // Arrange
        // Создаем BasketSummary без Builder
        BasketDTO.BasketSummary summary = new BasketDTO.BasketSummary();
        summary.setItemCount(2);
        summary.setTotalQuantity(3);
        summary.setTotalPrice(1550);

        when(userService.getBasketSummary("Bearer valid-token")).thenReturn(summary);

        // Act & Assert
        mockMvc.perform(get("/api/user/basket/summary")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(2))
                .andExpect(jsonPath("$.totalQuantity").value(3))
                .andExpect(jsonPath("$.totalPrice").value(1550));

        verify(userService, times(1)).getBasketSummary("Bearer valid-token");
    }
}
package com.productCatalogService.mapper;

import com.productCatalogService.dto.BasketDTO;
import com.productCatalogService.entity.Product;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BasketMapper {

    /**
     * Преобразует корзину (мапу товаров с количеством в корзине) в BasketDTO.
     */
    public BasketDTO toBasketDTO(Map<Long, Product> basketProducts) {
        if (basketProducts == null || basketProducts.isEmpty()) {
            return createEmptyBasketDTO();
        }

        Map<Long, BasketDTO.BasketItemDTO> items = new HashMap<>();
        int totalItems = 0;
        int totalPrice = 0;

        for (Map.Entry<Long, Product> entry : basketProducts.entrySet()) {
            Product product = entry.getValue();
            int quantityInBasket = product.getQuantity();

            if (quantityInBasket > 0) {
                BasketDTO.BasketItemDTO itemDTO = createBasketItemDTO(product, quantityInBasket);
                items.put(product.getId(), itemDTO);

                totalItems++;
                totalPrice += itemDTO.getItemTotal();
            }
        }

        BasketDTO basketDTO = new BasketDTO();
        basketDTO.setItems(items);
        basketDTO.setTotalItems(totalItems);
        basketDTO.setTotalPrice(totalPrice);

        return basketDTO;
    }

    /**
     * Создает DTO элемента корзины.
     */
    private BasketDTO.BasketItemDTO createBasketItemDTO(Product product, int quantityInBasket) {
        BasketDTO.BasketItemDTO itemDTO = new BasketDTO.BasketItemDTO();
        itemDTO.setId(product.getId());
        itemDTO.setName(product.getName());
        itemDTO.setQuantity(quantityInBasket);
        itemDTO.setPrice(product.getPrice());
        itemDTO.setCategoryId(product.getCategoryId());
        itemDTO.setStockQuantity(product.getQuantity());
        itemDTO.setItemTotal(product.getPrice() * quantityInBasket);
        itemDTO.setAvailable(product.getQuantity() >= quantityInBasket);
        return itemDTO;
    }

    /**
     * Создает пустой DTO корзины.
     */
    private BasketDTO createEmptyBasketDTO() {
        BasketDTO basketDTO = new BasketDTO();
        basketDTO.setItems(new HashMap<>());
        basketDTO.setTotalItems(0);
        basketDTO.setTotalPrice(0);
        return basketDTO;
    }

    /**
     * Преобразует корзину (мапу количеств) + информацию о товарах в BasketDTO.
     */
    public BasketDTO toDTO(Map<Long, Integer> basketQuantities, Map<Long, Product> productsInfo) {
        if (basketQuantities == null || basketQuantities.isEmpty()) {
            return createEmptyBasketDTO();
        }

        Map<Long, BasketDTO.BasketItemDTO> items = new HashMap<>();
        int totalItems = 0;
        int totalPrice = 0;

        for (Map.Entry<Long, Integer> entry : basketQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantityInBasket = entry.getValue();

            Product product = productsInfo.get(productId);
            if (product != null && quantityInBasket > 0) {
                BasketDTO.BasketItemDTO itemDTO = createBasketItemDTO(product, quantityInBasket);
                items.put(productId, itemDTO);

                totalItems++;
                totalPrice += itemDTO.getItemTotal();
            }
        }

        BasketDTO basketDTO = new BasketDTO();
        basketDTO.setItems(items);
        basketDTO.setTotalItems(totalItems);
        basketDTO.setTotalPrice(totalPrice);

        return basketDTO;
    }

    /**
     * Извлекает мапу количеств из DTO корзины.
     */
    public Map<Long, Integer> toQuantitiesMap(BasketDTO basketDTO) {
        if (basketDTO == null || basketDTO.getItems() == null) {
            return new HashMap<>();
        }

        Map<Long, Integer> quantities = new HashMap<>();
        for (Map.Entry<Long, BasketDTO.BasketItemDTO> entry : basketDTO.getItems().entrySet()) {
            if (entry.getValue().getQuantity() > 0) {
                quantities.put(entry.getKey(), entry.getValue().getQuantity());
            }
        }
        return quantities;
    }

    /**
     * Создает сводную информацию о корзине.
     */
    public BasketDTO.BasketSummary toSummary(Map<Long, Integer> basketQuantities, Map<Long, Product> productsInfo) {
        BasketDTO.BasketSummary summary = new BasketDTO.BasketSummary();

        if (basketQuantities == null || basketQuantities.isEmpty()) {
            return summary;
        }

        int itemCount = 0;
        int totalQuantity = 0;
        int totalPrice = 0;

        for (Map.Entry<Long, Integer> entry : basketQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantityInBasket = entry.getValue();

            Product product = productsInfo.get(productId);
            if (product != null && quantityInBasket > 0) {
                itemCount++;
                totalQuantity += quantityInBasket;
                totalPrice += product.getPrice() * quantityInBasket;
            }
        }

        summary.setItemCount(itemCount);
        summary.setTotalQuantity(totalQuantity);
        summary.setTotalPrice(totalPrice);

        return summary;
    }

    /**
     * Создает сводную информацию о корзине из Map<Product>.
     */
    public BasketDTO.BasketSummary toSummaryFromProducts(Map<Long, Product> basketProducts) {
        BasketDTO.BasketSummary summary = new BasketDTO.BasketSummary();

        if (basketProducts == null || basketProducts.isEmpty()) {
            return summary;
        }

        int itemCount = 0;
        int totalQuantity = 0;
        int totalPrice = 0;

        for (Product product : basketProducts.values()) {
            int quantityInBasket = product.getQuantity();
            if (quantityInBasket > 0) {
                itemCount++;
                totalQuantity += quantityInBasket;
                totalPrice += product.getPrice() * quantityInBasket;
            }
        }

        summary.setItemCount(itemCount);
        summary.setTotalQuantity(totalQuantity);
        summary.setTotalPrice(totalPrice);

        return summary;
    }
}
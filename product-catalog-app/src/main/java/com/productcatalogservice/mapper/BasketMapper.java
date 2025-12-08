package com.productcatalogservice.mapper;

import com.productcatalogservice.dto.BasketDTO;
import com.productcatalogservice.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BasketMapper {

    public BasketDTO toBasketDTO(Map<Long, Product> basket) {
        BasketDTO dto = new BasketDTO();
        if (basket != null) {
            Map<Long, BasketDTO.ProductItem> items = basket.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> toBasketItem(entry.getValue())
                    ));
            dto.setItems(items);
        }
        return dto;
    }

    public BasketDTO.ProductItem toBasketItem(Product product) {
        BasketDTO.ProductItem item = new BasketDTO.ProductItem();
        item.setId(product.getId());
        item.setName(product.getName());
        item.setQuantity(product.getQuantity());
        item.setPrice(product.getPrice());
        item.setCategoryId(product.getCategoryId());
        return item;
    }
}
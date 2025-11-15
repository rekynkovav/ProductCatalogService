package org.example.repository.impl;

import org.example.config.impl.UserSecurityConfigImpl;
import org.example.model.entity.Product;
import org.example.model.entity.User;
import org.example.repository.AuditRepository;

import java.util.HashMap;

/**
 * класс содержаший 2 мапы в которые сохраняется сериализуется и десериализуется
 * информация по действиям всех пользователей а именно:
 * добавление удаление изменение товаров добавление товаров в корзину
 */
public class AuditRepositoryImpl implements AuditRepository {
    private HashMap<Long, Product> popularProducts;
    private HashMap<String, User> requestUser;
    private UserSecurityConfigImpl userSecurityConfig;

    public void setUserSecurityConfig(UserSecurityConfigImpl userSecurityConfig) {
        this.userSecurityConfig = userSecurityConfig;
    }

    {
        popularProducts = new HashMap<>(50);
        requestUser = new HashMap<>(50);
        userSecurityConfig = new UserSecurityConfigImpl();
    }

    @Override
    public void setPopularProducts(long id) {
        /**
         * этим методом добавляем в список популярных запросов продукты которые по id возьмем у текущего пользователя из корзины
         * добавляет счетчик запросов к этому товару на 1
         */
        popularProducts.put(id, userSecurityConfig.getThisUser().getMapBasket().get(id));
        popularProducts.get(id).appendQuantity(1);
    }

    @Override
    public HashMap<Long, Product> getMapPopularProducts() {
        return popularProducts;
    }

    @Override
    public HashMap<String, User> getMapRequestUser() {
        return requestUser;
    }

    @Override
    public void setRequestUser(HashMap<String, User> requestUser) {
        this.requestUser = requestUser;
    }

    @Override
    public void auditUserAddBasket(String name) {
        userSecurityConfig.getThisUser().appendAddBasket();
    }

    @Override
    public void setMapRequestUser(HashMap<String, User> map) {
        if (map != null) {
            this.requestUser = map;
        }
    }

    @Override
    public void setMapPopularProducts(HashMap<Long, Product> map) {
        if (map != null) {
            this.popularProducts = map;
        }
    }


}

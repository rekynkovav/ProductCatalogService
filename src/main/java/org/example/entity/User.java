package org.example.entity;

import org.example.shop.Shop;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {

    private static final Long serialVersionUID = 1L;
    private String userName;
    private String password;
    private int in;
    private int out;
    private int addProducts;
    private int deleteProducts;
    private int modificationProducts;
    private int addBasket;
    private Role role;
    private HashMap<Long,Product> mapBasket;

    {
        in = 0;
        out = 0;
        addProducts = 0;
        deleteProducts = 0;
        modificationProducts = 0;
        addBasket = 0;
        mapBasket = new HashMap<>(Shop.productMap.size());
    }

    public User(String userName, String password, Role role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public HashMap<Long, Product> getMapBasket() {
        return mapBasket;
    }

    public int getAddBasket() {
        return addBasket;
    }

    public void setAddBasket(int addBasket) {
        this.addBasket = addBasket;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public int getAddProducts() {
        return addProducts;
    }

    public void setAddProducts(int addProducts) {
        this.addProducts = addProducts;
    }

    public int getDeleteProducts() {
        return deleteProducts;
    }

    public void setDeleteProducts(int deleteProducts) {
        this.deleteProducts = deleteProducts;
    }

    public int getModificationProducts() {
        return modificationProducts;
    }

    public void setModificationProducts(int modificationProducts) {
        this.modificationProducts = modificationProducts;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}

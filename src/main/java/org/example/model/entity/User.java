package org.example.model.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class User implements Serializable {

    private static final long SerialVersionUID = 1;
    private String userName;
    private String password;
    private int in;
    private int out;
    private int addProducts;
    private int deleteProducts;
    private int modificationProducts;
    private int addBasket;
    private Role role;

    private HashMap<Long, Product> mapBasket;

    {
        in = 0;
        out = 0;
        addProducts = 0;
        deleteProducts = 0;
        modificationProducts = 0;
        addBasket = 0;
        mapBasket = new HashMap<>(50);
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

    public void setMapBasket(HashMap<Long, Product> mapBasket) {
        this.mapBasket = mapBasket;
    }

    public int getAddBasket() {
        return addBasket;
    }

    public void appendAddBasket() {
        addBasket++;
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

    public void appendOut() {
        out++;
    }

    public int getAddProducts() {
        return addProducts;
    }

    public void AddCountProducts() {
        addProducts++;
    }

    public int getDeleteProducts() {
        return deleteProducts;
    }

    public void addCountDeleteProducts() {
        deleteProducts++;
    }

    public int getModificationProducts() {
        return modificationProducts;
    }

    public void addModificationProducts() {
        modificationProducts++;
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

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", in=" + in +
                ", out=" + out +
                ", addProducts=" + addProducts +
                ", deleteProducts=" + deleteProducts +
                ", modificationProducts=" + modificationProducts +
                ", addBasket=" + addBasket +
                ", role=" + role +
                ", mapBasket=" + mapBasket +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return in == user.in && out == user.out && addProducts == user.addProducts && deleteProducts == user.deleteProducts && modificationProducts == user.modificationProducts && addBasket == user.addBasket && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && role == user.role && Objects.equals(mapBasket, user.mapBasket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, in, out, addProducts, deleteProducts, modificationProducts, addBasket, role, mapBasket);
    }
}

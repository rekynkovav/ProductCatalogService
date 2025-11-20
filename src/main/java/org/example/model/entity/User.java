package org.example.model.entity;

import java.util.HashMap;
import java.util.Objects;

/**
 * Модель пользователя системы.
 * Содержит основную информацию о пользователе и его корзину товаров.
 * Реализует интерфейс Serializable для поддержки сериализации.
 */
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    private long id;

    /**
     * Имя пользователя для входа в систему.
     */
    private String userName;

    /**
     * Пароль пользователя.
     */
    private String password;

    /**
     * Роль пользователя в системе.
     */
    private Role role;

    /**
     * Корзина товаров пользователя.
     * Ключ - идентификатор товара, значение - товар с количеством.
     */
    private HashMap<Long, Product> mapBasket;

    /**
     * Конструктор по умолчанию.
     * Инициализирует пустую корзину товаров.
     */
    public User() {
        this.mapBasket = new HashMap<>();
    }

    /**
     * Конструктор с параметрами.
     *
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @param role     роль пользователя
     */
    public User(String userName, String password, Role role) {
        this();
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param id идентификатор пользователя
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param userName имя пользователя
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Возвращает роль пользователя.
     *
     * @return роль пользователя
     */
    public Role getRole() {
        return role;
    }

    /**
     * Устанавливает роль пользователя.
     *
     * @param role роль пользователя
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Возвращает корзину товаров пользователя.
     *
     * @return карта товаров в корзине
     */
    public HashMap<Long, Product> getMapBasket() {
        return mapBasket;
    }

    /**
     * Устанавливает корзину товаров пользователя.
     *
     * @param mapBasket карта товаров в корзине
     */
    public void setMapBasket(HashMap<Long, Product> mapBasket) {
        this.mapBasket = mapBasket;
    }

    /**
     * Возвращает строковое представление пользователя.
     *
     * @return строка с информацией о пользователе
     */
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", userName='" + userName + '\'' +
               ", role=" + role +
               ", basketItems=" + mapBasket.size() +
               '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && role == user.role && Objects.equals(mapBasket, user.mapBasket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, userName);
    }
}
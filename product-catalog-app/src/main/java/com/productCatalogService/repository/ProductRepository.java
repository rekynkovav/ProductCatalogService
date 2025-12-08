package com.productCatalogService.repository;

import com.productCatalogService.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с товарами магазина.
 * <p>
 * Определяет интерфейс для доступа к данным товаров ({@link Product})
 * и управления ими в системе хранения. Поддерживает все основные
 * CRUD-операции и специализированные запросы для работы с каталогом товаров.
 * </p>
 *
 * <h3>Основные возможности:</h3>
 * <ul>
 *   <li>Полное управление товарами (CRUD)</li>
 *   <li>Поиск товаров по категориям</li>
 *   <li>Проверка существования товаров</li>
 *   <li>Пагинация и сортировка (в реализации)</li>
 * </ul>
 *
 * @see Product
 * @since 1.0
 */
public interface ProductRepository {

    /**
     * Сохраняет товар в базе данных.
     * <p>
     * Выполняет операцию сохранения или обновления товара:
     * <ul>
     *   <li>Для нового товара ({@code product.getId() == null}) создаёт запись</li>
     *   <li>Для существующего товара обновляет данные</li>
     * </ul>
     * Метод автоматически генерирует идентификатор для новых товаров
     * и возвращает объект с установленным ID.
     * </p>
     *
     * @param product товар для сохранения (не может быть {@code null})
     * @return сохраненный товар с присвоенным идентификатором
     * @throws IllegalArgumentException если {@code product} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     * @throws org.springframework.dao.DuplicateKeyException при нарушении уникальных ограничений
     */
    Product save(Product product);

    /**
     * Находит товар по его уникальному идентификатору.
     * <p>
     * Поиск выполняется по первичному ключу таблицы товаров.
     * Используется для получения детальной информации о конкретном товаре,
     * например, при просмотре карточки товара или редактировании.
     * </p>
     *
     * @param id идентификатор товара (положительное число)
     * @return {@link Optional} с найденным товаром или empty если товар не найден
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    Optional<Product> findById(Long id);

    /**
     * Возвращает список всех товаров из каталога.
     * <p>
     * Метод загружает все товары из хранилища, отсортированные по идентификатору.
     * Может использоваться для отображения полного каталога,
     * экспорта данных или административных задач.
     * </p>
     *
     * @return список всех товаров в базе данных;
     *         если товаров нет, возвращается пустой список
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    List<Product> findAll();

    /**
     * Находит товары, принадлежащие указанной категории.
     * <p>
     * Поиск выполняется по внешнему ключу {@code categoryId}.
     * Используется для фильтрации каталога по категориям,
     * например, при отображении товаров определённой категории.
     * </p>
     *
     * @param categoryId идентификатор категории для поиска
     * @return список товаров указанной категории, отсортированных по ID;
     *         если в категории нет товаров, возвращается пустой список
     * @throws IllegalArgumentException если {@code categoryId} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Удаляет товар по его идентификатору.
     * <p>
     * Метод выполняет физическое удаление записи товара из базы данных.
     * Перед удалением рекомендуется проверить, не используется ли товар
     * в заказах или корзинах пользователей.
     * </p>
     *
     * @param id идентификатор товара для удаления
     * @return {@code true} если товар был успешно удалён,
     *         {@code false} если товар с указанным ID не найден
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    boolean deleteById(Long id);

    /**
     * Проверяет существование товара по идентификатору.
     * <p>
     * Выполняет быструю проверку наличия товара в базе данных
     * без загрузки полного объекта. Используется для валидации
     * перед выполнением операций с товаром (обновление, удаление,
     * добавление в корзину).
     * </p>
     *
     * @param id идентификатор товара для проверки
     * @return {@code true} если товар с указанным ID существует,
     *         {@code false} в противном случае
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данных
     */
    boolean existsById(Long id);

    /**
     * Мeтод для уменьшения количества товара в магазине при добавлении в корзину
     */
    boolean decreaseQuantity(Long productId, int quantity);
    /**
     * Мeтод для увеличения количества товара в магазине при удалении из корзины
     */
    boolean increaseQuantity(Long productId, int quantity);

    /**
     * Находит товары с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы
     * @return список товаров для указанной страницы
     */
    List<Product> findAllPaginated(int page, int size);

    /**
     * Возвращает общее количество товаров.
     *
     * @return общее количество товаров в базе данных
     */
    Long count();

    /**
     * Находит все товары по списку идентификаторов.
     * <p>
     * Метод выполняет пакетный поиск товаров по переданным идентификаторам.
     * Используется для эффективной загрузки информации о товарах,
     * например при отображении корзины пользователя.
     * </p>
     *
     * @param ids список идентификаторов товаров (не может быть {@code null})
     * @return список найденных товаров;
     *         если ни один товар не найден, возвращается пустой список
     * @throws IllegalArgumentException если {@code ids} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    List<Product> findAllById(Iterable<Long> ids);
}
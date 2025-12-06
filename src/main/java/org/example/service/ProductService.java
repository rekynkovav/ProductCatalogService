package org.example.service;

import org.example.dto.ProductDTO;
import org.example.dto.ProductPageDTO;
import org.example.model.entity.Product;
import java.util.List;
import java.util.Optional;

/**
 * Сервисный интерфейс для управления товарами.
 * Определяет контракт для работы с товарами в каталоге, включая CRUD операции,
 * управление количеством и преобразование данных между слоями приложения.
 *
 * <p>Сервис разделен на две основные части:</p>
 * <ul>
 *   <li>Методы для работы с доменными объектами ({@link Product})</li>
 *   <li>Методы для работы с DTO ({@link ProductDTO}) и административные операции</li>
 * </ul>
 *
 * <h3>Ключевые возможности:</h3>
 * <ul>
 *   <li>Полное управление товарами (CRUD операции)</li>
 *   <li>Фильтрация товаров по категориям</li>
 *   <li>Контроль остатков товаров на складе</li>
 *   <li>Пагинация товаров для оптимизации производительности</li>
 *   <li>Валидация данных и бизнес-правил</li>
 *   <li>Адаптация данных для клиентских приложений через DTO</li>
 * </ul>
 *
 * @see Product
 * @see ProductDTO
 * @see ProductPageDTO
 * @see org.example.service.impl.ProductServiceImpl
 * @see org.example.repository.ProductRepository
 * @since 1.0
 */
public interface ProductService {

    /**
     * Возвращает список всех товаров из каталога.
     * <p>
     * Используется для отображения полного ассортимента магазина.
     * В реализации может поддерживать пагинацию, сортировку и фильтрацию
     * для оптимизации производительности при большом количестве товаров.
     * </p>
     *
     * @return список всех товаров; если товаров нет, возвращается пустой список
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    List<Product> findAll();

    /**
     * Находит товар по его уникальному идентификатору.
     * <p>
     * Используется для получения детальной информации о товаре
     * при просмотре карточки товара, редактировании или добавлении в корзину.
     * Возвращает {@link Optional} для безопасной обработки отсутствия товара.
     * </p>
     *
     * @param id идентификатор товара (должен быть положительным числом)
     * @return {@link Optional} содержащий товар, если найден,
     *         или пустой {@link Optional} если товар не существует
     * @throws IllegalArgumentException если {@code id} равен {@code null} или неположителен
     */
    Optional<Product> findById(Long id);

    /**
     * Сохраняет или обновляет товар в каталоге.
     * <p>
     * Выполняет комплексную валидацию:
     * <ul>
     *   <li>Проверяет корректность данных товара</li>
     *   <li>Проверяет существование указанной категории</li>
     *   <li>Валидирует бизнес-правила (цена, количество и т.д.)</li>
     *   <li>Обрабатывает аудит изменений (кто и когда изменил)</li>
     * </ul>
     * </p>
     *
     * @param product товар для сохранения (не может быть {@code null})
     * @return сохраненный товар с присвоенным идентификатором
     * @throws IllegalArgumentException если {@code product} равен {@code null}
     * @throws javax.validation.ConstraintViolationException если данные товара невалидны
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     * @throws IllegalStateException если указанная категория не существует
     */
    Product save(Product product);

    /**
     * Удаляет товар по его идентификатору.
     * <p>
     * Выполняет проверки перед удалением:
     * <ul>
     *   <li>Проверяет, не находится ли товар в корзинах пользователей</li>
     *   <li>Проверяет, нет ли активных заказов с этим товаром</li>
     *   <li>При необходимости выполняет "мягкое" удаление (помечает как удалённый)</li>
     * </ul>
     * Возвращает результат операции для информирования клиента.
     * </p>
     *
     * @param id идентификатор товара для удаления
     * @return {@code true} если товар был успешно удалён,
     *         {@code false} если товар не найден или не может быть удалён
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     * @throws IllegalStateException если товар не может быть удалён из-за бизнес-правил
     */
    boolean deleteById(Long id);

    /**
     * Проверяет существование товара по идентификатору.
     * <p>
     * Используется для быстрой проверки наличия товара без загрузки полного объекта.
     * Применяется в различных сценариях:
     * <ul>
     *   <li>Валидация перед добавлением товара в корзину</li>
     *   <li>Проверка существования перед обновлением</li>
     *   <li>Подтверждение наличия товара для UI</li>
     * </ul>
     * </p>
     *
     * @param id идентификатор товара для проверки
     * @return {@code true} если товар с указанным ID существует,
     *         {@code false} в противном случае
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     */
    boolean existsById(Long id);

    /**
     * Находит товары по идентификатору категории.
     * <p>
     * Используется для фильтрации каталога по категориям.
     * Может применяться с дополнительными параметрами:
     * <ul>
     *   <li>Сортировка по цене, популярности, новизне</li>
     *   <li>Пагинация для больших категорий</li>
     *   <li>Фильтрация по дополнительным критериям (цена, наличие)</li>
     * </ul>
     * Если категория не существует или пуста, возвращается пустой список.
     * </p>
     *
     * @param categoryId идентификатор категории для фильтрации
     * @return список товаров указанной категории;
     *         если товаров нет или категория не существует, возвращается пустой список
     * @throws IllegalArgumentException если {@code categoryId} равен {@code null}
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Уменьшает количество товара на складе.
     * Используется при добавлении товара в корзину или оформлении заказа.
     *
     * @param productId идентификатор товара
     * @param quantity количество для уменьшения (должно быть положительным)
     * @return true если операция успешна, false если недостаточно товара на складе
     * @throws IllegalArgumentException если параметры некорректны
     */
    boolean decreaseQuantity(Long productId, int quantity);

    /**
     * Увеличивает количество товара на складе.
     * Используется при удалении товара из корзины или отмене заказа.
     *
     * @param productId идентификатор товара
     * @param quantity количество для увеличения (должно быть положительным)
     * @return true если операция успешна, false если товар не найден
     * @throws IllegalArgumentException если параметры некорректны
     */
    boolean increaseQuantity(Long productId, int quantity);

    /**
     * Находит товары с пагинацией.
     * Возвращает подмножество товаров для указанной страницы.
     *
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы (количество товаров на странице)
     * @return список товаров для указанной страницы
     * @throws IllegalArgumentException если page < 0 или size <= 0
     */
    List<Product> findAllPaginated(int page, int size);

    /**
     * Возвращает общее количество товаров в каталоге.
     *
     * @return общее количество товаров в базе данных
     */
    Long count();

    /**
     * Получает страницу товаров в формате DTO.
     * Включает информацию о пагинации (общее количество страниц, элементов и т.д.).
     *
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы
     * @return DTO страницы товаров
     * @throws IllegalArgumentException если параметры некорректны
     */
    ProductPageDTO getPaginatedProducts(int page, int size);

    /**
     * Получает товар по идентификатору в формате DTO.
     *
     * @param id идентификатор товара
     * @return DTO товара
     */
    ProductDTO getProductById(Long id);

    /**
     * Получает товары по идентификатору категории в формате DTO.
     *
     * @param categoryId идентификатор категории
     * @return список DTO товаров указанной категории
     */
    List<ProductDTO> getProductsByCategoryId(Long categoryId);

    /**
     * Создает новый товар (административная операция).
     *
     * @param token токен авторизации администратора
     * @param createProduct DTO с данными для создания товара
     * @return DTO созданного товара
     * @throws IllegalArgumentException если параметры некорректны
     */
    ProductDTO createProduct(String token, ProductDTO.CreateProduct createProduct);

    /**
     * Обновляет существующий товар (административная операция).
     *
     * @param token токен авторизации администратора
     * @param id идентификатор товара для обновления
     * @param updateProduct DTO с данными для обновления
     * @return DTO обновленного товара
     */
    ProductDTO updateProduct(String token, Long id, ProductDTO.UpdateProduct updateProduct);

    /**
     * Удаляет товар (административная операция).
     *
     * @param token токен авторизации администратора
     * @param id идентификатор товара для удаления
     */
    void deleteProduct(String token, Long id);
}
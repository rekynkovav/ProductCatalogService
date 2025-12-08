package com.productcatalogservice.service;

import com.productcatalogservice.dto.CategoryDTO;
import com.productcatalogservice.dto.ProductDTO;
import com.productcatalogservice.entity.Category;
import com.productcatalogservice.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный интерфейс для управления категориями товаров.
 * Определяет контракт для работы с категориями, включая CRUD операции,
 * работу с товарами внутри категорий и преобразование данных между слоями приложения.
 *
 * <p>Сервис разделен на две основные части:</p>
 * <ul>
 *   <li>Методы для работы с доменными объектами ({@link Category}, {@link Product})</li>
 *   <li>Методы для работы с DTO ({@link CategoryDTO}, {@link ProductDTO}) и административные операции</li>
 * </ul>
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Управление жизненным циклом категорий (CRUD)</li>
 *   <li>Поиск категорий по различным критериям</li>
 *   <li>Работа с товарами внутри категорий</li>
 *   <li>Валидация бизнес-правил для категорий</li>
 *   <li>Контроль целостности данных при удалении категорий</li>
 *   <li>Адаптация данных для клиентских приложений через DTO</li>
 * </ul>
 *
 * @see Category
 * @see Product
 * @see CategoryDTO
 * @see ProductDTO
 * @see org.example.service.impl.CategoryServiceImpl
 * @see org.example.repository.CategoryRepository
 * @since 1.0
 */
public interface CategoryService {

    /**
     * Возвращает список всех категорий из системы.
     * <p>
     * Используется для отображения полного перечня категорий
     * в пользовательском интерфейсе или для административных задач.
     * Метод может применяться для построения меню навигации по категориям.
     * </p>
     *
     * @return список всех категорий; если категорий нет, возвращается пустой список
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    List<Category> findAll();

    /**
     * Находит категорию по её уникальному идентификатору.
     * <p>
     * Используется для получения детальной информации о конкретной категории,
     * например, при редактировании категории или отображении её свойств.
     * Возвращает {@link Optional} для явной обработки случая отсутствия категории.
     * </p>
     *
     * @param id идентификатор категории (должен быть положительным числом)
     * @return {@link Optional} содержащий категорию, если найдена,
     *         или пустой {@link Optional} если категория не существует
     * @throws IllegalArgumentException если {@code id} равен {@code null} или неположителен
     */
    Optional<Category> findById(Long id);

    /**
     * Сохраняет или обновляет категорию в системе.
     * <p>
     * Выполняет бизнес-валидацию перед сохранением:
     * <ul>
     *   <li>Проверяет уникальность названия категории</li>
     *   <li>Валидирует соответствие формату названия</li>
     *   <li>Обрабатывает каскадные обновления связанных сущностей</li>
     * </ul>
     * Для новых категорий генерирует идентификатор.
     * </p>
     *
     * @param category категория для сохранения (не может быть {@code null})
     * @return сохранённая категория с установленным идентификатором
     * @throws IllegalArgumentException если {@code category} равен {@code null}
     * @throws org.springframework.dao.DuplicateKeyException при попытке сохранить категорию с существующим названием
     */
    Category save(Category category);

    /**
     * Удаляет категорию по её идентификатору.
     * <p>
     * Выполняет проверки перед удалением:
     * <ul>
     *   <li>Проверяет существование категории</li>
     *   <li>Проверяет, нет ли товаров в этой категории (опционально)</li>
     *   <li>При необходимости выполняет каскадное удаление или перенос товаров</li>
     * </ul>
     * Если категория не найдена, метод может игнорировать операцию или бросать исключение.
     * </p>
     *
     * @param id идентификатор категории для удаления
     * @return true если категория была успешно удалена, false если категория не найдена
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     * @throws IllegalStateException если категория содержит товары и не может быть удалена
     */
    boolean deleteById(Long id);

    /**
     * Находит все товары, принадлежащие указанной категории.
     * <p>
     * Используется для отображения товаров конкретной категории в каталоге.
     * Метод может поддерживать пагинацию и сортировку в реализации.
     * Если категория не существует или пуста, возвращается пустой список.
     * </p>
     *
     * @param categoryId идентификатор категории (не может быть {@code null})
     * @return список товаров указанной категории; если товаров нет или категория не существует,
     *         возвращается пустой список
     * @throws IllegalArgumentException если {@code categoryId} равен {@code null}
     */
    List<Product> findProductsByCategoryId(Long categoryId);

    /**
     * Находит категорию по её названию.
     * <p>
     * Используется для проверки уникальности названия при создании/редактировании категорий,
     * а также для поиска категории по имени в пользовательском интерфейсе.
     * Поиск чувствителен к регистру в зависимости от реализации базы данных.
     * </p>
     *
     * @param name название категории для поиска (не может быть {@code null} или пустым)
     * @return {@link Optional} содержащий категорию, если найдена,
     *         или пустой {@link Optional} если категория не существует
     * @throws IllegalArgumentException если {@code name} равен {@code null} или пуст
     */
    Optional<Category> findByName(String name);

    /**
     * Получает список всех категорий в формате DTO.
     *
     * @return список DTO категорий
     */
    List<CategoryDTO> getAllCategories();

    /**
     * Получает категорию по идентификатору в формате DTO.
     *
     * @param id идентификатор категории
     * @return DTO категории
     */
    CategoryDTO getCategoryById(Long id);

    /**
     * Получает товары категории по идентификатору в формате DTO.
     *
     * @param categoryId идентификатор категории
     * @return список DTO товаров указанной категории
     */
    List<ProductDTO> getProductsByCategoryIdDto(Long categoryId);

    /**
     * Создает новую категорию (административная операция).
     *
     * @param token токен авторизации администратора
     * @param createCategory DTO с данными для создания категории
     * @return DTO созданной категории
     * @throws IllegalArgumentException если параметры некорректны
     */
    CategoryDTO createCategory(String token, CategoryDTO.CreateCategory createCategory);

    /**
     * Обновляет существующую категорию (административная операция).
     *
     * @param token токен авторизации администратора
     * @param id идентификатор категории для обновления
     * @param updateCategory DTO с данными для обновления категории
     * @return DTO обновленной категории
     */
    CategoryDTO updateCategory(String token, Long id, CategoryDTO.UpdateCategory updateCategory);

    /**
     * Удаляет категорию (административная операция).
     *
     * @param token токен авторизации администратора
     * @param id идентификатор категории для удаления
     * @return true если категория была успешно удалена, false если категория не найдена
     * @throws IllegalStateException если категория содержит товары и не может быть удалена
     */
    Boolean deleteCategory(String token, Long id);
}
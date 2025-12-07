package org.example.repository;

import org.example.model.entity.Category;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с категориями товаров.
 * <p>
 * Определяет контракт для операций CRUD (Create, Read, Update, Delete)
 * с сущностью {@link Category} в системе хранения данных.
 * Реализация абстрагирует доступ к данным, позволяя легко менять
 * технологию хранения (JDBC, JPA, etc.).
 * </p>
 *
 * <h3>Основные возможности:</h3>
 * <ul>
 *   <li>Поиск категорий по различным критериям</li>
 *   <li>Сохранение и обновление категорий</li>
 *   <li>Удаление категорий</li>
 *   <li>Проверка существования категорий</li>
 * </ul>
 *
 * @see Category
 * @see org.example.repository.impl.CategoryRepositoryImpl
 * @since 1.0
 */
public interface CategoryRepository {

    /**
     * Возвращает список всех категорий из хранилища.
     * <p>
     * Метод используется для получения полного перечня категорий,
     * например, для отображения в пользовательском интерфейсе
     * или для административных задач.
     * </p>
     *
     * @return список всех категорий; если категорий нет, возвращается пустой список
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    List<Category> findAll();

    /**
     * Находит категорию по её уникальному идентификатору.
     * <p>
     * Поиск выполняется по первичному ключу таблицы категорий.
     * Используется {@link Optional} для явного указания возможности
     * отсутствия категории с указанным ID.
     * </p>
     *
     * @param id идентификатор категории (положительное число)
     * @return {@link Optional} содержащий категорию, если найдена,
     *         или пустой {@link Optional} если категория не существует
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    Optional<Category> findById(Long id);

    /**
     * Находит категорию по её имени.
     * <p>
     * Поиск выполняется по полю {@code name}, которое должно быть уникальным
     * в системе. Используется для проверки существования категории
     * перед созданием новой.
     * </p>
     *
     * @param name название категории для поиска (не может быть {@code null} или пустым)
     * @return {@link Optional} содержащий категорию, если найдена,
     *         или пустой {@link Optional} если категория не существует
     * @throws IllegalArgumentException если {@code name} равен {@code null} или пуст
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    Optional<Category> findByName(String name);

    /**
     * Сохраняет или обновляет категорию в хранилище.
     * <p>
     * Метод выполняет операцию "upsert" (insert или update):
     * <ul>
     *   <li>Если {@code category.getId()} равен {@code null} - создаёт новую категорию</li>
     *   <li>Если {@code category.getId()} не равен {@code null} - обновляет существующую категорию</li>
     * </ul>
     * После успешного сохранения возвращает объект категории
     * с присвоенным идентификатором (для новых записей).
     * </p>
     *
     * @param category категория для сохранения (не может быть {@code null})
     * @return сохранённая категория с установленным идентификатором
     * @throws IllegalArgumentException если {@code category} равен {@code null}
     * @throws org.springframework.dao.DuplicateKeyException при попытке сохранить категорию с дублирующимся именем
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    Category save(Category category);

    /**
     * Удаляет категорию по её идентификатору.
     * <p>
     * Метод удаляет запись категории из хранилища. Перед удалением
     * рекомендуется проверить, нет ли связанных товаров с этой категорией.
     * В случае успешного удаления метод не возвращает значение.
     * </p>
     *
     * @param id идентификатор категории для удаления
     * @return
     * @throws IllegalArgumentException                               если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException            при ошибках доступа к данным
     * @throws org.springframework.dao.EmptyResultDataAccessException если категория с указанным ID не найдена
     */
    boolean deleteById(Long id);

    /**
     * Проверяет существование категории по идентификатору.
     * <p>
     * Метод выполняет быструю проверку наличия категории в хранилище
     * без загрузки полного объекта. Используется для валидации
     * перед выполнением операций с категорией.
     * </p>
     *
     * @param id идентификатор категории для проверки
     * @return {@code true} если категория с указанным ID существует,
     *         {@code false} в противном случае
     * @throws IllegalArgumentException если {@code id} равен {@code null}
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    boolean existsById(Long id);
}
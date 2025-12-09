package com.productCatalogService.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Компонент для хеширования и проверки паролей.
 * Использует алгоритм SHA-256 с солью для безопасного хранения паролей.
 */
@Component
public class PasswordEncoder {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String DELIMITER = ":";

    /**
     * Хеширует пароль с солью.
     *
     * @param rawPassword исходный пароль
     * @return закодированный пароль в формате Base64
     * @throws RuntimeException если произошла ошибка при хешировании
     */
    public String encode(CharSequence rawPassword) {
        try {
            // Генерация соли
            byte[] salt = generateSalt();

            // Создание хеша пароля с солью
            byte[] hash = hashWithSalt(rawPassword.toString(), salt);

            // Объединение соли и хеша: соль:хеш
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + DELIMITER + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка при кодировании пароля: алгоритм не найден", e);
        }
    }

    /**
     * Проверяет соответствие исходного пароля закодированному.
     *
     * @param rawPassword исходный пароль
     * @param encodedPassword закодированный пароль
     * @return true если пароли совпадают, иначе false
     * @throws RuntimeException если произошла ошибка при проверке
     */
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            if (encodedPassword == null || !encodedPassword.contains(DELIMITER)) {
                return false;
            }

            // Разделение соли и хеша
            String[] parts = encodedPassword.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            // Хеширование введенного пароля с той же солью
            byte[] inputHash = hashWithSalt(rawPassword.toString(), salt);

            // Сравнение хешей
            return MessageDigest.isEqual(storedHash, inputHash);

        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            throw new RuntimeException("Ошибка при проверке пароля", e);
        }
    }

    /**
     * Генерирует случайную соль заданной длины.
     *
     * @return массив байтов соли
     */
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Хеширует пароль с солью.
     *
     * @param password пароль
     * @param salt соль
     * @return хеш пароля
     * @throws NoSuchAlgorithmException если алгоритм не найден
     */
    private byte[] hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);

        // Добавляем соль к паролю
        digest.update(salt);
        digest.update(password.getBytes(StandardCharsets.UTF_8));

        return digest.digest();
    }

    /**
     * Проверяет, является ли пароль закодированным (содержит разделитель).
     *
     * @param password пароль для проверки
     * @return true если пароль уже закодирован, иначе false
     */
    public boolean isEncoded(String password) {
        return password != null && password.contains(DELIMITER);
    }

    /**
     * Генерирует случайный пароль заданной длины.
     * Может использоваться для сброса пароля или генерации временных паролей.
     *
     * @param length длина пароля
     * @return случайный пароль
     */
    public String generateRandomPassword(int length) {
        if (length < 6) {
            length = 6; // Минимальная безопасная длина
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=[]{}|;:,.<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}
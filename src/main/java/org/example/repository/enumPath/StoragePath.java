package org.example.repository.enumPath;

public enum StoragePath {
    USER("src/main/resources/user.txt"),
    PRODUCT("src/main/resources/product.txt"),
    REQUEST("src/main/resources/request.txt"),
    POPULAR_PRODUCT("src/main/resources/popularProduct.txt");

    private final String path;

    StoragePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

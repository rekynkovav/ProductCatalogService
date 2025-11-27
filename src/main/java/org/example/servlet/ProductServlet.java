package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.example.context.ApplicationContext;
import org.example.model.dto.ApiResponse;
import org.example.model.dto.ProductDTO;
import org.example.model.dto.ProductMapper;
import org.example.model.entity.Product;
import org.example.service.ProductService;
import jakarta.validation.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {
    private ProductService productService;
    private ObjectMapper objectMapper;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        this.productService = ApplicationContext.getInstance().getBean(ProductService.class);
        this.objectMapper = new ObjectMapper();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/products - получить все продукты
                List<ProductDTO> products = ProductMapper.INSTANCE.productsToProductDTOs(
                        productService.getAllProduct()
                );
                sendSuccessResponse(response, products);

            } else {
                // GET /api/products/{id} - получить продукт по ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    Long productId = Long.parseLong(pathParts[1]);
                    Optional<Product> optionalProduct = productService.findById(productId);
                    if (optionalProduct.isPresent()) {
                        Product product = optionalProduct.get();
                        ProductDTO productDTO = ProductMapper.INSTANCE.productToProductDTO(product);
                        sendSuccessResponse(response, productDTO);
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid URL format");
                }
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product ID format");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ProductDTO productDTO = objectMapper.readValue(request.getInputStream(), ProductDTO.class);

            // Валидация DTO
            Set<ConstraintViolation<ProductDTO>> violations = validator.validate(productDTO);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.joining(", "));

                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, errorMessage);
                return;
            }

            // Сохранение продукта
            ProductDTO savedProduct = ProductMapper.INSTANCE.productToProductDTO(
                    productService.saveProduct(ProductMapper.INSTANCE.productDTOToProduct(productDTO))
            );

            response.setStatus(HttpServletResponse.SC_CREATED);
            sendSuccessResponse(response, "Product created successfully", savedProduct);

        } catch (IOException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JSON format");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error creating product: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.split("/").length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Product ID is required");
                return;
            }

            Long productId = Long.parseLong(pathInfo.split("/")[1]);
            ProductDTO productDTO = objectMapper.readValue(request.getInputStream(), ProductDTO.class);
            productDTO.setId(productId);

            // Валидация DTO
            Set<ConstraintViolation<ProductDTO>> violations = validator.validate(productDTO);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.joining(", "));

                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, errorMessage);
                return;
            }

            // Обновление продукта
            ProductDTO updatedProduct = ProductMapper.INSTANCE.productToProductDTO(
                    productService.updateProduct(ProductMapper.INSTANCE.productDTOToProduct(productDTO))
            );

            if (updatedProduct != null) {
                sendSuccessResponse(response, "Product updated successfully", updatedProduct);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Product not found with id: " + productId);
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product ID format");
        } catch (IOException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JSON format");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error updating product: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.split("/").length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Product ID is required");
                return;
            }

            Long productId = Long.parseLong(pathInfo.split("/")[1]);
            boolean deleted = productService.deleteProductById(productId);

            if (deleted) {
                sendSuccessResponse(response, "Product deleted successfully", null);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Product not found with id: " + productId);
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid product ID format");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error deleting product: " + e.getMessage());
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.success(data);
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    private void sendSuccessResponse(HttpServletResponse response, String message, Object data) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.success(message, data);
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.error(message);
        response.setStatus(statusCode);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
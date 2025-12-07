import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthUtilTest {

    private AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        authUtil = new AuthUtil();
    }

    @Test
    void testGenerateToken() {
        String username = "testuser";
        String password = "testpass";

        String token = authUtil.generateToken(username, password);

        assertNotNull(token);
        String decoded = new String(Base64.getDecoder().decode(token));
        assertEquals("testuser:testpass", decoded);
    }

    @Test
    void testExtractToken() {
        String authHeader = "Bearer dGVzdHVzZXI6dGVzdHBhc3M=";

        String token = authUtil.extractToken(authHeader);

        assertEquals("dGVzdHVzZXI6dGVzdHBhc3M=", token);
    }

    @Test
    void testExtractToken_InvalidHeader() {
        String token1 = authUtil.extractToken("invalid");
        assertEquals("", token1);

        String token2 = authUtil.extractToken(null);
        assertEquals("", token2);

        String token3 = authUtil.extractToken("Basic token");
        assertEquals("", token3);
    }

    @Test
    void testSessionManagement() {
        String token = "testtoken";
        User user = new User();
        user.setUserName("testuser");
        user.setRole(Role.USER);

        authUtil.addSession(token, user);

        Optional<User> retrievedUser = authUtil.getUserFromToken("Bearer " + token);
        assertTrue(retrievedUser.isPresent());
        assertEquals("testuser", retrievedUser.get().getUserName());

        assertEquals(1, authUtil.getActiveSessionsCount());

        User removedUser = authUtil.removeSession(token);
        assertNotNull(removedUser);
        assertEquals("testuser", removedUser.getUserName());

        assertEquals(0, authUtil.getActiveSessionsCount());
    }

    @Test
    void testHasRole() {
        String token = "Bearer testtoken";
        User adminUser = new User();
        adminUser.setUserName("admin");
        adminUser.setRole(Role.ADMIN);

        User regularUser = new User();
        regularUser.setUserName("user");
        regularUser.setRole(Role.USER);

        authUtil.addSession("testtoken", adminUser);

        assertTrue(authUtil.hasRole(token, Role.ADMIN));
        assertFalse(authUtil.hasRole(token, Role.USER));

        authUtil.removeSession("testtoken");
        authUtil.addSession("testtoken", regularUser);

        assertFalse(authUtil.hasRole(token, Role.ADMIN));
        assertTrue(authUtil.hasRole(token, Role.USER));

        assertFalse(authUtil.hasRole("Bearer invalid", Role.ADMIN));
    }
}
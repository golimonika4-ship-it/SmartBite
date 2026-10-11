package com.smartbite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbite.dto.LoginRequest;
import com.smartbite.dto.OrderItemRequest;
import com.smartbite.dto.OrderRequest;
import com.smartbite.dto.RegisterRequest;
import com.smartbite.model.Role;
import com.smartbite.model.User;
import com.smartbite.repository.UserRepository;
import com.smartbite.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldRegisterStudentSuccessfullyAndHashPassword() throws Exception {
        String testEmail = "rahul.sharma" + System.currentTimeMillis() + "@campus.edu";
        String testRoll = "21CS" + (System.currentTimeMillis() % 10000);
        String rawPassword = "SecurePassword@123";

        RegisterRequest request = new RegisterRequest(
                "Rahul Sharma",
                testRoll,
                testEmail,
                rawPassword
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.name", is("Rahul Sharma")))
                .andExpect(jsonPath("$.rollNumber", is(testRoll)))
                .andExpect(jsonPath("$.email", is(testEmail)))
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        // Verify password is NOT stored as plaintext in MySQL database
        User savedUser = userRepository.findByEmailIgnoreCase(testEmail).orElse(null);
        assertNotNull(savedUser, "Registered user must exist in database");
        assertNotNull(savedUser.getPassword(), "User password must not be null");
        assertFalse(savedUser.getPassword().equals(rawPassword), "Password must NEVER be stored in plaintext");
        assertTrue(passwordEncoder.matches(rawPassword, savedUser.getPassword()), "BCrypt encoder must verify the hashed password");
    }

    @Test
    void shouldRejectDuplicateEmailRegistration() throws Exception {
        String duplicateEmail = "dup.email." + System.currentTimeMillis() + "@campus.edu";
        RegisterRequest req1 = new RegisterRequest("Student One", "ROLL_D1_" + (System.currentTimeMillis() % 1000), duplicateEmail, "Pass1234");
        RegisterRequest req2 = new RegisterRequest("Student Two", "ROLL_D2_" + (System.currentTimeMillis() % 1000), duplicateEmail, "Pass1234");

        // First registration succeeds
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        // Duplicate email must be rejected with 409 Conflict
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectDuplicateRollNumberRegistration() throws Exception {
        String duplicateRoll = "ROLL_DUP_" + (System.currentTimeMillis() % 10000);
        RegisterRequest req1 = new RegisterRequest("Student A", duplicateRoll, "studentA." + System.currentTimeMillis() + "@campus.edu", "Pass1234");
        RegisterRequest req2 = new RegisterRequest("Student B", duplicateRoll, "studentB." + System.currentTimeMillis() + "@campus.edu", "Pass1234");

        // First registration succeeds
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        // Duplicate roll number must be rejected with 409 Conflict
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAuthenticateUserByEmailAndByRollNumber() throws Exception {
        String email = "login.test." + System.currentTimeMillis() + "@campus.edu";
        String roll = "ROLL_LOGIN_" + (System.currentTimeMillis() % 10000);
        String pass = "LoginPass@2026";

        RegisterRequest regReq = new RegisterRequest("Login User", roll, email, pass);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        // Login using Email
        LoginRequest emailLogin = new LoginRequest(email, pass);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.rollNumber", is(roll)));

        // Login using Roll Number
        LoginRequest rollLogin = new LoginRequest(roll, pass);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rollLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.rollNumber", is(roll)));
    }

    @Test
    void shouldRejectLoginWithInvalidPasswordUsingGenericErrorMessage() throws Exception {
        String email = "failed.test." + System.currentTimeMillis() + "@campus.edu";
        String roll = "FAIL_" + (System.currentTimeMillis() % 10000);
        String pass = "RealPassword@123";

        RegisterRequest regReq = new RegisterRequest("Failed User", roll, email, pass);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        // Login with wrong password
        LoginRequest wrongPass = new LoginRequest(email, "WrongPassword@999");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPass)))
                .andExpect(status().isUnauthorized());

        // Login with non-existent account returns same 401 (prevent user enumeration)
        LoginRequest nonExistent = new LoginRequest("does.not.exist@campus.edu", "AnyPassword");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistent)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUserProfileForAuthenticatedStudent() throws Exception {
        String email = "profile.test." + System.currentTimeMillis() + "@campus.edu";
        String roll = "PROF_" + (System.currentTimeMillis() % 10000);
        String pass = "Pass@1234";

        User student = new User("Profile Student", roll, email, passwordEncoder.encode(pass), Role.STUDENT);
        userRepository.save(student);

        String token = jwtTokenProvider.generateToken(student);

        // Fetch profile using Bearer Authorization header
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Profile Student")))
                .andExpect(jsonPath("$.rollNumber", is(roll)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.role", is("STUDENT")));
    }

    @Test
    @WithAnonymousUser
    void shouldRejectUnauthenticatedAccessToProfileWith401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldPreventStudentFromViewingAnotherStudentsOrder() throws Exception {
        // Create student A and student B
        long ts = System.currentTimeMillis();
        User studentA = new User("Alice", "ROLL_A_" + (ts % 10000), "alice" + ts + "@campus.edu", passwordEncoder.encode("Pass123"), Role.STUDENT);
        User studentB = new User("Bob", "ROLL_B_" + (ts % 10000), "bob" + ts + "@campus.edu", passwordEncoder.encode("Pass123"), Role.STUDENT);
        userRepository.save(studentA);
        userRepository.save(studentB);

        String tokenA = jwtTokenProvider.generateToken(studentA);
        String tokenB = jwtTokenProvider.generateToken(studentB);

        // Student A places an order
        OrderRequest orderReq = new OrderRequest("ROLL_A", List.of(new OrderItemRequest("sb-b1", 1)));
        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + tokenA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        long orderId = objectMapper.readTree(orderResult.getResponse().getContentAsString()).get("id").asLong();

        // Student A can access their own order
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        // Student B attempting to access Student A's order must receive 403 Forbidden!
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldLogoutSuccessfullyAndClearAuthCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.message", is("Logged out successfully")));
    }

    @Test
    void shouldAuthenticateAdminViaDedicatedAdminEndpoint() throws Exception {
        long ts = System.currentTimeMillis();
        String adminEmail = "canteen.admin." + ts + "@smartbite.campus";
        String adminRoll = "ADM-" + (ts % 10000);
        String adminPass = "SecureAdminSecret@2026";

        User adminUser = new User("Canteen Manager", adminRoll, adminEmail, passwordEncoder.encode(adminPass), Role.ADMIN);
        userRepository.save(adminUser);

        LoginRequest req = new LoginRequest(adminEmail, adminPass);

        mockMvc.perform(post("/api/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email", is(adminEmail)))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void shouldRejectStudentAttemptingAdminLoginWith403() throws Exception {
        long ts = System.currentTimeMillis();
        String studentEmail = "student.fakeadmin." + ts + "@campus.edu";
        String studentRoll = "21CS" + (ts % 10000);
        String pass = "StudentPass@123";

        User student = new User("Fake Admin Student", studentRoll, studentEmail, passwordEncoder.encode(pass), Role.STUDENT);
        userRepository.save(student);

        LoginRequest req = new LoginRequest(studentEmail, pass);

        // Student credentials calling /api/auth/admin/login must be rejected with 403 Forbidden!
        mockMvc.perform(post("/api/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectAdminAttemptingStudentLoginWith403() throws Exception {
        long ts = System.currentTimeMillis();
        String adminEmail = "real.admin." + ts + "@smartbite.campus";
        String adminRoll = "ADM-STU-" + (ts % 10000);
        String adminPass = "AdminPassword@2026";

        User adminUser = new User("Real Admin", adminRoll, adminEmail, passwordEncoder.encode(adminPass), Role.ADMIN);
        userRepository.save(adminUser);

        LoginRequest req = new LoginRequest(adminEmail, adminPass);

        // Admin attempting to sign in on student endpoint must be directed to admin portal with 403
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldPreventStudentFromEscalatingRoleToAdminOnRegistration() throws Exception {
        long ts = System.currentTimeMillis();
        String email = "hacker.student." + ts + "@campus.edu";
        String roll = "HACK_" + (ts % 10000);

        // Attempting to inject "role": "ADMIN" in registration payload
        String maliciousPayload = "{"
                + "\"name\":\"Hacker User\","
                + "\"collegeRollNumber\":\"" + roll + "\","
                + "\"email\":\"" + email + "\","
                + "\"password\":\"Password123!\","
                + "\"role\":\"ADMIN\""
                + "}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(maliciousPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role", is("STUDENT"))); // Must strictly remain STUDENT

        User savedUser = userRepository.findByEmailIgnoreCase(email).orElse(null);
        assertNotNull(savedUser);
        assertFalse(savedUser.getRole() == Role.ADMIN, "User role must never be elevated to ADMIN via public registration");
    }
}

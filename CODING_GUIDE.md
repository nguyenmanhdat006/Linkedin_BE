# Coding Guide - Workflow cho Feature Development (Spring Boot)

Hướng dẫn chi tiết workflow code khi implement một feature mới trong **Spring Boot** theo chuẩn backend hiện đại (RESTful API), dễ maintain, dễ scale, phù hợp cho project học tập lẫn production.

---

## 📋 Mục Lục

1. Tổng Quan Workflow
2. Kiến Trúc Tổng Thể
3. Chi Tiết Từng Bước
4. Ví Dụ Cụ Thể: User Management Feature
5. Best Practices
6. Checklist

---

## 🎯 Tổng Quan Workflow

Khi implement **một feature mới**, hãy tuân thủ thứ tự sau:

```
1. Entity (JPA Entity)
   ↓
2. Repository (Data Access Layer)
   ↓
3. DTO (Request / Response)
   ↓
4. Mapper (Entity ↔ DTO)
   ↓
5. Service Interface
   ↓
6. Service Implementation (Business Logic)
   ↓
7. Exception & Validation
   ↓
8. Controller (REST API)
   ↓
9. Security (Optional)
   ↓
10. Test (Unit / Integration)
```

---

## 🏗 Kiến Trúc Tổng Thể (Recommended)

```
src/main/java/com/example/app
│
├── common/            # utils, constants, base classes
├── config/            # config (security, swagger, cors)
├── exception/         # global exception handling
├── security/          # auth, jwt, filter
│
├── user/              # feature-based package
│   ├── controller/
│   ├── service/
│   │   ├── UserService.java
│   │   └── impl/
│   ├── repository/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── mapper/
│   ├── entity/
│   └── UserModule.java (optional)
│
└── Application.java
```

👉 **Rule:** Mỗi feature = 1 package riêng (KHÔNG chia theo layer toàn cục).

---

## 📝 CHI TIẾT TỪNG BƯỚC

---

### BƯỚC 1: Entity (JPA)

📍 `user/entity/User.java`

```java
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

⚠️ Không dùng Entity làm request/response

---

### BƯỚC 2: Repository

📍 `user/repository/UserRepository.java`

```java
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
```

---

### BƯỚC 3: DTO (Request / Response)

📍 `user/dto/request/CreateUserRequest.java`

```java
@Data
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotNull
    private Role role;
}
```

📍 `user/dto/response/UserResponse.java`

```java
@Data
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private Status status;
}
```

---

### BƯỚC 4: Mapper

📍 `user/mapper/UserMapper.java`

```java
@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .status(Status.ACTIVE)
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
```

---

### BƯỚC 5: Service Interface

📍 `user/service/UserService.java`

```java
public interface UserService {

    UserResponse create(CreateUserRequest request);

    List<UserResponse> getAll();

    UserResponse getById(UUID id);

    void delete(UUID id);
}
```

---

### BƯỚC 6: Service Implementation

📍 `user/service/impl/UserServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
```

---

### BƯỚC 7: Exception Handling

📍 `exception/GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ex.getMessage()));
    }
}
```

---

### BƯỚC 8: Controller

📍 `user/controller/UserController.java`

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
```

---

## ✅ Best Practices

* Không expose Entity ra ngoài
* Controller KHÔNG chứa business logic
* Service KHÔNG return Entity
* Validate input bằng `@Valid`
* Dùng constructor injection
* Package theo feature, không theo layer

---

## 📋 Checklist

* [ ] Entity
* [ ] Repository
* [ ] DTO request/response
* [ ] Mapper
* [ ] Service + Impl
* [ ] Controller
* [ ] Validation
* [ ] Exception
* [ ] Test

---

## 🎯 KẾT LUẬN

Workflow này **map 1–1** với guide React của bạn:

* React feature ↔ Spring Boot feature
* Thunks ↔ Service layer
* Slice ↔ Business logic

👉 Phù hợp cho **Backend Engineer / Fullstack** muốn code sạch, lâu dài.

---

# 🚀 ADVANCED VERSION: Spring Boot + JWT + Security + React Contract

Phiên bản **nâng cao – production-ready**, dùng cho backend kết nối trực tiếp với **React (Redux Toolkit / Thunks)**.

---

## 🏗 KIẾN TRÚC BẢO MẬT TỔNG THỂ

```
Client (React)
  ↓  JWT (Authorization: Bearer <token>)
Spring Security Filter Chain
  ↓
JWT Authentication Filter
  ↓
Security Context
  ↓
Controller → Service → Repository
```

---

## 🔐 1. AUTH + JWT WORKFLOW (MATCH REACT)

### React Flow

```
Login Form
 → POST /api/auth/login
 → accessToken + refreshToken
 → save token (memory / redux)
 → attach token vào Authorization header
```

### Spring Boot Flow

```
AuthController
 → AuthService
 → AuthenticationManager
 → JWT Provider
 → return AuthResponse
```

---

## 🔐 2. AUTH ENTITY & DTO

### Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled = true;
}
```

---

### DTO – React ↔ Spring Contract

```java
@Data
public class LoginRequest {
    private String email;
    private String password;
}
```

```java
@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
}
```

👉 **React thunk EXPECT đúng shape này**

```ts
interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}
```

---

## 🔐 3. JWT PROVIDER

```java
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public UUID getUserId(String token) {
        return UUID.fromString(Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }
}
```

---

## 🔐 4. JWT FILTER

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            UUID userId = jwtProvider.getUserId(token);

            User user = userRepository.findById(userId)
                    .orElseThrow();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
```

---

## 🔐 5. SPRING SECURITY CONFIG

```java
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

## 🔐 6. AUTH CONTROLLER

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
```

---

## 🔄 7. REACT ↔ SPRING API CONTRACT (QUAN TRỌNG)

### ✅ Quy ước response chung

```json
{
  "data": {},
  "message": "success"
}
```

### Java Wrapper

```java
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
}
```

### Controller

```java
@GetMapping
public ApiResponse<List<UserResponse>> getAll() {
    return new ApiResponse<>(userService.getAll(), "success");
}
```

### React Service

```ts
const res = await api.get<ApiResponse<User[]>>('/users');
return res.data.data;
```

👉 **Backend shape = Frontend type** (KHÔNG suy đoán)

---

## 🛡 ROLE & PERMISSION (MATCH REACT ROUTE GUARD)

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public void delete(@PathVariable UUID id) {}
```

```ts
if (!user.roles.includes('ADMIN')) return <Navigate />
```

---

## ✅ BEST PRACTICES (ADVANCED)

* Không parse JWT trong Controller
* Không lưu JWT trong localStorage (ưu tiên memory)
* JWT chỉ chứa ID + ROLE
* Mọi API protected đều require token
* Response shape luôn ổn định

---

## 🧠 TƯ DUY FULLSTACK

> Backend KHÔNG đoán frontend cần gì
> Frontend KHÔNG đoán backend trả gì

➡ **Contract là chân lý**

---

## 🎯 KẾT LUẬN

Guide này kết hợp:

* Spring Boot chuẩn enterprise
* Security + JWT thực chiến
* Contract khớp 100% React Redux Thunk

👉 Dùng được cho **CV / công ty / startup / đồ án lớn**

---

# 🧾 BỔ SUNG ENTERPRISE CORE (BẮT BUỘC)

## 1️⃣ Chuẩn hóa API RESPONSE CONTRACT (100%)

### ❗ QUY TẮC BẮT BUỘC

* Controller **chỉ return `ApiResponse<T>`**
* Không return trực tiếp DTO / List

### ApiResponse (chuẩn dùng toàn hệ thống)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .message("success")
                .build();
    }
}
```

### Controller (BẮT BUỘC)

```java
@GetMapping
public ApiResponse<List<UserResponse>> getAll() {
    return ApiResponse.success(userService.getAll());
}
```

---

## 2️⃣ Chuẩn hóa ERROR CONTRACT

### ApiError – lỗi nghiệp vụ

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiError {
    private String code;
    private String message;
}
```

### ValidationError – lỗi validate form

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationError {
    private String code;
    private Map<String, String> errors;
}
```

### GlobalExceptionHandler – FULL

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(new ValidationError("VALIDATION_ERROR", errors));
    }
}
```

---

## 🔐 3️⃣ HARDEN JWT FILTER (NO 500)

### ❌ VẤN ĐỀ

* Token sai / hết hạn → 500 (KHÔNG CHẤP NHẬN)

### ✅ JWT FILTER CHUẨN

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                UUID userId = jwtProvider.getUserId(token);

                userRepository.findById(userId).ifPresent(user -> {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
```

---

## 🧪 4️⃣ TEST MẪU (BẮT BUỘC)

### Unit Test – Service

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void create_user_success() {
        CreateUserRequest req = new CreateUserRequest("John", "a@mail.com", Role.USER);
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        User user = new User();
        when(userMapper.toEntity(req)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder().build());

        UserResponse res = userService.create(req);
        assertNotNull(res);
    }
}
```

### Integration Test – Controller

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void unauthorized_without_token() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }
}
```

---

## 📜 CONTRACT RULE (DÁN CUỐI GUIDE)

```text
1. Controller chỉ return ApiResponse<T>
2. Error chỉ return ApiError | ValidationError
3. DTO = Contract (không Entity)
4. Không đổi field nếu chưa version
5. JWT lỗi không được gây 500
6. Test tối thiểu: Service + Controller
```

---


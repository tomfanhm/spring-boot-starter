# Spring Boot Starter

A ready-to-run Spring Boot starter project providing user authentication features (registration, email verification, login with JWT & refresh tokens, password reset) and Docker Compose setup.

---

## 🚀 Features

- User Registration with email verification
- JWT Authentication (access & refresh tokens)
- Login attempt limiting
- Forgot/reset password via email
- PostgreSQL
- Mail integration (SMTP)
- Docker Compose for app & DB

---

## 🔧 Getting Started

1.  Clone the repo

```bash
git clone https://github.com/tomfanhm/spring-boot-starter.git
cd spring-boot-starter
```

2. Configure environment

Create a .env (or set env vars) matching docker-compose.yml:

```yml
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/your_db_name
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password

SPRING_JPA_HIBERNATE_DDL_AUTO=update

# JWT
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=3600000
JWT_REFRESHEXPIRATION=86400000

# Mail (SMTP)
SPRING_MAIL_HOST=smtp.yourmailprovider.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@example.com
SPRING_MAIL_PASSWORD=your_email_password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

# App URL (used in verification/reset links)
APP_VERIFICATION_BASE_URL=http://localhost:8080
```

3. Start with Docker Compose

```bash
docker-compose up --build
```

⸻

## 📚 API Endpoints

| Endpoint                | Method | Description               | Body / Query Params                          |
| ----------------------- | ------ | ------------------------- | -------------------------------------------- |
| **Register**            | POST   | Create new user           | `{ "username","email","password" }`          |
| **Verify Email**        | GET    | Confirm user email        | `?token=<verifyToken>`                       |
| **Login**               | POST   | Authenticate & get tokens | `{ "email","password" }`                     |
| **Refresh Token**       | POST   | Rotate access token       | `{ "refresh_token": "<refreshToken>" }`      |
| **Forgot Password**     | POST   | Request reset link        | `{ "email" }`                                |
| **Reset Password**      | POST   | Set new password          | `{ "token","new_password" }`                 |
| **Protected endpoints** | —      | Any other APIs require    | `Authorization: Bearer <accessToken>` header |

---

## 📄 License

[MIT](https://github.com/tomfanhm/spring-boot-starter/blob/main/LICENSE)

# 🔒 Modern JWT Authentication Platform

> Enterprise-grade authentication solution built with cutting-edge Spring Boot & Bootstrap technologies

## 🌟 Core Features
- 🔑 Secure JWT-based Authentication & Authorization
- 🔄 RESTful API Architecture with OpenAPI Documentation
- 📱 Modern Responsive UI with Bootstrap 5
- 👥 Advanced Role-based Access Control (RBAC)
- 🎯 Smart Token Management & Refresh Mechanism
- 🛡️ Cross-Site Request Forgery (CSRF) Protection

## 💻 Technology Stack
- ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) Spring Boot 3.x
- ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=spring-security&logoColor=white)
- ![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=flat&logo=bootstrap&logoColor=white) v5.3+
- ![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=json-web-tokens&logoColor=white)
- ![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven&logoColor=white)
- ![MySQL](https://img.shields.io/badge/MySQL-005C84?style=flat&logo=mysql&logoColor=white) / ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)

## ⚡ Quick Setup
```bash
# Clone repository
git clone https://github.com/sujankc31/JWTAuthentication.git

# Navigate to project
cd JWTAuthentication

# Configure database
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit application.properties with your database credentials

# Launch application
./mvnw spring-boot:run
```

## 🔌 API Reference
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Create new user account |
| POST | `/api/auth/login` | Authenticate user & get token |
| GET | `/api/auth/user` | Retrieve user profile |
| POST | `/api/auth/refresh` | Refresh access token |

## 🤝 Contributing Guidelines
1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License
Released under MIT License. See `LICENSE` for more information.

## 📊 Project Status
![Status](https://img.shields.io/badge/Status-Active-success)
![Version](https://img.shields.io/badge/Version-2.0.0-blue)
![Security](https://img.shields.io/badge/Security-Enterprise-green)


# 🍽️ Food Ordering API

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?logo=spring)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)
![Security](https://img.shields.io/badge/Security-JWT%20%7C%20Spring%20Security-red?logo=springsecurity)
![Last Commit](https://img.shields.io/github/last-commit/RodriLang/tennis-tournaments-API)
![License](https://img.shields.io/github/license/RodriLang/tennis-tournaments-API)

API RESTful desarrollada con **Spring Boot** para gestionar pedidos de comida en un entorno de restaurante digital. Soporta múltiples roles de usuario, gestión de productos, mesas, sesiones, órdenes, pagos, usuarios, etiquetas, etc.

---

## 🚀 Características Principales

- Gestión completa de usuarios (Staff, Clientes, Invitados)
- Control de productos y etiquetas
- Administración de mesas y sesiones activas
- Creación y seguimiento de órdenes con detalles
- Procesamiento y control de pagos
- Autenticación con JWT y control de acceso por roles
- Soporte para filtros y consultas dinámicas

---

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Hibernate Validator**
- **PostgreSQL** (o el motor que estés usando)
- **Lombok**
- **Swagger/OpenAPI**
- **MapStruct** (si estás usando para mapeo DTO)

---

## 📁 Estructura del Proyecto

```plaintext
com.example.food_ordering
│
├── configs # Configuración global (Swagger, CORS, etc.)
├── context # Contexto de cada FoodVenue
├── controllers # Controladores REST
├── dtos # Data Transfer Objects (Request / Response)
├── enums # Enumerados
├── exceptions # Manejadores de errores
├── mappers # Mapeo de Models a DTOs
├── models # Modelos y Entidades
├── repositories # Interfaces de acceso a datos
├── security # Seguridad JWT, filtros y configuración
├── services # Lógica de negocio (interfaces e implementaciones)
├── strategies # Patron de Diseño Strategy (Confirmación de pedidos)
├── utils # Clases de utilidad
```
---

## ⚙️ Instalación y Ejecución

1. **Clonar el repositorio:**

git clone https://github.com/tu-usuario/food-ordering-API.git
cd food-ordering-api

    Configurar la base de datos:

Modifica el archivo application.properties con tus credenciales de base de datos:

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/food_ordering

Crea las Variables de entorno:

    DB_USER: usuario de la base de datos
    DB_PASSWORD: contraseña de la base de datos
    JWT_KEY: clave para la validación del token HS512
    JWT_EXPIRATION: tiempo de expración en ms

---

    Ejecutar la aplicación:

./mvnw spring-boot:run

    Acceder a Swagger UI:

http://localhost:8080/swagger-ui/index.html

🔐 Autenticación y Roles

    El sistema utiliza JWT para autenticación.

    Acceso a endpoints basado en roles: ADMIN, STAFF, CLIENT, INVITED, SUPER_ADMIN, ROOT.

    Algunos endpoints son públicos (login), el resto requiere token.

📌 Endpoints Principales (Resumen)
🔐 AuthController
Método	Endpoint	Descripción
POST	/auth/login	Login con usuario y contraseña
👤 UserController
Método	Endpoint	Descripción
GET	/users	Obtener todos los usuarios
POST	/users	Crear nuevo usuario
PUT	/users/{id}	Actualizar usuario
DELETE	/users/{id}	Eliminar usuario
🧾 OrderController / OrderDetailController
Método	Endpoint	Descripción
GET	/orders	Obtener todas las órdenes
POST	/orders	Crear nueva orden
POST	/orders/{orderId}/details	Agregar detalle a una orden
GET	/orders/{orderId}/details	Obtener detalles de una orden
DELETE	/orders/{orderId}/details/{detailId}	Eliminar detalle de una orden
🍽️ ProductController
Método	Endpoint	Descripción
GET	/products	Obtener todos los productos
GET	/products/available	Obtener productos disponibles
POST	/products	Crear nuevo producto
🪑 TableController
Método	Endpoint	Descripción
GET	/tables	Listar mesas
GET	/tables/filter	Filtrar mesas por estado/capacidad
POST	/tables	Crear nueva mesa
🧾 PaymentController
Método	Endpoint	Descripción
GET	/payments	Listar pagos
POST	/payments	Crear nuevo pago
PATCH	/payments/{id}/cancel	Cancelar un pago
PATCH	/payments/{id}/complete	Completar un pago
🧑‍🤝‍🧑 TableSessionController
Método	Endpoint	Descripción
GET	/table-sessions	Todas las sesiones
POST	/table-sessions	Crear sesión de mesa
GET	/table-sessions/active	Obtener sesiones activas
PUT	/table-sessions/{id}/clients/{clientId}	Agregar cliente a una sesión
🏷️ TagController
Método	Endpoint	Descripción
GET	/tags	Obtener todas las tags
POST	/tags	Crear una nueva tag
🧪 Ejemplo de Login

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

📒 Swagger / OpenAPI

Swagger está habilitado para inspeccionar y probar los endpoints de forma visual.

http://localhost:8080/swagger-ui/index.html

🧩 Seguridad

    Los endpoints están protegidos con @PreAuthorize.

    Cada controlador restringe el acceso por rol de manera clara.

    JWT se valida en cada request vía filtro.

📌 Notas Adicionales

    El sistema permite a clientes e invitados crear órdenes vinculadas a sesiones de mesa.

    Cada sesión de mesa puede tener múltiples órdenes y participantes.

    Los pagos pueden estar en estado COMPLETED, CANCELLED, etc.

📝 Licencia

Este proyecto está bajo licencia MIT. Puedes modificarlo y adaptarlo según tus necesidades.

📫 Contacto

Desarrollado por: Rodrigo Lang - Ezequiel Santalla - Facundo Aguilera - Yago Sosa
Email: rodrigolang90@gmail.com
GitHub: https://github.com/RodriLang

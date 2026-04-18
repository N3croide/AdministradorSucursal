Este README está diseñado para destacar tanto la arquitectura técnica como el dominio de negocio de tu prueba para **Accenture**. Está estructurado para ser profesional y fácil de leer por un reclutador técnico.

---

# Franchise Manager API - Accenture Technical Challenge

Este proyecto es una API REST reactiva desarrollada para la gestión integral de franquicias, sucursales y productos. La solución permite administrar la jerarquía de una franquicia, controlando el inventario de productos por sucursal y consultando estadísticas de stock.

## 🚀 Tecnologías Utilizadas

* **Java 21**: Uso de las últimas funcionalidades del lenguaje para un código más limpio y eficiente.
* **Spring Boot 3.x**: Framework base para la construcción de la microarquitectura.
* **Spring WebFlux**: Programación reactiva para el manejo de flujos de datos asíncronos y no bloqueantes.
* **Spring Data R2DBC**: Conectividad reactiva a bases de datos relacionales, eliminando el bloqueo de hilos en la capa de persistencia.
* **MySQL**: Motor de base de datos relacional.
* **Lombok**: Biblioteca para reducir el código boilerplate (Getters, Setters, Constructores).
* **Docker & Docker Compose**: Contenerización de la aplicación y la base de datos para asegurar un entorno de ejecución consistente.
* **Render & Aiven**: Despliegue en la nube para el servicio y la base de datos gestionada.

---

## 🏗️ Arquitectura del Proyecto

La aplicación sigue los principios de la **Arquitectura Limpia (Clean Architecture)**, separando las responsabilidades en capas:

1.  **Capa de Controladores (Web)**: Endpoints REST que gestionan las peticiones de entrada y salida.
2.  **Capa de Aplicación (DTOs)**: Objetos de transferencia de datos para desacoplar el modelo interno de la API pública.
3.  **Capa de Servicio**: Contiene la lógica de negocio y la orquestación de llamadas a repositorios.
4.  **Capa de Persistencia**: Repositorios reactivos para la comunicación con la base de datos.

---

## 🛠️ Endpoints Principales

La URL base para el entorno desplegado es: `https://administradorsucursal.onrender.com/api`

### 🏢 Franquicias (`/franchise`)
* `POST /save`: Registra una nueva franquicia.
* `GET /getAll`: Lista todas las franquicias registradas.
* `PUT /update/{id}`: Actualiza el nombre de una franquicia existente.

### 📍 Sucursales (`/branch`)
* `POST /save`: Crea una sucursal asociada a una franquicia.
* `PUT /update/{id}`: Modifica los datos de una sucursal.
* `GET /getAll`: Obtiene el listado completo de sucursales.

### 📦 Productos e Inventario (`/product` & `/branch`)
* `POST /product/save`: Registra un producto en el catálogo general.
* `PUT /branch/addProduct`: Vincula un producto a una sucursal específica con stock inicial.
* `DELETE /branch/deleteProduct/{branchId}/{productId}`: Elimina un producto de una sucursal.
* `PUT /branch/updateStock`: Actualiza la cantidad disponible de un producto en sucursal.
* `GET /product/popular`: **Funcionalidad Especial** - Identifica el producto con mayor stock por cada sucursal de una franquicia.

---

## ⚙️ Configuración Local

### Requisitos Previos
* Docker y Docker Compose instalados.
* Java 21 (si se desea ejecutar sin Docker).

### Ejecución con Docker
1.  Clonar el repositorio.
2.  Configurar las variables de entorno en un archivo `.env` en la raíz:
    ```env
    MYSQL_ROOT_PASSWORD=root
    MYSQL_DATABASE=franchisedb
    SPRING_R2DBC_URL=r2dbc:mysql://db:3306/franchisedb
    SPRING_R2DBC_USERNAME=root
    SPRING_R2DBC_PASSWORD=root
    ```
3.  Ejecutar el comando:
    ```bash
    docker-compose up --build
    ```
4.  La API estará disponible en `http://localhost:8080/api`.

---

## 🧪 Pruebas Rápidas (cURL)

**Crear una Franquicia:**
```bash
curl -X POST http://localhost:8080/api/franchise/save \
     -H "Content-Type: application/json" \
     -d '{"name": "Accenture Franchise"}'
```

**Consultar Productos Populares:**
```bash
curl -X GET http://localhost:8080/api/product/popular
```



## 🗄️ Estructura de la Base de Datos



### Detalle de las Tablas

1.  **`franchise`**: Almacena la información principal de la franquicia.
    * `id` (BIGINT, PK): Identificador único.
    * `name` (VARCHAR): Nombre de la franquicia.

2.  **`branch`**: Representa las diferentes sedes pertenecientes a una franquicia.
    * `id` (BIGINT, PK): Identificador único.
    * `name` (VARCHAR): Nombre de la sucursal.
    * `franchise_id` (BIGINT, FK): Relación con la tabla `franchise`.

3.  **`product`**: Catálogo maestro de productos disponibles en el sistema.
    * `id` (BIGINT, PK): Identificador único.
    * `name` (VARCHAR): Nombre del producto.

4.  **`branch_product` (Tabla Intermedia / Inventario)**: Gestiona el stock específico de cada producto en cada sucursal.
    * `branch_id` (BIGINT, PK, FK): Referencia a la sucursal.
    * `product_id` (BIGINT, PK, FK): Referencia al producto.
    * `stock` (INT): Cantidad actual disponible en esa sede específica.

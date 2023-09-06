API de Suma con Porcentaje
==========================

Este es un proyecto de API REST desarrollado en Spring Boot utilizando Java 17.
La API ofrece una funcionalidad para sumar dos números y aplicar un porcentaje "adquirido de un servicio externo" (en realidad devuelve un 40%).
También guarda un historial de todas las llamadas a los endpoints junto con las respuestas exitosas.
El historial se almacena en una base de datos PostgreSQL.
Además, la API tiene manejo de errores HTTP y cumple con un límite máximo de 3 RPM (request por minuto).

Requerimientos
--------------

Asegúrate de tener instalados los siguientes requisitos antes de comenzar:

*   Docker
*   Docker Compose

Instrucciones de Uso
--------------------

Sigue estos pasos para iniciar la aplicación:

1.  Clona este repositorio:

    `git clone https://github.com/cri-st/challenge-sum.git && cd challenge-sum`

2.  Construye la imagen Docker de la aplicación:

    `docker build -t sum-app .`

3.  Inicia la aplicación junto con la base de datos PostgreSQL utilizando Docker Compose:

    `docker-compose up`


Una vez que la aplicación esté en funcionamiento, puedes acceder a ella en `http://localhost:8080/`.
A continuación, encontrarás información sobre los endpoints y cómo probar la API.

Endpoints
---------

### Sumar con Porcentaje

#### `GET /calculate`

Este endpoint permite sumar dos números y aplicar un porcentaje adquirido de un servicio externo.

**Parámetros de solicitud:**

*   `a` (número): El primer número.
*   `b` (número): El segundo número.

**Ejemplo de solicitud:**

`/calculate?a=5&b=5`

**Respuesta exitosa:**


`14.0`

### Historial de Llamadas

#### `GET /call-history?page=1&pageSize=10`

Este endpoint devuelve el historial de todas las llamadas a los endpoints junto con las respuestas exitosas. La respuesta se presenta en formato JSON con datos paginados.

**Parámetros de solicitud:**

*   `page` (número, opcional): El número de página (por defecto: 1).
*   `pageSize` (número, opcional): El tamaño de página (por defecto: 10).

**Ejemplo de solicitud:**

`/call-history?page=1&pageSize=10`

**Respuesta exitosa:**

`{
    "content": [
        {
            "id": 1,
            "timestamp": [
                2023,
                9,
                6,
                4,
                24,
                50,
                195105000
            ],
            "endpoint": "/calculate",
            "statusCode": 200,
            "response": "14.0"
        },
        {
            "id": 2,
            "timestamp": [
                2023,
                9,
                6,
                4,
                24,
                54,
                932166000
            ],
            "endpoint": "/calculate",
            "statusCode": 200,
            "response": "7.0"
        }
    ],
    "pageable": {
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 2,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 2,
    "empty": false
}`

### Límite de RPM

La API tiene un límite máximo de 3 RPM (requests por minuto). Si se supera este umbral, se devolverá un error con el código HTTP 429 con su mensaje de descripcion.

Documentación Adicional
-----------------------

Puedes encontrar la documentación completa de la API en el Swagger, el mismo se accede desde: http://localhost:8080/swagger-ui/

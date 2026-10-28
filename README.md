# Denunciaservice — API REST

API para registrar, consultar y atender denuncias ciudadanas. Proyecto académico con cinco operaciones REST y persistencia en MySQL.

## Tecnologías y requisitos

- Java 21, Spring Boot 4.1.2 y Gradle Wrapper 9.8.2.
- Spring Web MVC, Spring Data JPA, Hibernate, Lombok y MySQL Connector/J.
- OpenAPI/Swagger UI mediante springdoc 3.2.0.
- MySQL disponible y un usuario con permisos sobre `denunciasdb`.

## Instalación y configuración

Clona este repositorio y abre una terminal en su carpeta. Configura las variables de entorno en PowerShell:

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '3306'
$env:DB_NAME = 'denunciasdb'
$env:DB_USER = 'tu_usuario_mysql'
$env:DB_PASSWORD = Read-Host 'Contraseña MySQL'
```

La contraseña permanece en la sesión local; no la guardes en archivos versionados. `application.properties` ya lee estas variables. Los valores predeterminados son localhost, puerto 3306, base denunciasdb, usuario root y contraseña vacía. No es necesario usar esos valores en tu instalación.

El archivo `schema.sql` conserva el esquema original y puede ejecutarse manualmente desde MySQL. La configuración existente usa `ddl-auto=update` y permite crear la base si no existe; no se ha cambiado ese comportamiento. No ejecutes pruebas contra una base con datos importantes.

Si Java no está disponible, configura `JAVA_HOME` hacia tu JDK 21. `gradle.properties` se excluye porque contiene una ruta personal del JDK.

## Ejecución

```powershell
.\gradlew.bat bootRun
```

En Linux/macOS: `./gradlew bootRun` (otorga permiso de ejecución al wrapper si hace falta).

- API local: http://localhost:8080/denuncias
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs

## Funcionalidades y endpoints

| Método | Ruta | Función |
| --- | --- | --- |
| POST | `/denuncias` | Registrar una denuncia, inicialmente pendiente (1) |
| GET | `/denuncias?offset=0&limit=5` | Listar con paginación |
| GET | `/denuncias/{id}` | Consultar detalle por ID |
| GET | `/denuncias/ciudadano/{dni}` | Buscar por DNI de ocho dígitos |
| PUT | `/denuncias/{id}` | Marcar como atendida (0) |

El POST y el listado también aceptan barra final. El PUT no necesita cuerpo. Se validan DNI, fecha, tipo, dirección y descripción; se manejan solicitudes inválidas y recursos inexistentes.

Ejemplo de cuerpo para registrar (datos ficticios):

```json
{
  "dni": "00000000",
  "fecha": "2026-10-28T10:00:00",
  "tipo": "Ruido",
  "direccion": "Dirección de ejemplo",
  "descripcion": "Descripción de ejemplo"
}
```

## Pruebas y compilación

```powershell
.\gradlew.bat test
.\gradlew.bat bootJar
```

La prueba de contexto requiere MySQL disponible con la configuración anterior. El JAR se genera en `build/libs/`.

## Estructura

`src/main/java`: controlador, servicio, repositorio, entidad y manejo de errores. `src/main/resources`: configuración. `src/test`: pruebas. `schema.sql`: esquema MySQL original.

## Entrega

La app Flutter complementaria es `denunciasapp`. Este repositorio contiene el código de la API; publicar en GitHub no despliega un servidor. La demostración puede realizarse localmente o con el despliegue descrito abajo. La URL de Render se obtiene al crear el Web Service.

## Despliegue en Render con Docker y PostgreSQL 18

El perfil `render` usa PostgreSQL. Sin ese perfil, se conserva la configuración original de MySQL. No se migran los datos de MySQL; la base cloud tendrá sus propios registros.

1. En Render crea un **Web Service**, conecta este repositorio y selecciona la rama `main`, runtime **Docker**, región **Oregon** y Dockerfile `./Dockerfile` (contexto raíz). Usa la misma región que la base `denunciasdb-cloud`.
2. En **Environment** configura las variables de la tabla. Copia los datos desde la conexión **Internal** de la base PostgreSQL 18 existente. El nombre del recurso `denunciasdb-cloud` puede diferir del nombre real de la base: usa el valor **Database** de Render.
3. Guarda la contraseña únicamente en Render; no la pongas en el Dockerfile, README, comandos de construcción ni repositorio. No se necesita ninguna credencial durante el build.
4. Configura como health check `/v3/api-docs`. Despliega y comprueba `/swagger-ui.html` y los cinco endpoints en la URL HTTPS asignada por Render.

| Variable | Valor que debes configurar en Render |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `render` (también es el valor predeterminado de la imagen) |
| `RENDER_DB_HOST` | Host interno de PostgreSQL, sin protocolo ni credenciales |
| `RENDER_DB_PORT` | Puerto de la base; por defecto `5432` |
| `RENDER_DB_NAME` | Nombre real de la base indicado en Render |
| `RENDER_DB_USER` | Usuario indicado en Render |
| `RENDER_DB_PASSWORD` | Contraseña de la base, guardada como secreto en Render |
| `PORT` | Puerto proporcionado por Render; por defecto `10000` |

La aplicación construye una URL JDBC con TLS (`sslmode=require`). No pegues una URL `postgresql://usuario:contraseña@...` en `RENDER_DB_HOST`. El driver PostgreSQL se resuelve con la gestión de dependencias de Spring Boot.

El Dockerfile compila con JDK 21 y ejecuta con JRE 21 como usuario sin privilegios. No inicia un servidor PostgreSQL dentro del contenedor: usa la base PostgreSQL 18 creada en Render. `.dockerignore` limita el contexto y excluye configuración personal, compilaciones y secretos. `bootJar` empaqueta sin ejecutar las pruebas que necesitan base de datos.

El perfil desactiva los scripts SQL de inicialización: `schema.sql` conserva exclusivamente su propósito local de MySQL. Hibernate crea/actualiza las tablas en PostgreSQL desde las entidades originales. Los cinco endpoints no cambian.

Para construir manualmente, con Docker instalado:

```powershell
docker build -t denunciaservice:render .
```

Para comprobar integración con PostgreSQL 18 antes de desplegar, usa una base de pruebas vacía, configura las cinco variables `RENDER_DB_*` de esa base y ejecuta `./gradlew test --rerun-tasks -Dspring.profiles.active=render` (en Windows, `./gradlew.bat`). Las pruebas existentes crean y actualizan registros: no las ejecutes contra una base con datos que deban conservarse. La conexión de pruebas también debe admitir TLS.

El despliegue y la configuración de secretos en Render son pasos independientes de publicar el código. No hay una URL pública de la API hasta completar el despliegue.

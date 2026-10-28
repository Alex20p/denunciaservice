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

La app Flutter complementaria es `denunciasapp`. Este repositorio contiene el código de la API; publicar en GitHub no despliega un servidor. La demostración puede realizarse localmente. No se ha configurado una URL de Render.

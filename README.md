# MCP Security Demo

This project demonstrates a Spring WebFlux application secured with Spring Security and JWT authentication. It provides a login endpoint to obtain a token and a protected endpoint that requires a valid token for access. It also includes a Server-Sent Events (SSE) endpoint for real-time communication.

## Demo Scenario with Claude Desktop

This server is designed to work with a client like Claude Desktop. The client can connect to the SSE endpoint to receive real-time updates.

### Claude Desktop Configuration

To connect Claude Desktop to this server, use the following MCP Server configuration:

```json
{
    "mcpServers": {
        "default-server": {
            "type": "sse",
            "url": "http://localhost:8080/sse",
            "note": "For SSE connections, add this URL directly in your MCP Client"
        }
    }
}
```

## Running the Application

1.  Start the application using Maven:
    ```bash
    ./mvnw spring-boot:run
    ```

## Authentication

The application uses a hardcoded user for authentication.

-   **Username:** `user@example.com`
-   **Password:** `password`

### 1. Obtain a JWT Token

Send a POST request to the `/login` endpoint with the user's credentials to receive a JWT.

```bash
curl --location 'http://localhost:8080/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "user@example.com",
    "password": "password"
}'
```

The response will contain the JWT token:
```json
{
    "token": "your-jwt-token"
}
```

### 2. Access Protected Endpoints

Include the obtained JWT in the `Authorization` header as a Bearer token to access protected endpoints.

For example, to access the `/ping` endpoint:

```bash
curl --location 'http://localhost:8080/ping' \
--header 'Authorization: Bearer <your-jwt-token>'
```

A successful request will return the string `"pong"`.

### 3. Connect to the SSE Endpoint

The `/sse` endpoint is protected. Your client will need to handle the authentication flow to connect successfully. For testing purposes, you can add a temporary rule to `SecurityConfig.java` to permit all access to the `/sse` endpoint if needed.


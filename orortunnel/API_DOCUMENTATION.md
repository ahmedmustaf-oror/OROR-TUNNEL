# OROR TUNNEL - API Documentation & Backend Reference

Base URL:
`https://elias555.serv00.net/orortunnel/`

All API endpoints accept GET and POST and return standard JSON formatted responses:

```json
{
  "success": true,
  "message": "Operation status description",
  "code": "OK",
  "data": {},
  "timestamp": "2026-08-19 12:00:00"
}
```

---

## Endpoints Summary

### 1. `GET /api/index.php?action=servers`
- **Description**: Returns all active servers configured by admin.
- **Response**: Array of Server objects containing ID, Name, Country, Flag, Host, Port, Protocol, Load, Ping, Premium status.

### 2. `GET /api/index.php?action=configs`
- **Description**: Returns all active remote configurations.
- **Response**: Array of Config objects containing ID, Name, Protocol, Server details, Credentials, Payload, SNI, Custom Headers.

### 3. `GET /api/index.php?action=settings`
- **Description**: Returns app global settings, support phone (`01021520331`), maintenance mode, UI theme rules.

### 4. `GET /api/index.php?action=announcements`
- **Description**: Returns active news and home banners.

### 5. `GET /api/index.php?action=notifications`
- **Description**: Returns system notifications.

### 6. `GET /api/index.php?action=updates`
- **Description**: Returns update rules (`latest_version_code`, `min_version_code`, `download_url`, `force_update`, `changelog`).

### 7. `GET /api/index.php?action=maintenance`
- **Description**: Returns current maintenance state and notice message.

### 8. `POST /api/index.php?action=register`
- **Body JSON**:
```json
{
  "device_id": "unique_device_uuid",
  "app_version": "1.0.0",
  "device_model": "Samsung Galaxy S24",
  "android_version": "14"
}
```
- **Description**: Registers or updates device active status.

### 9. `POST /api/index.php?action=stats`
- **Body JSON**:
```json
{
  "download_bytes": 10485760,
  "upload_bytes": 2097152
}
```
- **Description**: Submits traffic statistics.

---

## Backend Deployment
1. Upload the `orortunnel` folder to `https://elias555.serv00.net/orortunnel/`.
2. Ensure write permissions (`chmod 0755` or `0777`) on `orortunnel/data/` and `orortunnel/logs/`.
3. Default Admin Login:
   - URL: `https://elias555.serv00.net/orortunnel/admin/`
   - Username: `admin`
   - Password: `admin12345`

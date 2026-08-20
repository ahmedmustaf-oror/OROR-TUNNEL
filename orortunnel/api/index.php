<?php
// OROR TUNNEL API Gateway Router
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../includes/json_db.php';
require_once __DIR__ . '/../includes/helper.php';

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Device-Id, X-App-Version');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

$action = sanitizeInput($_GET['action'] ?? $_POST['action'] ?? 'settings');

switch ($action) {
    case 'servers':
        $servers = JsonDB::read('servers.json');
        $activeServers = array_values(array_filter($servers, function($s) {
            return isset($s['status']) && $s['status'] === 'active';
        }));
        sendJsonResponse(true, 'Servers list retrieved', $activeServers);
        break;

    case 'configs':
        $configs = JsonDB::read('configs.json');
        $activeConfigs = array_values(array_filter($configs, function($c) {
            return isset($c['status']) && $c['status'] === 'active';
        }));
        sendJsonResponse(true, 'Remote configs retrieved', $activeConfigs);
        break;

    case 'settings':
        $settings = JsonDB::read('settings.json');
        sendJsonResponse(true, 'App settings retrieved', $settings);
        break;

    case 'announcements':
        $announcements = JsonDB::read('announcements.json');
        $active = array_values(array_filter($announcements, function($a) {
            return !isset($a['active']) || $a['active'] === true;
        }));
        sendJsonResponse(true, 'Announcements retrieved', $active);
        break;

    case 'notifications':
        $notifications = JsonDB::read('notifications.json');
        sendJsonResponse(true, 'Notifications retrieved', $notifications);
        break;

    case 'updates':
        $updates = JsonDB::read('updates.json');
        sendJsonResponse(true, 'Update info retrieved', $updates);
        break;

    case 'maintenance':
        $settings = JsonDB::read('settings.json');
        $maintenance = [
            'maintenance_mode' => $settings['maintenance_mode'] ?? false,
            'title' => $settings['maintenance_title'] ?? 'OROR TUNNEL Under Maintenance',
            'message' => $settings['maintenance_message'] ?? 'Please try again later.',
            'allow_version_bypass' => $settings['allow_version_bypass'] ?? []
        ];
        sendJsonResponse(true, 'Maintenance status retrieved', $maintenance);
        break;

    case 'register':
        $input = json_decode(file_get_contents('php://input'), true) ?? $_POST;
        $deviceId = sanitizeInput($input['device_id'] ?? $_SERVER['HTTP_X_DEVICE_ID'] ?? uniqid('dev_'));
        $appVersion = sanitizeInput($input['app_version'] ?? '1.0.0');
        $deviceModel = sanitizeInput($input['device_model'] ?? 'Android Device');
        $androidVersion = sanitizeInput($input['android_version'] ?? 'Android');

        $devices = JsonDB::read('devices.json');
        $device = [
            'id' => $deviceId,
            'app_version' => $appVersion,
            'device_model' => $deviceModel,
            'android_version' => $androidVersion,
            'last_seen' => date('Y-m-d H:i:s'),
            'ip' => $_SERVER['REMOTE_ADDR'] ?? '127.0.0.1'
        ];

        $existing = JsonDB::findById('devices.json', $deviceId);
        if (!$existing) {
            $device['first_seen'] = date('Y-m-d H:i:s');
            $device['status'] = 'active';
            $device['connection_count'] = 0;
        } else {
            $device['first_seen'] = $existing['first_seen'] ?? date('Y-m-d H:i:s');
            $device['status'] = $existing['status'] ?? 'active';
            $device['connection_count'] = ($existing['connection_count'] ?? 0) + 1;
        }

        JsonDB::saveItem('devices.json', $device);
        logEvent('DEVICE_REGISTER', "Device registered: $deviceId ($deviceModel)", $deviceId);

        sendJsonResponse(true, 'Device registered successfully', $device);
        break;

    case 'stats':
        $input = json_decode(file_get_contents('php://input'), true) ?? $_POST;
        $stats = JsonDB::read('stats.json');
        if (isset($input['download_bytes'])) {
            $stats['total_download_bytes'] = ($stats['total_download_bytes'] ?? 0) + (int)$input['download_bytes'];
        }
        if (isset($input['upload_bytes'])) {
            $stats['total_upload_bytes'] = ($stats['total_upload_bytes'] ?? 0) + (int)$input['upload_bytes'];
        }
        $stats['total_connections'] = ($stats['total_connections'] ?? 0) + 1;
        JsonDB::write('stats.json', $stats);
        sendJsonResponse(true, 'Stats updated', $stats);
        break;

    case 'heartbeat':
        $deviceId = sanitizeInput($_SERVER['HTTP_X_DEVICE_ID'] ?? $_GET['device_id'] ?? 'unknown');
        if ($deviceId !== 'unknown') {
            $device = JsonDB::findById('devices.json', $deviceId);
            if ($device) {
                $device['last_seen'] = date('Y-m-d H:i:s');
                JsonDB::saveItem('devices.json', $device);
            }
        }
        sendJsonResponse(true, 'Heartbeat received');
        break;

    default:
        sendJsonResponse(false, 'Invalid action specified', null, 'INVALID_ACTION', 400);
        break;
}

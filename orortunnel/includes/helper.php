<?php
// OROR TUNNEL Helper Utilities
require_once __DIR__ . '/json_db.php';

function sendJsonResponse($success, $message = '', $data = null, $code = 'OK', $httpStatus = 200) {
    http_response_code($httpStatus);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'code' => $code,
        'data' => $data,
        'timestamp' => date('Y-m-d H:i:s')
    ], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit;
}

function logEvent($type, $description, $deviceId = 'SYSTEM', $ip = '') {
    if (empty($ip) && isset($_SERVER['REMOTE_ADDR'])) {
        $ip = $_SERVER['REMOTE_ADDR'];
    }
    $log = [
        'id' => uniqid('log_'),
        'timestamp' => date('Y-m-d H:i:s'),
        'type' => $type,
        'description' => $description,
        'device_id' => $deviceId,
        'ip' => $ip
    ];
    JsonDB::saveItem('logs.json', $log);
}

function sanitizeInput($data) {
    if (is_array($data)) {
        return array_map('sanitizeInput', $data);
    }
    return htmlspecialchars(trim($data), ENT_QUOTES, 'UTF-8');
}

<?php
// OROR TUNNEL Configuration File
define('APP_NAME', 'OROR TUNNEL');
define('APP_VERSION', '1.0.0');
define('BASE_URL', 'https://elias555.serv00.net/orortunnel/');
define('SECRET_KEY', 'oror_tunnel_sec_#01021520331_key_2026');

// Paths
define('DATA_DIR', __DIR__ . '/../data/');
define('LOGS_DIR', __DIR__ . '/../logs/');
define('UPLOADS_DIR', __DIR__ . '/../uploads/');

// Session & Security Settings
define('SESSION_LIFETIME', 86400); // 24 hours
define('LOGIN_MAX_ATTEMPTS', 5);
define('LOGIN_LOCKOUT_TIME', 900); // 15 minutes

// Default timezone
date_default_timezone_set('Africa/Cairo');

// Ensure directories exist
if (!file_exists(DATA_DIR)) { @mkdir(DATA_DIR, 0755, true); }
if (!file_exists(LOGS_DIR)) { @mkdir(LOGS_DIR, 0755, true); }
if (!file_exists(UPLOADS_DIR)) { @mkdir(UPLOADS_DIR, 0755, true); }

<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$dataPath = __DIR__ . '/data/db.json';

// Ensure data folder exists and initialize default dataset
if (!file_exists($dataPath)) {
    if (!is_dir(__DIR__ . '/data')) {
        mkdir(__DIR__ . '/data', 0777, true);
    }
    
    $defaultData = [
        "settings" => [
            "app_name" => "OROR TUNNEL",
            "support_phone" => "01021520331",
            "support_telegram" => "https://t.me/orortunnel",
            "maintenance_mode" => false,
            "maintenance_title" => "OROR TUNNEL تحت الصيانة حاليًا 🛠️",
            "maintenance_message" => "نقوم بتحديث راوترات الاتصال وتطوير الثغرات لتقديم أسرع خدمة. يرجى الانتظار بضع دقائق.",
            "secret_key" => "oror_tunnel_secure_sig_key_2026"
        ],
        "updates" => [
            "latest_version_code" => 100,
            "latest_version_name" => "1.0.0",
            "min_version_code" => 100,
            "download_url" => "https://elias555.serv00.net/orortunnel/",
            "force_update" => false,
            "changelog" => "🚀 الإطلاق الرسمي لـ OROR TUNNEL متجر وسيرفرات الثغرات المتقدمة"
        ],
        "routers" => [
            [
                "id" => "rtr_eg_01",
                "name" => "🇪🇬 Egypt Router 01 - Vodafone / Orange Direct",
                "country" => "مصر",
                "flag" => "🇪🇬",
                "ip" => "104.16.51.1",
                "status" => "online",
                "cpu_usage" => 18,
                "ram_usage" => 32,
                "active_connections" => 142,
                "max_connections" => 2000,
                "ping_ms" => 18,
                "supported_protocols" => ["SSH", "V2RAY", "HYSTERIA2"]
            ],
            [
                "id" => "rtr_de_01",
                "name" => "🇩🇪 Germany High-Speed Router 01",
                "country" => "ألمانيا",
                "flag" => "🇩🇪",
                "ip" => "de1.orortunnel.net",
                "status" => "online",
                "cpu_usage" => 12,
                "ram_usage" => 24,
                "active_connections" => 88,
                "max_connections" => 1500,
                "ping_ms" => 42,
                "supported_protocols" => ["WIREGUARD", "OPENVPN", "DNSTT"]
            ]
        ],
        "configs" => [
            [
                "format" => "oror",
                "version" => 4,
                "id" => "cfg_eg_vf_ssh",
                "name" => "⚡ ثغرة فودافون مصر - HTTP WebSocket Direct",
                "type" => "ssh",
                "protocol" => "SSH",
                "category" => "Vodafone Egypt",
                "country" => "مصر 🇪🇬",
                "flag" => "🇪🇬",
                "router_id" => "rtr_eg_01",
                "router_name" => "🇪🇬 Egypt Router 01",
                "server" => [
                    "host" => "104.16.51.1",
                    "port" => 80
                ],
                "auth" => [
                    "username" => "oror_user",
                    "password" => "oror_pass"
                ],
                "payload" => "GET / HTTP/1.1[crlf]Host: vodafone.com.eg[crlf]Upgrade: websocket[crlf]Connection: Keep-Alive[crlf][crlf]",
                "sni" => "vodafone.com.eg",
                "custom_headers" => "User-Agent: Mozilla/5.0 (Android; OROR Tunnel)",
                "dns_server" => "1.1.1.1",
                "expires_at" => "2028-12-31T23:59:59Z",
                "is_premium" => false,
                "is_vip" => true,
                "ping_ms" => 18,
                "load_percentage" => 14,
                "signature" => md5("cfg_eg_vf_ssh_v4_oror_secret")
            ],
            [
                "format" => "oror",
                "version" => 2,
                "id" => "cfg_eg_orange_ssl",
                "name" => "🚀 ثغرة أورنج مصر - SSL/TLS SNI High Speed",
                "type" => "ssh-ssl",
                "protocol" => "SSH-SSL",
                "category" => "Orange Egypt",
                "country" => "مصر 🇪🇬",
                "flag" => "🇪🇬",
                "router_id" => "rtr_eg_01",
                "router_name" => "🇪🇬 Egypt Router 01",
                "server" => [
                    "host" => "104.16.52.2",
                    "port" => 443
                ],
                "auth" => [
                    "username" => "oror_user",
                    "password" => "oror_pass"
                ],
                "payload" => "",
                "sni" => "m.orange.eg",
                "custom_headers" => "",
                "dns_server" => "8.8.8.8",
                "expires_at" => "2028-12-31T23:59:59Z",
                "is_premium" => false,
                "is_vip" => false,
                "ping_ms" => 22,
                "load_percentage" => 19,
                "signature" => md5("cfg_eg_orange_ssl_v2_oror_secret")
            ],
            [
                "format" => "oror",
                "version" => 1,
                "id" => "cfg_de_dnstt",
                "name" => "🌐 ثغرة SlowDNS - اتصل مجاناً بدون رصيد نهائياً",
                "type" => "dnstt",
                "protocol" => "DNSTT",
                "category" => "SlowDNS Free",
                "country" => "ألمانيا 🇩🇪",
                "flag" => "🇩🇪",
                "router_id" => "rtr_de_01",
                "router_name" => "🇩🇪 Germany High-Speed Router 01",
                "server" => [
                    "host" => "dns.orortunnel.net",
                    "port" => 53
                ],
                "auth" => [
                    "pubkey" => "d094a3721fb31f49e0114f0e",
                    "nameserver" => "ns.orortunnel.net"
                ],
                "payload" => "",
                "sni" => "dns.orortunnel.net",
                "custom_headers" => "",
                "dns_server" => "1.1.1.1",
                "expires_at" => "2028-12-31T23:59:59Z",
                "is_premium" => false,
                "is_vip" => false,
                "ping_ms" => 45,
                "load_percentage" => 9,
                "signature" => md5("cfg_de_dnstt_v1_oror_secret")
            ]
        ],
        "announcements" => [
            [
                "id" => "ann_1",
                "title" => "📢 مرحباً بك في متجر OROR TUNNEL للكونفجات والتأمين!",
                "description" => "يمكنك تصفح متجر الـ Configs وتثبيت أحدث ثغرات فودافون وأورنج وأتصل مجاناً بدون انقطاع.",
                "active" => true
            ]
        ],
        "notifications" => [
            [
                "id" => "notif_1",
                "title" => "تحديث الـ Configs التلقائي 🔄",
                "message" => "تم تحديث سيرفرات فودافون وأورنج إلى الإصدار الأخير بدون إيقاف الخدمة.",
                "created_at" => date("Y-m-d H:i")
            ]
        ]
    ];
    
    file_put_contents($dataPath, json_encode($defaultData, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
}

$db = json_decode(file_get_contents($dataPath), true);
$action = $_GET['action'] ?? $_GET['a'] ?? 'store';

function respond($success, $message, $code = "OK", $data = null) {
    echo json_encode([
        "success" => $success,
        "message" => $message,
        "code" => $code,
        "data" => $data,
        "timestamp" => date("Y-m-d H:i:s")
    ], JSON_UNESCAPED_UNICODE);
    exit();
}

switch ($action) {
    case 'store':
    case 'configs':
        respond(true, "Configs fetched successfully", "OK", $db['configs'] ?? []);
        break;

    case 'routers':
    case 'servers':
        respond(true, "Routers fetched successfully", "OK", $db['routers'] ?? []);
        break;

    case 'config_detail':
        $cfgId = $_GET['id'] ?? '';
        $found = null;
        foreach ($db['configs'] as $c) {
            if ($c['id'] === $cfgId) {
                $found = $c;
                break;
            }
        }
        if ($found) {
            respond(true, "Config found", "OK", $found);
        } else {
            respond(false, "Config not found", "NOT_FOUND");
        }
        break;

    case 'sync':
        respond(true, "Sync state retrieved", "OK", [
            "settings" => $db['settings'] ?? [],
            "routers" => $db['routers'] ?? [],
            "configs" => $db['configs'] ?? [],
            "announcements" => $db['announcements'] ?? [],
            "notifications" => $db['notifications'] ?? [],
            "updates" => $db['updates'] ?? []
        ]);
        break;

    case 'settings':
        respond(true, "Settings fetched successfully", "OK", $db['settings'] ?? []);
        break;

    case 'announcements':
        respond(true, "Announcements fetched successfully", "OK", $db['announcements'] ?? []);
        break;

    case 'notifications':
        respond(true, "Notifications fetched successfully", "OK", $db['notifications'] ?? []);
        break;

    case 'updates':
        respond(true, "Updates fetched successfully", "OK", $db['updates'] ?? []);
        break;

    default:
        respond(true, "OROR TUNNEL Engine API Active", "OK", [
            "app_name" => "OROR TUNNEL",
            "version" => "1.0.0",
            "total_configs" => count($db['configs'] ?? []),
            "total_routers" => count($db['routers'] ?? [])
        ]);
        break;
}

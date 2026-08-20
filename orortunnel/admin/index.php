<?php
// OROR TUNNEL ADMIN PANEL (Mobile-First Responsive Cyber Dark UI)
require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../includes/json_db.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/helper.php';

// Handle Logout
if (isset($_GET['action']) && $_GET['action'] === 'logout') {
    Auth::logout();
    header('Location: index.php');
    exit;
}

// Handle Login POST
$loginError = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['login_submit'])) {
    $username = sanitizeInput($_POST['username'] ?? '');
    $password = $_POST['password'] ?? '';
    if (Auth::login($username, $password)) {
        header('Location: index.php?page=dashboard');
        exit;
    } else {
        $loginError = 'اسم المستخدم أو كلمة المرور غير صحيحة';
    }
}

// Check Admin Auth
if (!Auth::check()) {
    ?>
    <!DOCTYPE html>
    <html lang="ar" dir="rtl">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>OROR TUNNEL - تسجيل الدخول للوحة التحكم</title>
        <style>
            * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
            body { background: #0D0E15; color: #E2E8F0; display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px; }
            .login-card { background: #131520; border: 1px solid #2A2D3D; border-radius: 16px; width: 100%; max-width: 420px; padding: 32px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); }
            .logo { text-align: center; margin-bottom: 24px; }
            .logo h1 { color: #9D4EDD; font-size: 28px; font-weight: 800; letter-spacing: 1px; }
            .logo p { color: #00F5D4; font-size: 14px; margin-top: 4px; }
            .form-group { margin-bottom: 20px; }
            label { display: block; margin-bottom: 8px; font-size: 14px; color: #94A3B8; }
            input[type="text"], input[type="password"] { width: 100%; padding: 12px 16px; background: #1A1D2C; border: 1px solid #2A2D3D; border-radius: 8px; color: #FFF; font-size: 15px; outline: none; }
            input:focus { border-color: #9D4EDD; box-shadow: 0 0 8px rgba(157, 78, 221, 0.3); }
            .btn-submit { width: 100%; padding: 14px; background: linear-gradient(135deg, #7B2CBF, #9D4EDD); border: none; border-radius: 8px; color: #FFF; font-size: 16px; font-weight: 700; cursor: pointer; transition: 0.3s; }
            .btn-submit:hover { opacity: 0.9; transform: translateY(-2px); }
            .error-banner { background: rgba(239, 68, 68, 0.15); border: 1px solid #EF4444; color: #FCA5A5; padding: 12px; border-radius: 8px; font-size: 14px; margin-bottom: 20px; text-align: center; }
            .phone-badge { text-align: center; margin-top: 20px; font-size: 12px; color: #64748B; }
        </style>
    </head>
    <body>
        <div class="login-card">
            <div class="logo">
                <h1>OROR TUNNEL</h1>
                <p>لوحة التحكم الرئيسية Admin</p>
            </div>
            <?php if (!empty($loginError)): ?>
                <div class="error-banner"><?php echo $loginError; ?></div>
            <?php endif; ?>
            <form method="POST" action="index.php">
                <div class="form-group">
                    <label>اسم المستخدم</label>
                    <input type="text" name="username" required placeholder="admin">
                </div>
                <div class="form-group">
                    <label>كلمة المرور</label>
                    <input type="password" name="password" required placeholder="••••••••">
                </div>
                <button type="submit" name="login_submit" class="btn-submit">تسجيل الدخول</button>
            </form>
            <div class="phone-badge">OROR #01021520331</div>
        </div>
    </body>
    </html>
    <?php
    exit;
}

// Admin Action Processors
$message = '';
$page = sanitizeInput($_GET['page'] ?? 'dashboard');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Action: Save Server
    if (isset($_POST['save_server'])) {
        $serverData = [
            'id' => sanitizeInput($_POST['id'] ?? ''),
            'name' => sanitizeInput($_POST['name'] ?? ''),
            'country' => sanitizeInput($_POST['country'] ?? ''),
            'flag' => sanitizeInput($_POST['flag'] ?? '🌐'),
            'host' => sanitizeInput($_POST['host'] ?? ''),
            'port' => (int)($_POST['port'] ?? 22),
            'protocol' => sanitizeInput($_POST['protocol'] ?? 'SSH'),
            'status' => sanitizeInput($_POST['status'] ?? 'active'),
            'max_users' => (int)($_POST['max_users'] ?? 500),
            'current_users' => (int)($_POST['current_users'] ?? 0),
            'load' => (int)($_POST['load'] ?? 10),
            'ping' => (int)($_POST['ping'] ?? 30),
            'premium' => isset($_POST['premium']) && $_POST['premium'] == '1',
            'priority' => (int)($_POST['priority'] ?? 1)
        ];
        JsonDB::saveItem('servers.json', $serverData);
        logEvent('SERVER_SAVE', 'Saved server: ' . $serverData['name']);
        $message = 'تم حفظ الخادم بنجاح';
    }

    // Action: Delete Server
    if (isset($_POST['delete_server'])) {
        $serverId = sanitizeInput($_POST['server_id']);
        JsonDB::deleteById('servers.json', $serverId);
        logEvent('SERVER_DELETE', 'Deleted server ID: ' . $serverId);
        $message = 'تم حذف الخادم بنجاح';
    }

    // Action: Save Config
    if (isset($_POST['save_config'])) {
        $configData = [
            'id' => sanitizeInput($_POST['id'] ?? ''),
            'name' => sanitizeInput($_POST['name'] ?? ''),
            'protocol' => sanitizeInput($_POST['protocol'] ?? 'SSH'),
            'server_id' => sanitizeInput($_POST['server_id'] ?? ''),
            'host' => sanitizeInput($_POST['host'] ?? ''),
            'port' => (int)($_POST['port'] ?? 22),
            'username' => sanitizeInput($_POST['username'] ?? ''),
            'password' => sanitizeInput($_POST['password'] ?? ''),
            'payload' => sanitizeInput($_POST['payload'] ?? ''),
            'sni' => sanitizeInput($_POST['sni'] ?? ''),
            'custom_headers' => sanitizeInput($_POST['custom_headers'] ?? ''),
            'expiration' => sanitizeInput($_POST['expiration'] ?? '2028-12-31'),
            'status' => sanitizeInput($_POST['status'] ?? 'active'),
            'premium' => isset($_POST['premium']) && $_POST['premium'] == '1',
            'category' => sanitizeInput($_POST['category'] ?? 'General')
        ];
        JsonDB::saveItem('configs.json', $configData);
        logEvent('CONFIG_SAVE', 'Saved config: ' . $configData['name']);
        $message = 'تم حفظ الإعدادات بنجاح';
    }

    // Action: Delete Config
    if (isset($_POST['delete_config'])) {
        $configId = sanitizeInput($_POST['config_id']);
        JsonDB::deleteById('configs.json', $configId);
        logEvent('CONFIG_DELETE', 'Deleted config ID: ' . $configId);
        $message = 'تم حذف الإعداد بنجاح';
    }

    // Action: Update Settings
    if (isset($_POST['save_settings'])) {
        $settingsData = [
            'app_name' => sanitizeInput($_POST['app_name'] ?? 'OROR TUNNEL'),
            'support_phone' => sanitizeInput($_POST['support_phone'] ?? '01021520331'),
            'telegram_channel' => sanitizeInput($_POST['telegram_channel'] ?? ''),
            'website_url' => sanitizeInput($_POST['website_url'] ?? ''),
            'support_email' => sanitizeInput($_POST['support_email'] ?? ''),
            'api_timeout_ms' => (int)($_POST['api_timeout_ms'] ?? 10000),
            'auto_reconnect' => isset($_POST['auto_reconnect']),
            'maintenance_mode' => isset($_POST['maintenance_mode']),
            'maintenance_title' => sanitizeInput($_POST['maintenance_title'] ?? ''),
            'maintenance_message' => sanitizeInput($_POST['maintenance_message'] ?? ''),
            'primary_color' => sanitizeInput($_POST['primary_color'] ?? '#9D4EDD'),
            'secondary_color' => sanitizeInput($_POST['secondary_color'] ?? '#00F5D4')
        ];
        JsonDB::write('settings.json', $settingsData);
        logEvent('SETTINGS_UPDATE', 'App settings updated');
        $message = 'تم تحديث إعدادات التطبيق العامة بنجاح';
    }

    // Action: Save App Update
    if (isset($_POST['save_update_info'])) {
        $updateData = [
            'latest_version_code' => (int)($_POST['latest_version_code'] ?? 1),
            'latest_version_name' => sanitizeInput($_POST['latest_version_name'] ?? '1.0.0'),
            'min_version_code' => (int)($_POST['min_version_code'] ?? 1),
            'download_url' => sanitizeInput($_POST['download_url'] ?? ''),
            'force_update' => isset($_POST['force_update']),
            'changelog' => sanitizeInput($_POST['changelog'] ?? ''),
            'release_date' => date('Y-m-d')
        ];
        JsonDB::write('updates.json', $updateData);
        logEvent('APP_UPDATE_SET', 'Updated app version release rules');
        $message = 'تم حفظ بيانات التحديث والإجبار على التحديث بنجاح';
    }

    // Action: Create Notification
    if (isset($_POST['save_notification'])) {
        $notifData = [
            'id' => uniqid('notif_'),
            'title' => sanitizeInput($_POST['title']),
            'message' => sanitizeInput($_POST['message']),
            'type' => sanitizeInput($_POST['type'] ?? 'INFO'),
            'priority' => sanitizeInput($_POST['priority'] ?? 'NORMAL'),
            'created_at' => date('Y-m-d H:i:s')
        ];
        JsonDB::saveItem('notifications.json', $notifData);
        logEvent('NOTIF_SEND', 'Created notification: ' . $notifData['title']);
        $message = 'تم إرسال/إنشاء الإشعار بنجاح';
    }

    // Action: Create Announcement
    if (isset($_POST['save_announcement'])) {
        $ancData = [
            'id' => uniqid('anc_'),
            'title' => sanitizeInput($_POST['title']),
            'description' => sanitizeInput($_POST['description']),
            'link' => sanitizeInput($_POST['link'] ?? ''),
            'active' => true,
            'created_at' => date('Y-m-d H:i:s')
        ];
        JsonDB::saveItem('announcements.json', $ancData);
        logEvent('ANNOUNCEMENT_SAVE', 'Created announcement: ' . $ancData['title']);
        $message = 'تم نشر الإعلان بنجاح';
    }

    // Action: Restore Backup
    if (isset($_POST['restore_backup']) && isset($_FILES['backup_file'])) {
        $uploaded = $_FILES['backup_file']['tmp_name'];
        if (file_exists($uploaded)) {
            $jsonContent = file_get_contents($uploaded);
            $backupData = json_decode($jsonContent, true);
            if (is_array($backupData)) {
                foreach ($backupData as $fileName => $fileContent) {
                    if (str_ends_with($fileName, '.json') && is_array($fileContent)) {
                        JsonDB::write($fileName, $fileContent);
                    }
                }
                logEvent('BACKUP_RESTORE', 'Backup restored successfully');
                $message = 'تم استرجاع النسخة الاحتياطية بنجاح!';
            } else {
                $message = 'ملف النسخة الاحتياطية غير صالح';
            }
        }
    }
}

// Download Backup Handler
if (isset($_GET['action']) && $_GET['action'] === 'download_backup') {
    $files = ['servers.json', 'configs.json', 'settings.json', 'announcements.json', 'notifications.json', 'updates.json', 'devices.json', 'stats.json', 'users.json'];
    $backup = [];
    foreach ($files as $f) {
        $backup[$f] = JsonDB::read($f);
    }
    header('Content-Type: application/json');
    header('Content-Disposition: attachment; filename="oror_tunnel_backup_' . date('Y-m-d_H-i') . '.json"');
    echo json_encode($backup, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit;
}

// Fetch stats & data
$servers = JsonDB::read('servers.json');
$configs = JsonDB::read('configs.json');
$devices = JsonDB::read('devices.json');
$logs = JsonDB::read('logs.json');
$settings = JsonDB::read('settings.json');
$updates = JsonDB::read('updates.json');
$announcements = JsonDB::read('announcements.json');
$notifications = JsonDB::read('notifications.json');
$stats = JsonDB::read('stats.json');
?>
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OROR TUNNEL - Dashboard Admin</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', system-ui, -apple-system, sans-serif; }
        body { background: #0D0E15; color: #E2E8F0; min-height: 100vh; display: flex; flex-direction: column; }
        
        /* Layout */
        header { background: #131520; border-bottom: 1px solid #2A2D3D; padding: 16px 24px; display: flex; align-items: center; justify-content: space-between; sticky: top; top: 0; z-index: 100; }
        .brand { display: flex; align-items: center; gap: 12px; }
        .brand-title { color: #9D4EDD; font-size: 22px; font-weight: 800; }
        .brand-subtitle { font-size: 12px; color: #00F5D4; background: rgba(0,245,212,0.1); padding: 4px 8px; border-radius: 6px; }
        .user-nav { display: flex; align-items: center; gap: 16px; }
        .btn-logout { background: rgba(239, 68, 68, 0.2); color: #FCA5A5; border: 1px solid #EF4444; padding: 8px 14px; border-radius: 8px; text-decoration: none; font-size: 13px; font-weight: 600; }
        
        .admin-wrapper { display: flex; flex: 1; overflow: hidden; }
        aside { width: 250px; background: #131520; border-left: 1px solid #2A2D3D; padding: 20px 10px; display: flex; flex-direction: column; gap: 6px; }
        aside a { color: #94A3B8; text-decoration: none; padding: 12px 16px; border-radius: 10px; font-size: 14px; font-weight: 600; display: flex; align-items: center; gap: 10px; transition: 0.2s; }
        aside a:hover, aside a.active { background: rgba(157, 78, 221, 0.15); color: #9D4EDD; border-right: 3px solid #9D4EDD; }
        
        main { flex: 1; padding: 24px; overflow-y: auto; background: #0D0E15; }
        
        /* Cards & Components */
        .page-header { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; }
        .page-title { font-size: 24px; font-weight: 700; color: #FFF; }
        .alert-msg { background: rgba(16, 185, 129, 0.2); border: 1px solid #10B981; color: #6EE7B7; padding: 12px 16px; border-radius: 10px; margin-bottom: 20px; font-size: 14px; }
        
        .grid-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin-bottom: 24px; }
        .stat-card { background: #131520; border: 1px solid #2A2D3D; border-radius: 14px; padding: 20px; }
        .stat-card .num { font-size: 32px; font-weight: 800; color: #00F5D4; margin-top: 8px; }
        .stat-card .label { font-size: 13px; color: #94A3B8; }
        
        .card-block { background: #131520; border: 1px solid #2A2D3D; border-radius: 14px; padding: 20px; margin-bottom: 24px; }
        .card-title { font-size: 18px; font-weight: 700; color: #FFF; margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
        
        table { width: 100%; border-collapse: collapse; text-align: right; margin-top: 10px; }
        th, td { padding: 12px; border-bottom: 1px solid #2A2D3D; font-size: 14px; }
        th { color: #94A3B8; font-weight: 600; background: #1A1D2C; }
        tr:hover { background: rgba(255,255,255,0.02); }
        
        .form-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 16px; }
        label { display: block; font-size: 13px; color: #94A3B8; margin-bottom: 6px; }
        input[type="text"], input[type="number"], select, textarea { width: 100%; padding: 10px 14px; background: #1A1D2C; border: 1px solid #2A2D3D; border-radius: 8px; color: #FFF; font-size: 14px; outline: none; }
        textarea { height: 100px; resize: vertical; }
        .btn-action { padding: 10px 18px; background: #7B2CBF; border: none; border-radius: 8px; color: #FFF; font-weight: 700; cursor: pointer; transition: 0.2s; }
        .btn-action:hover { background: #9D4EDD; }
        .btn-danger { background: #DC2626; }
        .btn-danger:hover { background: #EF4444; }
        .badge { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 700; }
        .badge-active { background: rgba(16, 185, 129, 0.2); color: #6EE7B7; }
        .badge-premium { background: rgba(245, 158, 11, 0.2); color: #FBBF24; }
        
        @media (max-width: 768px) {
            .admin-wrapper { flex-direction: column; }
            aside { width: 100%; border-left: none; border-bottom: 1px solid #2A2D3D; flex-direction: row; overflow-x: auto; padding: 10px; }
            aside a { white-space: nowrap; }
        }
    </style>
</head>
<body>

<header>
    <div class="brand">
        <div class="brand-title">OROR TUNNEL</div>
        <div class="brand-subtitle">ADMIN PANEL</div>
    </div>
    <div class="user-nav">
        <span style="font-size: 14px; color: #94A3B8;">مرحباً، <?php echo htmlspecialchars(Auth::user()['name'] ?? 'Admin'); ?></span>
        <a href="index.php?action=logout" class="btn-logout">تسجيل الخروج</a>
    </div>
</header>

<div class="admin-wrapper">
    <aside>
        <a href="index.php?page=dashboard" class="<?php echo $page === 'dashboard' ? 'active' : ''; ?>">📊 لوحة التحكم</a>
        <a href="index.php?page=servers" class="<?php echo $page === 'servers' ? 'active' : ''; ?>">🖥️ السيرفرات</a>
        <a href="index.php?page=configs" class="<?php echo $page === 'configs' ? 'active' : ''; ?>">⚙️ التكوينات (Configs)</a>
        <a href="index.php?page=users" class="<?php echo $page === 'users' ? 'active' : ''; ?>">📱 المستخدمين والأجهزة</a>
        <a href="index.php?page=updates" class="<?php echo $page === 'updates' ? 'active' : ''; ?>">🔄 تحديثات التطبيق</a>
        <a href="index.php?page=notifications" class="<?php echo $page === 'notifications' ? 'active' : ''; ?>">🔔 الإشعارات</a>
        <a href="index.php?page=announcements" class="<?php echo $page === 'announcements' ? 'active' : ''; ?>">📢 الإعلانات</a>
        <a href="index.php?page=logs" class="<?php echo $page === 'logs' ? 'active' : ''; ?>">📜 السجلات (Logs)</a>
        <a href="index.php?page=settings" class="<?php echo $page === 'settings' ? 'active' : ''; ?>">🛠️ إعدادات النظام</a>
        <a href="index.php?action=download_backup" style="color: #00F5D4;">💾 تحميل نسخة احتياطية</a>
    </aside>

    <main>
        <?php if (!empty($message)): ?>
            <div class="alert-msg"><?php echo $message; ?></div>
        <?php endif; ?>

        <?php if ($page === 'dashboard'): ?>
            <div class="page-header">
                <div class="page-title">نظرة عامة على الإحصائيات</div>
                <div>رقم الهوية: OROR 01021520331</div>
            </div>

            <div class="grid-stats">
                <div class="stat-card">
                    <div class="label">إجمالي السيرفرات</div>
                    <div class="num"><?php echo count($servers); ?></div>
                </div>
                <div class="stat-card">
                    <div class="label">إجمالي الـ Configs</div>
                    <div class="num"><?php echo count($configs); ?></div>
                </div>
                <div class="stat-card">
                    <div class="label">الأجهزة المسجلة</div>
                    <div class="num"><?php echo count($devices); ?></div>
                </div>
                <div class="stat-card">
                    <div class="label">إجمالي الاتصالات</div>
                    <div class="num"><?php echo $stats['total_connections'] ?? 0; ?></div>
                </div>
            </div>

            <div class="card-block">
                <div class="card-title">حالة النظام و الصيانة</div>
                <p style="font-size: 14px; color: #94A3B8; margin-bottom: 12px;">
                    وضع الصيانة: <strong style="color: <?php echo ($settings['maintenance_mode'] ?? false) ? '#EF4444' : '#10B981'; ?>;">
                        <?php echo ($settings['maintenance_mode'] ?? false) ? 'مفعل (المستخدمين معطلين)' : 'يعمل بشكل طبيعي'; ?>
                    </strong>
                </p>
                <form method="POST">
                    <input type="hidden" name="app_name" value="<?php echo htmlspecialchars($settings['app_name'] ?? ''); ?>">
                    <input type="hidden" name="support_phone" value="<?php echo htmlspecialchars($settings['support_phone'] ?? ''); ?>">
                    <label style="display: inline-flex; align-items: center; gap: 8px; cursor: pointer; font-size: 15px;">
                        <input type="checkbox" name="maintenance_mode" value="1" <?php echo ($settings['maintenance_mode'] ?? false) ? 'checked' : ''; ?>>
                        تفعيل وضع الصيانة الفوري (Maintenance Mode)
                    </label>
                    <div style="margin-top: 12px;">
                        <button type="submit" name="save_settings" class="btn-action">حفظ التغيير</button>
                    </div>
                </form>
            </div>

        <?php elseif ($page === 'servers'): ?>
            <div class="page-header">
                <div class="page-title">إدارة السيرفرات الخادمة</div>
            </div>

            <div class="card-block">
                <div class="card-title">إضافة / تعديل سيرفر</div>
                <form method="POST">
                    <div class="form-row">
                        <div>
                            <label>معرف السيرفر (تلقائي إن كان جديد)</label>
                            <input type="text" name="id" placeholder="srv_01">
                        </div>
                        <div>
                            <label>اسم السيرفر</label>
                            <input type="text" name="name" required placeholder="OROR Egypt VIP">
                        </div>
                        <div>
                            <label>الدولة</label>
                            <input type="text" name="country" required placeholder="Egypt">
                        </div>
                        <div>
                            <label>العلم (Emoji Flag)</label>
                            <input type="text" name="flag" required placeholder="🇪🇬">
                        </div>
                    </div>
                    <div class="form-row">
                        <div>
                            <label>الـ Host / IP</label>
                            <input type="text" name="host" required placeholder="eg.orortunnel.net">
                        </div>
                        <div>
                            <label>المنفذ Port</label>
                            <input type="number" name="port" value="22">
                        </div>
                        <div>
                            <label>البروتوكول Protocol</label>
                            <select name="protocol">
                                <option value="SSH">SSH</option>
                                <option value="WireGuard">WireGuard</option>
                                <option value="OpenVPN">OpenVPN</option>
                                <option value="V2Ray">V2Ray / Xray</option>
                                <option value="Hysteria2">Hysteria2</option>
                                <option value="DNSTT">DNSTT SlowDNS</option>
                                <option value="HTTP Proxy">HTTP Proxy</option>
                                <option value="SOCKS Proxy">SOCKS Proxy</option>
                            </select>
                        </div>
                        <div>
                            <label>نوع السيرفر</label>
                            <select name="premium">
                                <option value="0">مجاني Free</option>
                                <option value="1">VIP Premium</option>
                            </select>
                        </div>
                    </div>
                    <button type="submit" name="save_server" class="btn-action">حفظ السيرفر</button>
                </form>
            </div>

            <div class="card-block">
                <div class="card-title">قائمة السيرفرات الحالية</div>
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>العلم</th>
                                <th>الاسم</th>
                                <th>البروتوكول</th>
                                <th>Host:Port</th>
                                <th>النوع</th>
                                <th>الـ Ping</th>
                                <th>إجراءات</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($servers as $srv): ?>
                                <tr>
                                    <td><?php echo $srv['flag']; ?></td>
                                    <td><strong><?php echo htmlspecialchars($srv['name']); ?></strong></td>
                                    <td><?php echo $srv['protocol']; ?></td>
                                    <td><?php echo htmlspecialchars($srv['host']) . ':' . $srv['port']; ?></td>
                                    <td>
                                        <span class="badge <?php echo ($srv['premium'] ?? false) ? 'badge-premium' : 'badge-active'; ?>">
                                            <?php echo ($srv['premium'] ?? false) ? 'VIP' : 'مجاني'; ?>
                                        </span>
                                    </td>
                                    <td><?php echo $srv['ping'] ?? 30; ?> ms</td>
                                    <td>
                                        <form method="POST" style="display:inline;" onsubmit="return confirm('هل أنت تأكد من حذف السيرفر؟');">
                                            <input type="hidden" name="server_id" value="<?php echo $srv['id']; ?>">
                                            <button type="submit" name="delete_server" class="btn-action btn-danger" style="padding:4px 10px; font-size:12px;">حذف</button>
                                        </form>
                                    </td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            </div>

        <?php elseif ($page === 'configs'): ?>
            <div class="page-header">
                <div class="page-title">إدارة إعدادات الـ Configs وتخصيص الـ Header/Payload</div>
            </div>

            <div class="card-block">
                <div class="card-title">إنشاء / تعديل Config جديد</div>
                <form method="POST">
                    <div class="form-row">
                        <div>
                            <label>اسم الـ Config</label>
                            <input type="text" name="name" required placeholder="OROR Fast SSH Direct">
                        </div>
                        <div>
                            <label>البروتوكول</label>
                            <select name="protocol">
                                <option value="SSH">SSH</option>
                                <option value="WireGuard">WireGuard</option>
                                <option value="OpenVPN">OpenVPN</option>
                                <option value="V2Ray">V2Ray / Xray</option>
                                <option value="Hysteria2">Hysteria2</option>
                                <option value="DNSTT">DNSTT</option>
                            </select>
                        </div>
                        <div>
                            <label>الـ Host</label>
                            <input type="text" name="host" placeholder="eg1.orortunnel.net">
                        </div>
                        <div>
                            <label>الـ Port</label>
                            <input type="number" name="port" value="22">
                        </div>
                    </div>
                    <div class="form-row">
                        <div>
                            <label>اسم المستخدم (إن وجد)</label>
                            <input type="text" name="username" placeholder="user_oror">
                        </div>
                        <div>
                            <label>كلمة المرور (إن وجدت)</label>
                            <input type="text" name="password" placeholder="pass_oror">
                        </div>
                        <div>
                            <label>الـ SNI (TLS Server Name Indication)</label>
                            <input type="text" name="sni" placeholder="cdn.domain.com">
                        </div>
                        <div>
                            <label>تاريخ انتهاء الصلاحية</label>
                            <input type="text" name="expiration" value="2028-12-31">
                        </div>
                    </div>
                    <div class="form-row">
                        <div style="grid-column: 1 / -1;">
                            <label>الـ Custom Payload (HTTP Header Injection)</label>
                            <textarea name="payload" placeholder="GET / HTTP/1.1[crlf]Host: domain.com[crlf]Upgrade: websocket[crlf][crlf]"></textarea>
                        </div>
                    </div>
                    <button type="submit" name="save_config" class="btn-action">حفظ التكوين (Save Config)</button>
                </form>
            </div>

            <div class="card-block">
                <div class="card-title">التكوينات الحالية المخزنة</div>
                <table>
                    <thead>
                        <tr>
                            <th>الاسم</th>
                            <th>البروتوكول</th>
                            <th>Host:Port</th>
                            <th>SNI</th>
                            <th>تاريخ الانتهاء</th>
                            <th>إجراءات</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($configs as $cfg): ?>
                            <tr>
                                <td><strong><?php echo htmlspecialchars($cfg['name']); ?></strong></td>
                                <td><?php echo $cfg['protocol']; ?></td>
                                <td><?php echo htmlspecialchars($cfg['host']) . ':' . $cfg['port']; ?></td>
                                <td><?php echo htmlspecialchars($cfg['sni'] ?? '-'); ?></td>
                                <td><?php echo $cfg['expiration']; ?></td>
                                <td>
                                    <form method="POST" style="display:inline;" onsubmit="return confirm('حذف هذا التكوين؟');">
                                        <input type="hidden" name="config_id" value="<?php echo $cfg['id']; ?>">
                                        <button type="submit" name="delete_config" class="btn-action btn-danger" style="padding:4px 10px; font-size:12px;">حذف</button>
                                    </form>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>

        <?php elseif ($page === 'updates'): ?>
            <div class="page-header">
                <div class="page-title">إدارة تحديثات التطبيق والإجبار (Force Update)</div>
            </div>
            <div class="card-block">
                <form method="POST">
                    <div class="form-row">
                        <div>
                            <label>أحدث كود إصدار (Version Code)</label>
                            <input type="number" name="latest_version_code" value="<?php echo $updates['latest_version_code'] ?? 1; ?>">
                        </div>
                        <div>
                            <label>اسم الإصدار (Version Name)</label>
                            <input type="text" name="latest_version_name" value="<?php echo htmlspecialchars($updates['latest_version_name'] ?? '1.0.0'); ?>">
                        </div>
                        <div>
                            <label>أقل إصدار مسموح به (Min Version Code)</label>
                            <input type="number" name="min_version_code" value="<?php echo $updates['min_version_code'] ?? 1; ?>">
                        </div>
                    </div>
                    <div class="form-row">
                        <div style="grid-column: 1 / -1;">
                            <label>رابط تحميل التحديث (APK Download URL)</label>
                            <input type="text" name="download_url" value="<?php echo htmlspecialchars($updates['download_url'] ?? ''); ?>">
                        </div>
                    </div>
                    <div class="form-row">
                        <div style="grid-column: 1 / -1;">
                            <label>ملاحظات الإصدار (Changelog)</label>
                            <textarea name="changelog"><?php echo htmlspecialchars($updates['changelog'] ?? ''); ?></textarea>
                        </div>
                    </div>
                    <div style="margin-bottom: 16px;">
                        <label style="display:inline-flex; align-items:center; gap:8px; cursor:pointer;">
                            <input type="checkbox" name="force_update" value="1" <?php echo ($updates['force_update'] ?? false) ? 'checked' : ''; ?>>
                            تفعيل الإجبار على التحديث (منع فتح التطبيق بدون تحديث)
                        </label>
                    </div>
                    <button type="submit" name="save_update_info" class="btn-action">حفظ قاعدة التحديث</button>
                </form>
            </div>

        <?php elseif ($page === 'users'): ?>
            <div class="page-header">
                <div class="page-title">الأجهزة والمستخدمين النشطين</div>
            </div>
            <div class="card-block">
                <table>
                    <thead>
                        <tr>
                            <th>Device ID</th>
                            <th>الموديل</th>
                            <th>إصدار التطبيق</th>
                            <th>أول ظهور</th>
                            <th>آخر ظهور</th>
                            <th>الـ IP</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($devices as $dev): ?>
                            <tr>
                                <td><code><?php echo htmlspecialchars($dev['id']); ?></code></td>
                                <td><?php echo htmlspecialchars($dev['device_model'] ?? 'Android'); ?></td>
                                <td><?php echo htmlspecialchars($dev['app_version'] ?? '1.0.0'); ?></td>
                                <td><?php echo $dev['first_seen'] ?? '-'; ?></td>
                                <td><?php echo $dev['last_seen'] ?? '-'; ?></td>
                                <td><?php echo $dev['ip'] ?? '-'; ?></td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>

        <?php elseif ($page === 'logs'): ?>
            <div class="page-header">
                <div class="page-title">سجلات الأحداث والأمان (Logs)</div>
            </div>
            <div class="card-block">
                <table>
                    <thead>
                        <tr>
                            <th>الوقت</th>
                            <th>نوع الحدث</th>
                            <th>الوصف</th>
                            <th>المعرف/الـ IP</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach (array_reverse($logs) as $l): ?>
                            <tr>
                                <td><?php echo $l['timestamp']; ?></td>
                                <td><span class="badge badge-active"><?php echo $l['type']; ?></span></td>
                                <td><?php echo htmlspecialchars($l['description']); ?></td>
                                <td><?php echo htmlspecialchars(($l['device_id'] ?? '') . ' / ' . ($l['ip'] ?? '')); ?></td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>

        <?php elseif ($page === 'settings'): ?>
            <div class="page-header">
                <div class="page-title">إعدادات النظام العامة واستراد البيانات</div>
            </div>
            <div class="card-block">
                <form method="POST">
                    <div class="form-row">
                        <div>
                            <label>اسم التطبيق</label>
                            <input type="text" name="app_name" value="<?php echo htmlspecialchars($settings['app_name'] ?? 'OROR TUNNEL'); ?>">
                        </div>
                        <div>
                            <label>رقم الدعم (الهاتف)</label>
                            <input type="text" name="support_phone" value="<?php echo htmlspecialchars($settings['support_phone'] ?? '01021520331'); ?>">
                        </div>
                        <div>
                            <label>قناة التليجرام</label>
                            <input type="text" name="telegram_channel" value="<?php echo htmlspecialchars($settings['telegram_channel'] ?? ''); ?>">
                        </div>
                    </div>
                    <div class="form-row">
                        <div>
                            <label>عنوان الصيانة (Title)</label>
                            <input type="text" name="maintenance_title" value="<?php echo htmlspecialchars($settings['maintenance_title'] ?? ''); ?>">
                        </div>
                    </div>
                    <div class="form-row">
                        <div style="grid-column: 1 / -1;">
                            <label>رسالة الصيانة للمستخدمين</label>
                            <textarea name="maintenance_message"><?php echo htmlspecialchars($settings['maintenance_message'] ?? ''); ?></textarea>
                        </div>
                    </div>
                    <button type="submit" name="save_settings" class="btn-action">حفظ إعدادات النظام</button>
                </form>
            </div>

            <div class="card-block">
                <div class="card-title">استرجاع نسخة احتياطية (Restore JSON Backup)</div>
                <form method="POST" enctype="multipart/form-data">
                    <div style="margin-bottom: 12px;">
                        <input type="file" name="backup_file" required accept=".json">
                    </div>
                    <button type="submit" name="restore_backup" class="btn-action btn-danger">استرجاع البيانات الآن</button>
                </form>
            </div>
        <?php endif; ?>
    </main>
</div>

</body>
</html>

<?php
// ==========================================
// OROR TUNNEL - Admin Panel (No Login - Blue Theme)
// ==========================================
error_reporting(E_ALL & ~E_WARNING & ~E_NOTICE);
ini_set('display_errors', 0);

$dataPath = __DIR__ . '/data/db.json';
$uploadsDir = __DIR__ . '/uploads/';
if (!is_dir($uploadsDir)) {
    mkdir($uploadsDir, 0777, true);
}
if (!file_exists($dataPath)) {
    @file_get_contents('http://' . $_SERVER['HTTP_HOST'] . dirname($_SERVER['PHP_SELF']) . '/index.php');
}
$db = json_decode(file_get_contents($dataPath), true) ?: ['settings' => [], 'routers' => [], 'configs' => [], 'announcements' => [], 'notifications' => [], 'updates' => []];
$message = "";

// --- Handle Actions ---
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // 1. Save Router
    if (isset($_POST['add_router'])) {
        $newRouter = [
            "id" => "rtr_" . time(),
            "name" => $_POST['rtr_name'],
            "country" => $_POST['rtr_country'],
            "flag" => $_POST['rtr_flag'] ?: '🌐',
            "ip" => $_POST['rtr_ip'],
            "status" => $_POST['rtr_status'] ?: 'online',
            "cpu_usage" => rand(10, 30),
            "ram_usage" => rand(20, 40),
            "active_connections" => 0,
            "max_connections" => (int)($_POST['rtr_max'] ?: 1000),
            "ping_ms" => (int)($_POST['rtr_ping'] ?: 25),
            "supported_protocols" => explode(',', $_POST['rtr_protocols'] ?: 'SSH,WIREGUARD,V2RAY,SOCKS5')
        ];
        $db['routers'][] = $newRouter;
        file_put_contents($dataPath, json_encode($db, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
        $message = "تم حفظ السيرفر بنجاح!";
    }
    // Delete Router
    if (isset($_POST['delete_router_id'])) {
        $delId = $_POST['delete_router_id'];
        $db['routers'] = array_values(array_filter($db['routers'], fn($r) => $r['id'] !== $delId));
        file_put_contents($dataPath, json_encode($db, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
        $message = "تم حذف السيرفر بنجاح!";
    }
    // 2. Add Config
    if (isset($_POST['add_config'])) {
        $cfgId = "cfg_" . time();
        $customFileUrl = "";
        if (isset($_FILES['cfg_file']) && $_FILES['cfg_file']['error'] === UPLOAD_ERR_OK) {
            $fileName = time() . '_' . preg_replace("/[^a-zA-Z0-9\._-]/", "_", $_FILES['cfg_file']['name']);
            if (move_uploaded_file($_FILES['cfg_file']['tmp_name'], $uploadsDir . $fileName)) {
                $customFileUrl = "https://" . $_SERVER['HTTP_HOST'] . dirname($_SERVER['PHP_SELF']) . "/uploads/" . $fileName;
            }
        }
        $logoUrl = $_POST['cfg_logo_url'] ?? '';
        if (isset($_FILES['cfg_logo_file']) && $_FILES['cfg_logo_file']['error'] === UPLOAD_ERR_OK) {
            $logoName = "logo_" . time() . '_' . preg_replace("/[^a-zA-Z0-9\._-]/", "_", $_FILES['cfg_logo_file']['name']);
            if (move_uploaded_file($_FILES['cfg_logo_file']['tmp_name'], $uploadsDir . $logoName)) {
                $logoUrl = "https://" . $_SERVER['HTTP_HOST'] . dirname($_SERVER['PHP_SELF']) . "/uploads/" . $logoName;
            }
        }
        $newCfg = [
            "id" => $cfgId,
            "name" => $_POST['cfg_name'] ?? 'OROR Config',
            "protocol" => strtoupper($_POST['cfg_protocol'] ?? 'SSH'),
            "category" => $_POST['cfg_category'] ?? 'General',
            "flag" => $_POST['cfg_flag'] ?? '🌐',
            "icon" => $_POST['cfg_icon'] ?? '⚡',
            "logo_url" => $logoUrl,
            "file_url" => $customFileUrl,
            "host" => $_POST['cfg_host'] ?? '',
            "port" => (int)($_POST['cfg_port'] ?? 80),
            "username" => $_POST['cfg_username'] ?? '',
            "password" => $_POST['cfg_password'] ?? '',
            "server" => [
                "host" => $_POST['cfg_host'] ?? '',
                "port" => (int)($_POST['cfg_port'] ?? 80)
            ],
            "auth" => [
                "username" => $_POST['cfg_username'] ?? '',
                "password" => $_POST['cfg_password'] ?? ''
            ],
            "payload" => $_POST['cfg_payload'] ?? '',
            "sni" => $_POST['cfg_sni'] ?? '',
            "socks_ip" => $_POST['cfg_socks_ip'] ?? '',
            "socks_port" => (int)(trim($_POST['cfg_socks_port'] ?? '') ?: 0),
            "status" => "active",
            "is_premium" => false
        ];
        $db['configs'][] = $newCfg;
        file_put_contents($dataPath, json_encode($db, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
        $message = "تم حفظ الكونفج بنجاح!";
    }
    // Delete Config
    if (isset($_POST['delete_config_id'])) {
        $delId = $_POST['delete_config_id'];
        $db['configs'] = array_values(array_filter($db['configs'], fn($c) => $c['id'] !== $delId));
        file_put_contents($dataPath, json_encode($db, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
        $message = "تم حذف الكونفج بنجاح!";
    }
    // 3. Notifications
    if (isset($_POST['add_notification'])) {
        $db['notifications'][] = [
            "id" => "notif_" . time(),
            "title" => $_POST['notif_title'],
            "message" => $_POST['notif_msg'],
            "date" => date('Y-m-d H:i:s')
        ];
        file_put_contents($dataPath, json_encode($db, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
        $message = "تم إرسال الإشعار!";
    }
}
?>
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <title>لوحة تحكم OROR TUNNEL</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f7f6; color: #333; font-family: 'Segoe UI', Tahoma, sans-serif; }
        .navbar { background: linear-gradient(90deg, #0d6efd 0%, #00b4d8 100%); box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .navbar-brand { color: #fff !important; font-weight: bold; }
        .card { background-color: #fff; border: none; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); margin-bottom: 20px; }
        .form-control, .form-select { border-radius: 8px; border: 1px solid #ced4da; }
        .form-control:focus, .form-select:focus { border-color: #0d6efd; box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25); }
        .btn-primary { background-color: #0d6efd; border: none; border-radius: 8px; }
        .btn-primary:hover { background-color: #0b5ed7; }
        .text-accent { color: #0d6efd !important; }
        .table { background-color: #fff; }
        .table th { background-color: #f8f9fa; color: #495057; border-bottom: 2px solid #dee2e6; }
        .table td { vertical-align: middle; }
        .nav-tabs .nav-link { color: #6c757d; font-weight: 500; border: none; padding: 12px 20px; }
        .nav-tabs .nav-link.active { color: #0d6efd; background-color: transparent; border-bottom: 3px solid #0d6efd; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg mb-4 p-3">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="#">
            <i class="bi bi-shield-lock-fill me-2 fs-3"></i> OROR TUNNEL ADMIN
        </a>
    </div>
</nav>

<div class="container">
    <?php if($message): ?>
        <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> <?= $message ?>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <?php endif; ?>

    <ul class="nav nav-tabs mb-4" id="adminTabs" role="tablist">
        <li class="nav-item">
            <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#tab-configs" type="button"><i class="bi bi-sliders"></i> متجر الكونفجات (<?= count($db['configs'] ?? []) ?>)</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tab-routers" type="button"><i class="bi bi-hdd-network"></i> السيرفرات (<?= count($db['routers'] ?? []) ?>)</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tab-notif" type="button"><i class="bi bi-bell-fill"></i> الإشعارات</button>
        </li>
    </ul>

    <div class="tab-content" id="adminTabsContent">
        <!-- CONFIGS TAB -->
        <div class="tab-pane fade show active" id="tab-configs">
            <div class="row">
                <div class="col-md-5">
                    <div class="card p-4">
                        <h5 class="text-accent mb-3"><i class="bi bi-plus-circle-fill"></i> إضافة Config / ملف كاستم</h5>
                        <form method="POST" enctype="multipart/form-data">
                            <input type="hidden" name="add_config" value="1">
                            <div class="mb-3">
                                <label class="form-label small">اسم الكونفج (يظهر للعميل)</label>
                                <input type="text" name="cfg_name" class="form-control" placeholder="فودافون VIP" required>
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-6">
                                    <label class="form-label small">البروتوكول</label>
                                    <select name="cfg_protocol" class="form-select">
                                        <option value="SSH">SSH</option>
                                        <option value="CUSTOM_FILE">ملف كاستم جاهز</option>
                                        <option value="V2RAY">V2Ray</option>
                                    </select>
                                </div>
                                <div class="col-6">
                                    <label class="form-label small">العلم (Emoji)</label>
                                    <input type="text" name="cfg_flag" class="form-control" value="🇪🇬">
                                </div>
                            </div>
                            <!-- FILES -->
                            <div class="mb-3 p-3 rounded bg-light border">
                                <label class="form-label small text-primary fw-bold"><i class="bi bi-file-earmark-arrow-up"></i> رفع ملف كونفج جاهز (.oror, .ehi)</label>
                                <input type="file" name="cfg_file" class="form-control form-control-sm mb-2">
                                <label class="form-label small text-primary fw-bold"><i class="bi bi-image"></i> صورة لوجو (رابط أو ملف)</label>
                                <input type="text" name="cfg_logo_url" class="form-control form-control-sm mb-1" placeholder="رابط صورة (اختياري)">
                                <input type="file" name="cfg_logo_file" class="form-control form-control-sm">
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-8">
                                    <label class="form-label small">الهوست (IP / Bug)</label>
                                    <input type="text" name="cfg_host" class="form-control" placeholder="104.16.51.1">
                                </div>
                                <div class="col-4">
                                    <label class="form-label small">البورت</label>
                                    <input type="number" name="cfg_port" class="form-control" value="80">
                                </div>
                            </div>
                            <!-- SOCKS -->
                            <div class="row g-2 mb-3 p-3 rounded bg-light border">
                                <div class="col-8">
                                    <label class="form-label small text-secondary"><i class="bi bi-hdd-stack"></i> SOCKS Proxy IP</label>
                                    <input type="text" name="cfg_socks_ip" class="form-control form-control-sm">
                                </div>
                                <div class="col-4">
                                    <label class="form-label small text-secondary">SOCKS Port</label>
                                    <input type="number" name="cfg_socks_port" class="form-control form-control-sm">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small">SNI (Domain Bug)</label>
                                <input type="text" name="cfg_sni" class="form-control" placeholder="vodafone.com.eg">
                            </div>
                            <div class="mb-3">
                                <label class="form-label small">Payload</label>
                                <textarea name="cfg_payload" class="form-control" rows="2"></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary w-100 py-2 fw-bold">حفظ وإرسال للتطبيق 🚀</button>
                        </form>
                    </div>
                </div>
                <div class="col-md-7">
                    <div class="card p-4">
                        <h5 class="text-accent mb-3"><i class="bi bi-list-check"></i> الكونفجات المتوفرة</h5>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>الاسم</th>
                                        <th>البروتوكول/السوكس</th>
                                        <th>ملفات</th>
                                        <th>حذف</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php foreach(($db['configs'] ?? []) as $c): ?>
                                        <tr>
                                            <td>
                                                <span class="fw-bold"><?= $c['flag'] ?> <?= htmlspecialchars($c['name']) ?></span>
                                            </td>
                                            <td>
                                                <span class="badge bg-secondary"><?= $c['protocol'] ?></span><br>
                                                <?php if(!empty($c['socks_ip'])): ?>
                                                    <small class="text-muted">SOCKS: <?= $c['socks_ip'] ?>:<?= $c['socks_port'] ?></small>
                                                <?php endif; ?>
                                            </td>
                                            <td>
                                                <?php if(!empty($c['file_url'])): ?>
                                                    <a href="<?= $c['file_url'] ?>" target="_blank" class="badge bg-success text-decoration-none">ملف 📂</a>
                                                <?php endif; ?>
                                                <?php if(!empty($c['logo_url'])): ?>
                                                    <img src="<?= $c['logo_url'] ?>" style="height: 24px; border-radius: 4px; border: 1px solid #ccc">
                                                <?php endif; ?>
                                            </td>
                                            <td>
                                                <form method="POST" onsubmit="return confirm('حذف؟');">
                                                    <input type="hidden" name="delete_config_id" value="<?= $c['id'] ?>">
                                                    <button class="btn btn-outline-danger btn-sm"><i class="bi bi-trash"></i></button>
                                                </form>
                                            </td>
                                        </tr>
                                    <?php endforeach; ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- ROUTERS TAB -->
        <div class="tab-pane fade" id="tab-routers">
            <div class="row">
                <div class="col-md-5">
                    <div class="card p-4">
                        <h5 class="text-accent mb-3">إضافة سيرفر جديد</h5>
                        <form method="POST">
                            <input type="hidden" name="add_router" value="1">
                            <div class="mb-3">
                                <label class="form-label small">اسم السيرفر</label>
                                <input type="text" name="rtr_name" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small">IP السيرفر</label>
                                <input type="text" name="rtr_ip" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">حفظ السيرفر 🖥️</button>
                        </form>
                    </div>
                </div>
                <div class="col-md-7">
                    <div class="card p-4">
                        <h5 class="text-accent mb-3">السيرفرات النشطة</h5>
                        <table class="table table-hover">
                            <thead><tr><th>الاسم والـ IP</th><th>الحالة</th><th>حذف</th></tr></thead>
                            <tbody>
                                <?php foreach(($db['routers'] ?? []) as $r): ?>
                                    <tr>
                                        <td><strong><?= htmlspecialchars($r['name']) ?></strong><br><small><?= htmlspecialchars($r['ip']) ?></small></td>
                                        <td><span class="badge bg-success">Online</span></td>
                                        <td>
                                            <form method="POST" onsubmit="return confirm('حذف؟');">
                                                <input type="hidden" name="delete_router_id" value="<?= $r['id'] ?>">
                                                <button class="btn btn-outline-danger btn-sm"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- NOTIFICATIONS TAB -->
        <div class="tab-pane fade" id="tab-notif">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card p-4">
                        <h5 class="text-accent mb-3">إرسال إشعار فوري للتطبيق</h5>
                        <form method="POST">
                            <input type="hidden" name="add_notification" value="1">
                            <div class="mb-3">
                                <label class="form-label small">عنوان الإشعار</label>
                                <input type="text" name="notif_title" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small">الرسالة</label>
                                <textarea name="notif_msg" class="form-control" rows="3" required></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">إرسال 🔔</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

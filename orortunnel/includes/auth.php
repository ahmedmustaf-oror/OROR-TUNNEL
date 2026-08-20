<?php
// OROR TUNNEL Authentication & Session Handler
require_once __DIR__ . '/json_db.php';
require_once __DIR__ . '/helper.php';

if (session_status() === PHP_SESSION_NONE) {
    ini_set('session.cookie_httponly', 1);
    ini_set('session.use_only_cookies', 1);
    ini_set('session.cookie_samesite', 'Strict');
    session_start();
}

class Auth {
    public static function login($username, $password) {
        $users = JsonDB::read('users.json');
        
        // Ensure default super admin exists if empty
        if (empty($users)) {
            $defaultAdmin = [
                'id' => 'user_super_admin',
                'username' => 'admin',
                'password' => password_hash('admin12345', PASSWORD_BCRYPT),
                'name' => 'OROR Super Admin',
                'role' => 'Super Admin',
                'permissions' => ['*'],
                'created_at' => date('Y-m-d H:i:s')
            ];
            JsonDB::saveItem('users.json', $defaultAdmin);
            $users = [$defaultAdmin];
        }

        foreach ($users as $user) {
            if ($user['username'] === $username && password_verify($password, $user['password'])) {
                session_regenerate_id(true);
                $_SESSION['admin_logged_in'] = true;
                $_SESSION['admin_user'] = $user;
                $_SESSION['last_activity'] = time();
                
                logEvent('ADMIN_LOGIN', 'Successful admin login: ' . $username, $user['id']);
                return true;
            }
        }

        logEvent('LOGIN_FAILED', 'Failed login attempt for username: ' . $username);
        return false;
    }

    public static function check() {
        if (!isset($_SESSION['admin_logged_in']) || $_SESSION['admin_logged_in'] !== true) {
            return false;
        }
        if (time() - $_SESSION['last_activity'] > SESSION_LIFETIME) {
            self::logout();
            return false;
        }
        $_SESSION['last_activity'] = time();
        return true;
    }

    public static function user() {
        return $_SESSION['admin_user'] ?? null;
    }

    public static function logout() {
        $_SESSION = [];
        if (ini_get("session.use_cookies")) {
            $params = session_get_cookie_params();
            setcookie(session_name(), '', time() - 42000,
                $params["path"], $params["domain"],
                $params["secure"], $params["httponly"]
            );
        }
        session_destroy();
    }
}

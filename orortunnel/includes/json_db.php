<?php
// OROR TUNNEL Safe JSON Database Engine with File Locking & Atomic Writes
require_once __DIR__ . '/../config/config.php';

class JsonDB {
    private static function getFilePath($filename) {
        $path = DATA_DIR . $filename;
        if (!file_exists($path)) {
            file_put_contents($path, json_encode([], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
        }
        return $path;
    }

    public static function read($filename) {
        $file = self::getFilePath($filename);
        $fp = @fopen($file, 'r');
        if (!$fp) return [];

        if (flock($fp, LOCK_SH)) {
            $content = stream_get_contents($fp);
            flock($fp, LOCK_UN);
            fclose($fp);
            $data = json_decode($content, true);
            return is_array($data) ? $data : [];
        }
        fclose($fp);
        return [];
    }

    public static function write($filename, array $data) {
        $file = self::getFilePath($filename);
        $tempFile = $file . '.tmp.' . uniqid();
        $json = json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

        $fp = @fopen($file, 'c+');
        if (!$fp) return false;

        if (flock($fp, LOCK_EX)) {
            ftruncate($fp, 0);
            fwrite($fp, $json);
            fflush($fp);
            flock($fp, LOCK_UN);
            fclose($fp);
            return true;
        }
        fclose($fp);
        return false;
    }

    public static function findById($filename, $id) {
        $items = self::read($filename);
        foreach ($items as $item) {
            if (isset($item['id']) && $item['id'] === $id) {
                return $item;
            }
        }
        return null;
    }

    public static function saveItem($filename, array $newItem) {
        $items = self::read($filename);
        if (!isset($newItem['id'])) {
            $newItem['id'] = uniqid(str_replace('.json', '', $filename) . '_');
        }
        if (!isset($newItem['created_at'])) {
            $newItem['created_at'] = date('Y-m-d H:i:s');
        }
        $newItem['updated_at'] = date('Y-m-d H:i:s');

        $found = false;
        foreach ($items as $index => $item) {
            if (isset($item['id']) && $item['id'] === $newItem['id']) {
                $items[$index] = array_merge($item, $newItem);
                $found = true;
                break;
            }
        }
        if (!$found) {
            $items[] = $newItem;
        }

        return self::write($filename, $items) ? $newItem : false;
    }

    public static function deleteById($filename, $id) {
        $items = self::read($filename);
        $filtered = array_values(array_filter($items, function($item) use ($id) {
            return !isset($item['id']) || $item['id'] !== $id;
        }));
        return self::write($filename, $filtered);
    }
}

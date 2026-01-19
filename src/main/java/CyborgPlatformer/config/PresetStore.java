package CyborgPlatformer.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple JSON-backed preset store for LevelSettings.
 *
 */
public final class PresetStore {

    private static final String DIR_NAME = ".cyborgplatformer";
    private static final String FILE_NAME = "presets.json";

    private PresetStore() {}

    private static Path getStorePath() {
        String home = System.getProperty("user.home");
        Path dir = Paths.get(home, DIR_NAME);
        return dir.resolve(FILE_NAME);
    }

    public static Map<String, LevelSettings> loadAll() {
        Path path = getStorePath();
        Map<String, LevelSettings> out = new LinkedHashMap<>();
        if (!Files.exists(path)) return out;
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            // crude parse: find object blocks
            String[] parts = text.split("\\{|");
            for (String p : parts) {
                if (!p.contains("}\"" ) && !p.contains("}")) continue;
                String obj = "{" + p.substring(0, p.indexOf('}') + 1);
                String name = extractString(obj, "name");
                if (name == null) continue;
                int maxEnemies = extractInt(obj, "maxEnemies", 10);
                boolean respawn = extractBool(obj, "respawn", false);
                double speed = extractDouble(obj, "speed", 1.0);
                double health = extractDouble(obj, "health", 1.0);
                double damage = extractDouble(obj, "damage", 1.0);
                double spawnInterval = extractDouble(obj, "spawnInterval", 1.0);
                double aggression = extractDouble(obj, "aggression", 1.0);
                out.put(name, new LevelSettings(maxEnemies, respawn, speed, health, damage, spawnInterval, aggression));
            }
        } catch (IOException ignored) {}
        return out;
    }

    public static void savePreset(String name, LevelSettings settings) throws IOException {
        Map<String, LevelSettings> all = loadAll();
        all.put(name, settings);
        writeAll(all);
    }

    public static void deletePreset(String name) throws IOException {
        Map<String, LevelSettings> all = loadAll();
        all.remove(name);
        writeAll(all);
    }

    private static void writeAll(Map<String, LevelSettings> all) throws IOException {
        Path path = getStorePath();
        Files.createDirectories(path.getParent());
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Map.Entry<String, LevelSettings> e : all.entrySet()) {
            if (!first) sb.append(",\n");
            first = false;
            LevelSettings s = e.getValue();
            sb.append("  {");
            sb.append("\"name\":\"").append(escape(e.getKey())).append("\"");
            sb.append(",\"maxEnemies\":").append(s.getMaxEnemies());
            sb.append(",\"respawn\":").append(s.isRespawnOnPlayerDeath());
            sb.append(",\"speed\":").append(s.getSpeedMultiplier());
            sb.append(",\"health\":").append(s.getHealthMultiplier());
            sb.append(",\"damage\":").append(s.getDamageMultiplier());
            sb.append(",\"spawnInterval\":").append(s.getSpawnIntervalMultiplier());
            sb.append(",\"aggression\":").append(s.getAggressionMultiplier());
            sb.append("}");
        }
        sb.append("\n]");
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // --- tiny helpers ---
    private static String escape(String in) { return in.replace("\\", "\\\\").replace("\"", "\\\""); }

    private static String extractString(String obj, String key) {
        String pat = "\"" + key + "\"\s*:\s*\"";
        int i = obj.indexOf(pat);
        if (i < 0) return null;
        int start = i + pat.length();
        int end = obj.indexOf('"', start);
        if (end < 0) return null;
        return obj.substring(start, end);
    }

    private static int extractInt(String obj, String key, int def) {
        String pat = "\"" + key + "\"\s*:\s*";
        int i = obj.indexOf(pat);
        if (i < 0) return def;
        int start = i + pat.length();
        String num = readNumber(obj, start);
        try { return Integer.parseInt(num); } catch (Exception ex) { return def; }
    }

    private static double extractDouble(String obj, String key, double def) {
        String pat = "\"" + key + "\"\s*:\s*";
        int i = obj.indexOf(pat);
        if (i < 0) return def;
        int start = i + pat.length();
        String num = readNumber(obj, start);
        try { return Double.parseDouble(num); } catch (Exception ex) { return def; }
    }

    private static boolean extractBool(String obj, String key, boolean def) {
        String pat = "\"" + key + "\"\s*:\s*";
        int i = obj.indexOf(pat);
        if (i < 0) return def;
        int start = i + pat.length();
        String remaining = obj.substring(start);
        if (remaining.startsWith("true")) return true;
        if (remaining.startsWith("false")) return false;
        return def;
    }

    private static String readNumber(String s, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+') sb.append(c);
            else break;
        }
        return sb.toString();
    }
}

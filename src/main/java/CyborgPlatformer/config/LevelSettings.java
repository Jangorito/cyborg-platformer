package CyborgPlatformer.config;

/**
 * Encapsulates tunable parameters that affect level difficulty and enemy behaviour.
 *
 * Instances are immutable Kotlin-style POJOs (simple getters) and include a few
 * convenient factory methods for presets.
 */
public final class LevelSettings {

    private final int maxEnemies; // spawn cap (not yet enforced everywhere)
    private final boolean respawnOnPlayerDeath;
    private final double speedMultiplier;
    private final double healthMultiplier;
    private final double damageMultiplier;
    private final double spawnIntervalMultiplier;
    private final double aggressionMultiplier; // affects contact cooldown / frequency

    public LevelSettings(int maxEnemies,
                         boolean respawnOnPlayerDeath,
                         double speedMultiplier,
                         double healthMultiplier,
                         double damageMultiplier,
                         double spawnIntervalMultiplier,
                         double aggressionMultiplier) {
        this.maxEnemies = maxEnemies;
        this.respawnOnPlayerDeath = respawnOnPlayerDeath;
        this.speedMultiplier = speedMultiplier;
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.spawnIntervalMultiplier = spawnIntervalMultiplier;
        this.aggressionMultiplier = aggressionMultiplier;
    }

    public int getMaxEnemies() { return maxEnemies; }
    public boolean isRespawnOnPlayerDeath() { return respawnOnPlayerDeath; }
    public double getSpeedMultiplier() { return speedMultiplier; }
    public double getHealthMultiplier() { return healthMultiplier; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public double getSpawnIntervalMultiplier() { return spawnIntervalMultiplier; }
    public double getAggressionMultiplier() { return aggressionMultiplier; }

    public static LevelSettings easy() {
        return new LevelSettings(
                6,      // maxEnemies
                false,  // respawn
                0.85,   // speed
                0.9,    // health
                0.8,    // damage
                1.2,    // spawn interval (slower spawning)
                0.8     // aggression
        );
    }

    public static LevelSettings medium() {
        return new LevelSettings(
                10,
                true,
                1.0,
                1.0,
                1.0,
                1.0,
                1.0
        );
    }

    public static LevelSettings hard() {
        return new LevelSettings(
                16,
                true,
                1.25,
                1.5,
                1.3,
                0.8,
                1.25
        );
    }

    public static LevelSettings defaultsFor(GameMode mode) {
        return switch (mode) {
            case EASY -> easy();
            case MEDIUM -> medium();
            case HARD -> hard();
            default -> medium();
        };
    }
}

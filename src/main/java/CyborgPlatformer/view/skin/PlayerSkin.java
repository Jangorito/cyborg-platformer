package CyborgPlatformer.view.skin;

public record PlayerSkin(
        int hair,
        int skin,
        int visor,
        int belt,
        int suitDark,
        int suitMid,
        int suitLight,
        int led
) {
    public static int hex(String rgb) {
        return Integer.parseInt(rgb, 16) | 0xFF000000; // ARGB
    }
}


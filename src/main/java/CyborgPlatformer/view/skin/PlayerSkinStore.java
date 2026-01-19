package CyborgPlatformer.view.skin;

import java.util.Objects;

public final class PlayerSkinStore {
    private PlayerSkinStore() {}

    private static PlayerSkin current = PlayerSkins.CLASSIC;

    public static PlayerSkin get() {
        return current;
    }

    public static void set(PlayerSkin skin) {
        Objects.requireNonNull(skin);
        current = skin;
    }
}

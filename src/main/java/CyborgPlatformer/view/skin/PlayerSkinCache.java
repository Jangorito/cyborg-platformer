package CyborgPlatformer.view.skin;

import CyborgPlatformer.assets.AssetManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PlayerSkinCache {

    private final AssetManager assets;
    private final Map<PlayerSkin, SkinnedPlayerAssets> cache = new HashMap<>();

    public PlayerSkinCache(AssetManager assets) {
        this.assets = Objects.requireNonNull(assets, "assets");
    }

    public SkinnedPlayerAssets get(PlayerSkin skin) {
        Objects.requireNonNull(skin, "skin");
        return cache.computeIfAbsent(skin, s -> new SkinnedPlayerAssets(assets, s));
    }
}

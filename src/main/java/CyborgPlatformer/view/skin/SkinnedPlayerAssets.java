package CyborgPlatformer.view.skin;

import CyborgPlatformer.assets.AssetManager;
import javafx.scene.image.Image;

import java.util.Map;
import java.util.Objects;

public final class SkinnedPlayerAssets {

    private final Image[] idle;
    private final Image[] run;
    private final Image[] hurt;
    private final Image   shoot;

    public SkinnedPlayerAssets(AssetManager assets, PlayerSkin skin) {
        Objects.requireNonNull(assets);
        Objects.requireNonNull(skin);

        Map<Integer, Integer> map =
                PlayerSkinMapper.buildRecolorMap(skin);

        this.idle  = recolorAll(assets.playerIdle(), map);
        this.run   = recolorAll(assets.playerRun(), map);
        this.hurt  = recolorAll(assets.playerHurt(), map);
        this.shoot = SpriteRecolorer.recolor(assets.playerShoot(), map);
    }

    private Image[] recolorAll(Image[] src, Map<Integer, Integer> map) {
        Image[] out = new Image[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = SpriteRecolorer.recolor(src[i], map);
        }
        return out;
    }

    public Image[] idle()  { return idle; }
    public Image[] run()   { return run; }
    public Image[] hurt()  { return hurt; }
    public Image   shoot() { return shoot; }
}

package CyborgPlatformer.view.skin;

import java.util.HashMap;
import java.util.Map;

public final class PlayerSkinMapper {

    private PlayerSkinMapper() {}

    public static Map<Integer, Integer> buildRecolorMap(PlayerSkin skin) {
        Map<Integer, Integer> m = new HashMap<>();

        m.put(BasePlayerPalette.HAIR,  skin.hair());
        m.put(BasePlayerPalette.SKIN,  skin.skin());
        m.put(BasePlayerPalette.VISOR, skin.visor());
        m.put(BasePlayerPalette.BELT,  skin.belt());

        m.put(BasePlayerPalette.SUIT_DARK,  skin.suitDark());
        m.put(BasePlayerPalette.SUIT_MID,   skin.suitMid());
        m.put(BasePlayerPalette.SUIT_LIGHT, skin.suitLight());

        m.put(BasePlayerPalette.LED, skin.led());

        return m;
    }
}
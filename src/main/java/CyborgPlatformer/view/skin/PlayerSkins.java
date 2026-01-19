package CyborgPlatformer.view.skin;

public final class PlayerSkins {
    private PlayerSkins() {}

    public static final PlayerSkin CLASSIC = new PlayerSkin(
            PlayerSkin.hex("158968"),   // hair
            PlayerSkin.hex("FFDBA5"),   // skin
            PlayerSkin.hex("663B93"),   // visor
            PlayerSkin.hex("38002C"),   // belt
            PlayerSkin.hex("222A5C"),   // suitDark
            PlayerSkin.hex("566A89"),   // suitMid
            PlayerSkin.hex("8BABBF"),   // suitLight
            PlayerSkin.hex("5BECF1")    // led
    );

    // “Stealth”
    public static final PlayerSkin STEALTH = new PlayerSkin(
            PlayerSkin.hex("2A2A2A"),   // hair
            PlayerSkin.hex("C8A77B"),   // skin
            PlayerSkin.hex("FF4D4D"),   // visor
            PlayerSkin.hex("1C1C1C"),   // belt
            PlayerSkin.hex("111827"),   // suitDark
            PlayerSkin.hex("374151"),   // suitMid
            PlayerSkin.hex("9CA3AF"),   // suitLight
            PlayerSkin.hex("22D3EE")    // led
    );

    // NOTE: DEFAULT_CYBORG removed — use CLASSIC for the default cyborg appearance

    public static final PlayerSkin ELITE_NEON_UNIT = new PlayerSkin(
            PlayerSkin.hex("1BD4C4"),   // hair (Neon Cyber Accent)
            PlayerSkin.hex("F0D4B8"),   // skin (Synth-Human)
            PlayerSkin.hex("A23BBF"),   // visor (Neon Magenta)
            PlayerSkin.hex("1B1B24"),   // belt (Carbon Black)
            PlayerSkin.hex("2A0F3D"),   // suitDark (purple option)
            PlayerSkin.hex("6B2FA3"),   // suitMid
            PlayerSkin.hex("B46CFF"),   // suitLight
            PlayerSkin.hex("5FFFFF")    // led (High-Energy Neon)
    );

    public static final PlayerSkin TACTICAL_RECON = new PlayerSkin(
            PlayerSkin.hex("3C8F76"),   // hair (Military Green)
            PlayerSkin.hex("E7B87F"),   // skin (Warm Tan)
            PlayerSkin.hex("3F1F5E"),   // visor (Dark Tactical)
            PlayerSkin.hex("7A5228"),   // belt (Brass Tech)
            PlayerSkin.hex("111827"),   // suitDark
            PlayerSkin.hex("374151"),   // suitMid
            PlayerSkin.hex("6B7280"),   // suitLight
            PlayerSkin.hex("74D8FF")    // led (Ice Blue)
    );

}

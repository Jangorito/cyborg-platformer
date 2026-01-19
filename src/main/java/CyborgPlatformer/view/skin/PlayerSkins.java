package CyborgPlatformer.view.skin;

public final class PlayerSkins {
    private PlayerSkins() {}

    public static final PlayerSkin CLASSIC = new PlayerSkin(
            PlayerSkin.hex("158868"), // hair
            PlayerSkin.hex("FFDBA5"), // skin
            PlayerSkin.hex("663B93"), // visor
            PlayerSkin.hex("38002C"), // belt
            PlayerSkin.hex("222A5C"), // suitDark
            PlayerSkin.hex("566A89"), // suitMid
            PlayerSkin.hex("8BABBF"), // suitLight
            PlayerSkin.hex("5BECF1")  // led
    );

    // Example “Stealth”
    public static final PlayerSkin STEALTH = new PlayerSkin(
            PlayerSkin.hex("2A2A2A"),
            PlayerSkin.hex("C8A77B"),
            PlayerSkin.hex("FF4D4D"), // visor red
            PlayerSkin.hex("1C1C1C"),
            PlayerSkin.hex("111827"),
            PlayerSkin.hex("374151"),
            PlayerSkin.hex("9CA3AF"),
            PlayerSkin.hex("22D3EE")
    );

    public static final PlayerSkin TEST = new PlayerSkin(
            PlayerSkin.hex("00FF00"), // hair
            PlayerSkin.hex("FF00FF"), // skin
            PlayerSkin.hex("FFFFFF"), // visor
            PlayerSkin.hex("FFFF00"), // belt
            PlayerSkin.hex("FF0000"), // suitDark
            PlayerSkin.hex("00AAFF"), // suitMid
            PlayerSkin.hex("0000FF"), // suitLight
            PlayerSkin.hex("00FFFF")  // led
    );

}

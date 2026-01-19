package CyborgPlatformer.view.skin;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.Map;
import java.util.Objects;

public final class SpriteRecolorer {

    private SpriteRecolorer() {}

    /**
     * Returns a recoloured copy of the given image.
     *
     * @param src base sprite image
     * @param argbMap map of sourceARGB -> targetARGB (both should include alpha)
     */
    public static Image recolor(Image src, Map<Integer, Integer> argbMap) {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(argbMap, "argbMap");

        int w = (int) src.getWidth();
        int h = (int) src.getHeight();

        PixelReader r = src.getPixelReader();
        if (r == null) return src;

        WritableImage out = new WritableImage(w, h);
        PixelWriter wri = out.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = r.getArgb(x, y);

                // Preserve transparency exactly
                int a = (argb >>> 24) & 0xFF;
                if (a == 0) {
                    wri.setArgb(x, y, argb);
                    continue;
                }

                Integer repl = argbMap.get(argb);
                wri.setArgb(x, y, repl != null ? repl : argb);
            }
        }

        return out;
    }
}

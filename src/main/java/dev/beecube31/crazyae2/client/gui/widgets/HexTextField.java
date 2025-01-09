package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.common.util.ColorUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.jetbrains.annotations.NotNull;

public class HexTextField extends GuiTextField {

    private final Slider rSlider;
    private final Slider gSlider;
    private final Slider bSlider;

    public HexTextField
    (
            final FontRenderer fontRenderer,
            final int x,
            final int y,
            final int width,
            final int height,
            final int id,
            final Slider rSlider,
            final Slider gSlider,
            final Slider bSlider
    ) {
        super(id, fontRenderer, x, y, width, height);
        this.setMaxStringLength(7);
        this.setTextColor(0xFFFFFF);
        this.setVisible(true);

        this.rSlider = rSlider;
        this.gSlider = gSlider;
        this.bSlider = bSlider;
    }

    @Override
    public void writeText(@NotNull final String selectedText) {
        final String original = this.getText();
        super.writeText(selectedText);

        this.parseHex(original);
    }

    public void parseHex(String originalText) {
        int[] colors = ColorUtils.getRGBFromHex(this.getText());

        if (colors != null) {
            this.rSlider.setScroll(colors[0]);
            this.gSlider.setScroll(colors[1]);
            this.bSlider.setScroll(colors[2], true);
        }
    }
}

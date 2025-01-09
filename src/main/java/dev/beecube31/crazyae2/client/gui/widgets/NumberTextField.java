package dev.beecube31.crazyae2.client.gui.widgets;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.jetbrains.annotations.NotNull;

public class NumberTextField extends GuiTextField {

    private final Class type;

    public NumberTextField(final FontRenderer fontRenderer, final int x, final int y, final int width, final int height, final Class type, final int id) {
        super(id, fontRenderer, x, y, width, height);
        this.type = type;
    }

    @Override
    public void writeText(@NotNull final String selectedText) {
        final String original = this.getText();
        super.writeText(selectedText);

        if (this.getText().isEmpty()) {
            return;
        }
        try {
            if (this.type == int.class || this.type == Integer.class) {
                Integer.parseInt(this.getText());
            } else if (this.type == long.class || this.type == Long.class) {
                Long.parseLong(this.getText());
            } else if (this.type == double.class || this.type == Double.class) {
                Double.parseDouble(this.getText());
            }
        } catch (final NumberFormatException e) {
            this.setText(original);
        }
    }
}

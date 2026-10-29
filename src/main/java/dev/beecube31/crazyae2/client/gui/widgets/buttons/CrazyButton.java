package dev.beecube31.crazyae2.client.gui.widgets.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.CrazyBlitHelper;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class CrazyButton extends AbstractButton {
    public static final ResourceLocation WIDGETS_LOCATION = ResourceLocation.fromNamespaceAndPath(CrazyAE.MODID, "textures/guis/widgets/buttons.png");

    protected static final CrazyButton.CreateNarration DEFAULT_NARRATION = Supplier::get;

    public static final int SMALL_WIDTH = 120;
    public static final int DEFAULT_WIDTH = 150;
    public static final int DEFAULT_HEIGHT = 20;

    protected final CrazyButton.OnPress onPress;
    protected final CrazyButton.CreateNarration createNarration;

    protected final ColorContainer hue;

    public static Builder builder(@NotNull Component p_254439_, CrazyButton.@NotNull OnPress p_254567_) {
        return new Builder(p_254439_, p_254567_);
    }

    protected CrazyButton(CrazyButton.Builder builder) {
        this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration, builder.hue);
        setTooltip(builder.tooltip);
    }

    protected CrazyButton(int p_259075_, int p_259271_, int p_260232_, int p_260028_, Component p_259351_, CrazyButton.OnPress p_260152_, CrazyButton.CreateNarration p_259552_, ColorContainer hue) {
        super(p_259075_, p_259271_, p_260232_, p_260028_, p_259351_);
        this.onPress = p_260152_;
        this.createNarration = p_259552_;
        this.hue = hue;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final Component message;
        private final CrazyButton.OnPress onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private ColorContainer hue;
        private CrazyButton.CreateNarration createNarration = CrazyButton.DEFAULT_NARRATION;

        public Builder(Component p_254097_, CrazyButton.OnPress p_253761_) {
            this.message = p_254097_;
            this.onPress = p_253761_;
        }

        public CrazyButton.Builder setHue(ColorContainer hue) {
            this.hue = hue;
            return this;
        }

        public CrazyButton.Builder pos(int p_254538_, int p_254216_) {
            this.x = p_254538_;
            this.y = p_254216_;
            return this;
        }

        public CrazyButton.Builder width(int p_254259_) {
            this.width = p_254259_;
            return this;
        }

        public CrazyButton.Builder size(int p_253727_, int p_254457_) {
            this.width = p_253727_;
            this.height = p_254457_;
            return this;
        }

        public CrazyButton.Builder bounds(int p_254166_, int p_253872_, int p_254522_, int p_253985_) {
            return this.pos(p_254166_, p_253872_).size(p_254522_, p_253985_);
        }

        public CrazyButton.Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public CrazyButton.Builder createNarration(CrazyButton.CreateNarration createNarration) {
            this.createNarration = createNarration;
            return this;
        }

        public CrazyButton build() {
            return build(CrazyButton::new);
        }

        public CrazyButton build(Function<Builder, CrazyButton> builder) {
            return builder.apply(this);
        }
    }

    @Override
    public void onPress() {
        if (this.onPress != null) {
            this.onPress.onPress(this);
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        ColorContainer hue = this.hue != null ? this.hue : new ColorContainer();

        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(hue.getRed(), hue.getGreen(), hue.getBlue(), this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        CrazyBlitHelper.blitNineSliced(guiGraphics, WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderString(guiGraphics, minecraft.font, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }

    @OnlyIn(Dist.CLIENT)
    public interface CreateNarration {
        MutableComponent createNarrationMessage(Supplier<MutableComponent> mutableComponentSupplier);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(CrazyButton button);
    }

}

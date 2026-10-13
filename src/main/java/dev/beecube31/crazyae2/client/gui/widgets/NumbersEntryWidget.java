package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.MathExpressionParser;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.Rects;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.core.localization.GuiText;
import com.google.common.primitives.Longs;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.ICrazyCompositeWidget;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.*;
import java.util.function.Consumer;

public class NumbersEntryWidget implements ICrazyCompositeWidget {

    private static final long[] STEPS = new long[] { 1, 10, 100, 1000 };
    private static final Component PLUS = Component.literal("+");
    private static final Component MINUS = Component.literal("-");
    private static final int UNIT_PADDING = 3;
    private final int errorTextColor;

    private final CrazyTextField textField;
    private final DecimalFormat decimalFormat;
    private final ColorContainer hue;
    private final ColorContainer textHue;
    private NumberEntryType type;
    private List<CrazyButton> buttons;
    private long minValue;
    private long maxValue = Long.MAX_VALUE;
    private ValidationIcon validationIcon;

    // Called when the value changes
    private Runnable onChange;

    // Called when the user presses enter while there's a valid number in the field
    private Runnable onConfirm;

    private boolean hideValidationIcon;

    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    private Rect2i textFieldBounds = Rects.ZERO;
    private Point currentScreenOrigin = Point.ZERO;

    public NumbersEntryWidget(ScreenStyle style, NumberEntryType type, ColorContainer hue, ColorContainer textHue) {
        this.errorTextColor = style.getColor(PaletteColor.TEXTFIELD_ERROR).toARGB();

        this.type = Objects.requireNonNull(type, "type");
        this.decimalFormat = new DecimalFormat("#.######", new DecimalFormatSymbols());
        this.decimalFormat.setParseBigDecimal(true);
        this.decimalFormat.setNegativePrefix("-");

        this.hue = hue;
        this.textHue = textHue;

        Font font = Minecraft.getInstance().font;

        this.textField = new CrazyTextField(font, 0, 0, 0, font.lineHeight, () -> this.hue);
        this.textField.setMaxLength(16);
        this.textField.setVisible(true);
        this.textField.setResponder(text -> {
            validate();
            if (onChange != null) {
                this.onChange.run();
            }
        });
        this.textField.setOnConfirm(() -> {
            if (this.onConfirm != null && getLongValue().isPresent()) {
                this.onConfirm.run();
            }
        });
        validate();
    }

    public void setOnConfirm(Runnable callback) {
        this.onConfirm = callback;
    }

    public void setOnChange(Runnable callback) {
        this.onChange = callback;
    }

    public void setActive(boolean active) {
        this.textField.setEditable(active);
        this.buttons.forEach(b -> b.active = active);
    }


    public void setTextFieldBounds(Rect2i bounds) {
        this.textFieldBounds = bounds;
        this.textField.move(currentScreenOrigin.move(bounds.getX(), bounds.getY()));
        int unitWidth = 0;
        if (this.type.unit() != null) {
            unitWidth = Minecraft.getInstance().font.width(this.type.unit()) + UNIT_PADDING;
        }
        this.textField.resize(bounds.getWidth() - unitWidth, bounds.getHeight());
    }

    public void setTextFieldStyle(WidgetStyle style) {
        int left = 0;
        if (style.getLeft() != null) {
            left = style.getLeft();
        }
        int top = 0;
        if (style.getTop() != null) {
            top = style.getTop();
        }
        setTextFieldBounds(new Rect2i(
                left,
                top,
                style.getWidth(),
                style.getHeight()));
    }

    public void setMinValue(long minValue) {
        this.minValue = minValue;
        validate();
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
        validate();
    }

    @Override
    public void setPosition(Point position) {
        bounds = new Rect2i(position.getX(), position.getY(), bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public void setSize(int width, int height) {
        bounds = new Rect2i(bounds.getX(), bounds.getY(), width, height);
    }

    @Override
    public Rect2i getBounds() {
        return bounds;
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, CrazyAEBaseGui<?> screen) {
        int left = bounds.getX() + this.bounds.getX();
        int top = bounds.getY() + this.bounds.getY();

        List<CrazyButton> buttons = new ArrayList<>(9);

        buttons.add(CrazyButton.builder(makeLabel(PLUS, STEPS[0]), btn -> addQty(STEPS[0])).setHue(this.hue)
                .setTextHue(this.textHue).bounds(left, top, 22, 20).build());
        buttons.add(CrazyButton.builder(makeLabel(PLUS, STEPS[1]), btn -> addQty(STEPS[1])).setHue(this.hue)
                .setTextHue(this.textHue).bounds(left + 28, top, 28, 20).build());
        buttons.add(CrazyButton.builder(makeLabel(PLUS, STEPS[2]), btn -> addQty(STEPS[2])).setHue(this.hue)
                .setTextHue(this.textHue).bounds(left + 62, top, 32, 20).build());
        buttons.add(CrazyButton.builder(makeLabel(PLUS, STEPS[3]), btn -> addQty(STEPS[3])).setHue(this.hue)
                .setTextHue(this.textHue).bounds(left + 100, top, 38, 20).build());

        buttons.forEach(addWidget);

        this.currentScreenOrigin = Point.fromTopLeft(bounds);
        setTextFieldBounds(this.textFieldBounds);
        screen.setInitialFocus(this.textField);
        addWidget.accept(this.textField);

        buttons.add(CrazyButton.builder(makeLabel(MINUS, STEPS[0]), btn -> addQty(-STEPS[0]))
                .bounds(left, top + 42, 22, 20).setHue(this.hue).setTextHue(this.textHue).build());
        buttons.add(CrazyButton.builder(makeLabel(MINUS, STEPS[1]), btn -> addQty(-STEPS[1]))
                .bounds(left + 28, top + 42, 28, 20).setTextHue(this.textHue).setHue(this.hue).build());
        buttons.add(CrazyButton.builder(makeLabel(MINUS, STEPS[2]), btn -> addQty(-STEPS[2]))
                .bounds(left + 62, top + 42, 32, 20).setTextHue(this.textHue).setHue(this.hue).build());
        buttons.add(CrazyButton.builder(makeLabel(MINUS, STEPS[3]), btn -> addQty(-STEPS[3]))
                .bounds(left + 100, top + 42, 38, 20).setTextHue(this.textHue).setHue(this.hue).build());

        if (!hideValidationIcon) {
            this.validationIcon = new ValidationIcon(btn -> {}, this.hue, this.textHue);
            this.validationIcon.setX(left + 104);
            this.validationIcon.setY(top + 27);
            buttons.add(this.validationIcon);
        }

        buttons.subList(4, buttons.size()).forEach(addWidget);

        this.buttons = buttons;

        // we need to re-validate because the icon may now be present and needs it's
        // initial state
        this.validate();
    }

    public boolean startsWithEquals() {
        return textField.getValue().startsWith("=");
    }

    public OptionalInt getIntValue() {
        var value = getLongValue();
        if (value.isPresent()) {
            var longValue = value.getAsLong();
            if (longValue > Integer.MAX_VALUE) {
                return OptionalInt.empty();
            }
            return OptionalInt.of((int) longValue);
        }
        return OptionalInt.empty();
    }

    public OptionalLong getLongValue() {
        var internalValue = getValueInternal();
        if (internalValue.isEmpty()) {
            return OptionalLong.empty();
        }

        // Reject decimal values if the unit is integral
        if (type.amountPerUnit() == 1 && internalValue.get().scale() > 0) {
            return OptionalLong.empty();
        }

        var externalValue = convertToExternalValue(internalValue.get());
        if (externalValue < minValue) {
            return OptionalLong.empty();
        } else if (externalValue > maxValue) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(externalValue);
    }

    public void setLongValue(long value) {
        var internalValue = convertToInternalValue(Longs.constrainToRange(value, minValue, maxValue));
        this.textField.setValue(decimalFormat.format(internalValue));
        this.textField.moveCursorToEnd();
        this.textField.setHighlightPos(0);
        validate();
    }

    private void addQty(long delta) {
        var currentValue = getValueInternal().orElse(BigDecimal.ZERO);
        var newValue = currentValue.add(BigDecimal.valueOf(delta));
        var minimum = convertToInternalValue(this.minValue).setScale(0, RoundingMode.CEILING);
        var maximum = convertToInternalValue(this.maxValue).setScale(0, RoundingMode.FLOOR);
        if (newValue.compareTo(minimum) < 0) {
            newValue = minimum;
        } else if (newValue.compareTo(maximum) > 0) {
            newValue = maximum;
        } else if (currentValue.compareTo(BigDecimal.ONE) == 0 && delta > 0 && delta % 10 == 0) {
            newValue = newValue.subtract(BigDecimal.ONE);
        }
        setValueInternal(newValue);
    }

    private Optional<BigDecimal> getValueInternal() {
        var textValue = textField.getValue();
        if (textValue.startsWith("=")) {
            textValue = textValue.substring(1);
        }
        return MathExpressionParser.parse(textValue, decimalFormat);
    }

    private boolean isNumber() {
        var position = new ParsePosition(0);
        var textValue = textField.getValue().trim();
        decimalFormat.parse(textValue, position);
        return position.getErrorIndex() == -1 && position.getIndex() == textValue.length();
    }

    private void setValueInternal(BigDecimal value) {
        textField.setValue(decimalFormat.format(value));
    }

    private void validate() {
        List<Component> validationErrors = new ArrayList<>();
        List<Component> infoMessages = new ArrayList<>();

        var possibleValue = getValueInternal();
        if (possibleValue.isPresent()) {
            // Reject decimal values if the unit is integral
            if (type.amountPerUnit() == 1 && possibleValue.get().scale() > 0) {
                validationErrors.add(GuiText.NumberNonInteger.text());
            } else {
                var value = convertToExternalValue(possibleValue.get());
                if (value < minValue) {
                    var formatted = decimalFormat.format(convertToInternalValue(minValue));
                    validationErrors.add(GuiText.NumberLessThanMinValue.text(formatted));
                } else if (value > maxValue) {
                    var formatted = decimalFormat.format(convertToInternalValue(maxValue));
                    validationErrors.add(GuiText.NumberGreaterThanMaxValue.text(formatted));
                } else if (!isNumber()) { // is a mathematical expression
                    // displaying the evaluation of the expression
                    infoMessages.add(Component.literal("= " + decimalFormat.format(possibleValue.get())));
                }
            }
        } else {
            validationErrors.add(GuiText.InvalidNumber.text());
        }

        boolean valid = validationErrors.isEmpty();
        var tooltip = valid ? infoMessages : validationErrors;
        this.textField.setTextColorOverride(valid ? null : errorTextColor);
        this.textField.setTooltipMessage(tooltip);

        if (this.validationIcon != null) {
            this.validationIcon.setValid(valid);
            this.validationIcon.setTooltip(tooltip);
        }
    }

    private Component makeLabel(Component prefix, long amount) {
        return prefix.plainCopy().append(decimalFormat.format(amount));
    }

    public void setHideValidationIcon(boolean hideValidationIcon) {
        this.hideValidationIcon = hideValidationIcon;
    }

    public NumberEntryType getType() {
        return type;
    }

    public void setType(NumberEntryType type) {
        if (this.type == type) {
            return;
        }
        this.type = type;
        setTextFieldBounds(this.textFieldBounds);
        if (onChange != null) {
            onChange.run();
        }

        validate();
    }

    private long convertToExternalValue(BigDecimal internalValue) {
        var multiplicand = BigDecimal.valueOf(type.amountPerUnit());
        var value = internalValue.multiply(multiplicand, MathContext.DECIMAL128);
        value = value.setScale(0, RoundingMode.UP);
        return value.longValue();
    }

    private BigDecimal convertToInternalValue(long externalValue) {
        var divisor = BigDecimal.valueOf(type.amountPerUnit());
        return BigDecimal.valueOf(externalValue).divide(divisor, MathContext.DECIMAL128);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        if (type.unit() != null) {
            var font = Minecraft.getInstance().font;
            var x = bounds.getX() + textFieldBounds.getX() + textFieldBounds.getWidth()
                    - font.width(type.unit());
            var y = (int) (bounds.getY() + textFieldBounds.getY() + (textFieldBounds.getHeight() - font.lineHeight) / 2f
                    + 1);

            int unitColor = this.textHue != null ? this.textHue.getIntColor() & 0xFFFFFF : 0x404040;
            guiGraphics.drawString(font, type.unit(), x, y, 0xFF000000 | unitColor, false);
        }
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        if (textFieldBounds.contains(mousePos.getX(), mousePos.getY())) {
            if (getValueInternal().isPresent()) {
                if (delta < 0) {
                    addQty(-1);
                } else if (delta > 0) {
                    addQty(1);
                }

                return true;
            }
        }
        return false;
    }
}

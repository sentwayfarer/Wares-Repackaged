package com.flarelabsmc.mpfui.renderable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class MPFRenderable<T extends MPFRenderable<T>> extends AbstractWidget {
    protected VisibilityPredicate<T> visibilityPredicate = (renderable, mouseX, mouseY) -> true;
    protected Supplier<Component> tooltip = Component::empty;
    protected int tooltipWidth = 220;

    public MPFRenderable(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public MPFRenderable(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public abstract T getThis();

    public T setTooltip(Supplier<Component> tooltip) {
        this.tooltip = tooltip;
        return getThis();
    }

    public T setTooltip(Component tooltip) {
        this.tooltip = () -> tooltip;
        return getThis();
    }

    public T setTooltipWidth(int tooltipWidth) {
        this.tooltipWidth = tooltipWidth;
        return getThis();
    }

    public T visibility(VisibilityPredicate<T> predicate) {
        this.visibilityPredicate = predicate;
        return getThis();
    }

    public boolean isVisible(int mouseX, int mouseY) {
        return this.visible && visibilityPredicate.isVisible(getThis(), mouseX, mouseY);
    }

    public abstract void submit(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (isVisible(mouseX, mouseY)) {
            submit(graphics, mouseX, mouseY, partialTick);
            super.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    public interface VisibilityPredicate<T extends MPFRenderable<?>> {
        boolean isVisible(T renderable, int mouseX, int mouseY);
    }
}

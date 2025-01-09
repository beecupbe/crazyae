package dev.beecube31.crazyae2.common.interfaces.gui;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;

public interface IScrollSrc {
    float getCurrentScroll();

    void click(final CrazyAEBaseGui aeBaseGui, final int x, final int y);

    void onClickEnd(final CrazyAEBaseGui aeBaseGui, final int x, final int y);

    void draw(final CrazyAEBaseGui g);

    void wheel(int delta);
}

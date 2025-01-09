package dev.beecube31.crazyae2.common.interfaces.gui;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;

public interface IGuiElementsCallbackHandler {
    void onInteractionStart();

    void onInteractionUpdate();

    void onInteractionEnd();

    CrazyAEBaseGui getCallbackHandler();
}

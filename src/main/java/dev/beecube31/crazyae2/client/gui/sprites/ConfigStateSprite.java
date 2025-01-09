package dev.beecube31.crazyae2.client.gui.sprites;

public enum ConfigStateSprite {
    IMPROVED_CONDENSER(new StateSprite[]{
            StateSprite.TRASH_SLOT,
            StateSprite.TRASH_SLOT,
            StateSprite.TRASH_SLOT,

    });


    private final StateSprite[] sprites;

    ConfigStateSprite
    (
            StateSprite[] sprites
    ) {
        this.sprites = sprites;
    }

    public StateSprite[] getSprites() {
        return sprites;
    }
}

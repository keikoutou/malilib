package fi.dy.masa.malilib.render.text;

import net.minecraft.util.ResourceLocation;

public class Glyph
{
    public final ResourceLocation texture;
    public final float u1;
    public final float u2;
    public final float v1;
    public final float v2;
    public final int width;
    public final int height;
    public final int renderWidth;
    public final boolean whiteSpace;

    public Glyph(ResourceLocation texture, float u1, float v1, float u2, float v2,
                 int width, int height, boolean whiteSpace)
    {
        this(texture, u1, v1, u2, v2, width, height, width, whiteSpace);
    }

    public Glyph(ResourceLocation texture, float u1, float v1, float u2, float v2,
                 int width, int height, int renderWidth, boolean whiteSpace)
    {
        this.texture = texture;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.width = width;
        this.height = height;
        this.renderWidth = renderWidth;
        this.whiteSpace = whiteSpace;
    }
}

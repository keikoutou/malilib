package fi.dy.masa.malilib.gui.widget;

import java.util.Arrays;
import java.util.List;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.render.text.StringListRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class LabelWidget extends BackgroundWidget
{
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected boolean useBackgroundForHoverOverflow = true;
    protected boolean visible = true;
    protected int totalHeight;
    protected int totalWidth;

    public LabelWidget(int x, int y, int width, int height, int textColor)
    {
        super(x, y, width, height);

        this.stringListRenderer.getNormalTextSettings().setTextColor(textColor);
        this.stringListRenderer.getHoverTextSettings().setTextColor(textColor);
    }

    public LabelWidget(int x, int y, int textColor, String... text)
    {
        this(x, y, -1, -1, textColor, text);
    }

    public LabelWidget(int x, int y, int width, int height, int textColor, String... text)
    {
        this(x, y, width, height, textColor);

        this.setText(Arrays.asList(text));
    }

    public LabelWidget(int x, int y, int width, int height, int textColor, StyledTextLine... lines)
    {
        this(x, y, width, height, textColor, Arrays.asList(lines));
    }

    public LabelWidget(int x, int y, int width, int height, int textColor, List<StyledTextLine> lines)
    {
        this(x, y, width, height, textColor);

        this.setStyledTextLines(lines);
    }

    public int getTotalWidth()
    {
        return this.totalWidth;
    }

    public int getTotalHeight()
    {
        return this.totalHeight;
    }

    protected void clearText()
    {
        this.stringListRenderer.clearText();
    }

    public LabelWidget setText(String translationKey, Object... args)
    {
        this.stringListRenderer.setText(translationKey, args);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setText(List<String> lines)
    {
        this.stringListRenderer.setText(lines);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget addLine(String translationKey, Object... args)
    {
        this.stringListRenderer.addLine(translationKey, args);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setStyledText(StyledText text)
    {
        this.stringListRenderer.setStyledText(text);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setStyledTextLines(StyledTextLine... lines)
    {
        this.stringListRenderer.setStyledTextLines(Arrays.asList(lines));
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setStyledTextLines(List<StyledTextLine> lines)
    {
        this.stringListRenderer.setStyledTextLines(lines);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget addStyledTextLine(StyledTextLine line)
    {
        this.stringListRenderer.addStyledTextLine(line);
        this.updateLabelWidgetSize();
        return this;
    }

    public LabelWidget setVisible(boolean visible)
    {
        this.visible = visible;
        return this;
    }

    public LabelWidget setHorizontalAlignment(HorizontalAlignment alignment)
    {
        this.stringListRenderer.setHorizontalAlignment(alignment);
        return this;
    }

    public LabelWidget setUseTextShadow(boolean useShadow)
    {
        this.stringListRenderer.getNormalTextSettings().setUseTextShadow(useShadow);
        return this;
    }

    @Override
    public void setLineHeight(int lineHeight)
    {
        super.setLineHeight(lineHeight);
        this.stringListRenderer.setLineHeight(lineHeight);
    }
    
    public LabelWidget setNormalTextColor(int color)
    {
        this.stringListRenderer.getNormalTextSettings().setTextColor(color);
        return this;
    }

    public LabelWidget setHoverTextColor(int color)
    {
        this.stringListRenderer.getHoverTextSettings().setTextColor(color);
        return this;
    }

    public LabelWidget setUseBackgroundForHoverOverflow(boolean useBackground)
    {
        this.useBackgroundForHoverOverflow = useBackground;
        return this;
    }

    protected void updateLabelWidgetSize()
    {
        this.updateWidth();
        this.updateHeight();
        this.updateStringRendererSize();
    }

    @Override
    protected void onSizeChanged()
    {
        this.updateStringRendererSize();
    }

    protected void updateStringRendererSize()
    {
        int width = this.hasMaxWidth ? this.maxWidth : this.getWidth();
        int height = this.hasMaxHeight ? this.maxHeight : this.getHeight();
        int bw = this.getActiveBorderWidth() * 2;

        this.stringListRenderer.setMaxWidth(width - this.padding.getLeft() - this.padding.getRight() - bw);
        this.stringListRenderer.setMaxHeight(height - this.padding.getTop() - this.padding.getBottom() - bw);
        this.stringListRenderer.reAddLines();
    }

    @Override
    public void updateWidth()
    {
        this.totalWidth = this.stringListRenderer.getTotalTextWidth() + this.padding.getLeft() + this.padding.getRight();
        this.totalWidth += this.getActiveBorderWidth() * 2;

        if (this.automaticWidth)
        {
            int width = this.totalWidth;

            if (this.hasMaxWidth)
            {
                width = Math.min(width, this.maxWidth);
            }

            this.setWidth(width);
        }
    }

    @Override
    public void updateHeight()
    {
        this.totalHeight = this.stringListRenderer.getTotalTextHeight() + this.padding.getTop() + this.padding.getBottom();
        this.totalHeight += this.getActiveBorderWidth() * 2;

        if (this.automaticHeight)
        {
            int height = this.totalHeight;

            if (this.hasMaxHeight)
            {
                height = Math.min(height, this.maxHeight);
            }

            this.setHeight(height);
        }
    }

    @Override
    protected int getBackgroundWidth(boolean hovered, ScreenContext ctx)
    {
        if (this.hasMaxWidth && ctx.isActiveScreen && hovered)
        {
            return this.totalWidth;
        }

        return super.getBackgroundWidth(hovered, ctx);
    }

    @Override
    protected int getBackgroundHeight(boolean hovered, ScreenContext ctx)
    {
        if (this.hasMaxHeight && ctx.isActiveScreen && hovered)
        {
            return this.totalHeight;
        }

        return super.getBackgroundHeight(hovered, ctx);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.visible)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);

            if (this.renderBackground == false &&
                this.useBackgroundForHoverOverflow &&
                this.stringListRenderer.hasClampedContent() &&
                this.isHoveredForRender(ctx))
            {
                z += 20;
                int width = this.totalWidth;
                int height = this.totalHeight;
                this.renderBackground(x, y, z, width, height, this.borderWidthHovered, this.backgroundColorHovered, ctx);
                this.renderBorder(x, y, z, width, height, this.borderWidthHovered, this.borderColorHovered, ctx);
            }
            else
            {
                super.renderAt(x, y, z, ctx);
            }

            int bw = this.getActiveBorderWidth();
            x += this.padding.getLeft() + bw;
            y += this.padding.getTop() + bw;

            this.stringListRenderer.renderAt(x, y, z, this.isHoveredForRender(ctx));
        }
    }
}

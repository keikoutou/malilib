package fi.dy.masa.malilib.gui.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.TextRenderUtils;
import fi.dy.masa.malilib.render.text.OrderedStringListFactory;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class InteractableWidget extends BaseWidget
{
    private static int nextWidgetId;

    private final int id;
    protected OrderedStringListFactory hoverInfoFactory = new OrderedStringListFactory();
    @Nullable protected EventListener clickListener;
    @Nullable protected HoverChecker renderHoverChecker;
    @Nullable protected Consumer<Runnable> taskQueue;
    @Nullable protected StyledTextLine text;
    protected boolean shouldReceiveOutsideClicks;
    protected boolean shouldReceiveOutsideScrolls;
    protected boolean textShadow = true;
    protected int defaultTextColor = 0xFFFFFFFF;
    protected int textOffsetX = 4;
    protected int textOffsetY;

    public InteractableWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        this.id = nextWidgetId++;
    }

    /**
     * Returns the unique(-ish) ID of this widget.
     * The ID is increment by one for each widget that is created (starting from 0 for each game launch).
     * This ID is mainly meant for things like identifying the top-most hovered widget.
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Sets a simple single-line text to be rendered in the widget,
     * without having to add a LabelWidget for it.
     */
    public void setText(@Nullable StyledTextLine text)
    {
        this.text = text;
    }

    /**
     * Sets a simple single-line text to be rendered in the widget,
     * without having to add a LabelWidget for it.
     * @param textOffsetX an x offset for the text. By default this is 4 pixels from the left edge.
     * @param textOffsetY an y offset for the text. Note: The text is already centered vertically,
     *                    this is an additional offset on top of that!
     */
    public void setText(@Nullable StyledTextLine text, int textOffsetX, int textOffsetY)
    {
        this.text = text;
        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;
    }

    public void setTaskQueue(@Nullable Consumer<Runnable> taskQueue)
    {
        this.taskQueue = taskQueue;
    }

    public void setRenderHoverChecker(@Nullable HoverChecker checker)
    {
        this.renderHoverChecker = checker;
    }

    /**
     * Schedules a task to run after any widget iterations are finished, to not cause CMEs
     * if the task needs to modify the data list or the widget list in some way.
     */
    protected void scheduleTask(Runnable task)
    {
        if (this.taskQueue != null)
        {
            this.taskQueue.accept(task);
        }
    }

    public void setClickListener(@Nullable EventListener listener)
    {
        this.clickListener = listener;
    }

    public void setShouldReceiveOutsideClicks(boolean shouldReceiveOutsideClicks)
    {
        this.shouldReceiveOutsideClicks = shouldReceiveOutsideClicks;
    }

    public void setShouldReceiveOutsideScrolls(boolean shouldReceiveOutsideScrolls)
    {
        this.shouldReceiveOutsideScrolls = shouldReceiveOutsideScrolls;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int x = this.getX();
        int y = this.getY();

        return mouseX >= x && mouseX < x + this.getWidth() &&
               mouseY >= y && mouseY < y + this.getHeight();
    }

    public boolean isHoveredForRender(ScreenContext ctx)
    {
        if (this.renderHoverChecker != null)
        {
            return this.renderHoverChecker.isHovered(ctx);
        }

        return ctx.hoveredWidgetId == this.getId() ||
               (ctx.isActiveScreen && this.isMouseOver(ctx.mouseX, ctx.mouseY));
    }

    public boolean getShouldReceiveOutsideClicks()
    {
        return this.shouldReceiveOutsideClicks;
    }

    public boolean getShouldReceiveOutsideScrolls()
    {
        return this.shouldReceiveOutsideScrolls;
    }

    public boolean tryMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getShouldReceiveOutsideClicks() || this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.clickListener != null)
        {
            this.clickListener.onEvent();
            return true;
        }

        return false;
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
    }

    public boolean tryMouseScroll(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.getShouldReceiveOutsideScrolls() || this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
        }

        return false;
    }

    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        return false;
    }

    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        return false;
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        return false;
    }

    /**
     * Returns true if this widget can be hovered (for hover info etc.) at the given point
     */
    public boolean canHoverAt(int mouseX, int mouseY, int mouseButton)
    {
        return true;
    }

    public boolean hasHoverText()
    {
        return this.hoverInfoFactory.hasNoStrings() == false;
    }

    public ImmutableList<StyledTextLine> getHoverText()
    {
        return this.hoverInfoFactory.getStyledLines();
    }

    public OrderedStringListFactory getHoverInfoFactory()
    {
        return this.hoverInfoFactory;
    }

    /**
     * <b>Note:</b> The strings should be localized already.
     */
    public void addHoverStrings(String... hoverStrings)
    {
        this.hoverInfoFactory.addStrings(Arrays.asList(hoverStrings));
    }

    /**
     * <b>Note:</b> The strings should be localized already.
     */
    public void addHoverStrings(List<String> hoverStrings)
    {
        this.hoverInfoFactory.addStrings(hoverStrings);
    }

    public void translateAndAddHoverString(String translationKey, Object... args)
    {
        this.hoverInfoFactory.addStrings(StringUtils.translate(translationKey, args));
    }

    public void translateAndAddHoverStrings(String... hoverStrings)
    {
        this.hoverInfoFactory.translateAndAddStrings(Arrays.asList(hoverStrings));
    }

    /**
     * Adds the provided hover string supplier, by using the provided key.<br>
     * The key can be used to remove this string provider later.<br>
     * <b>Note:</b> The returned strings should be localized already.
     */
    public void setHoverStringProvider(String key, Supplier<List<String>> supplier)
    {
        this.hoverInfoFactory.setStringListProvider(key, supplier);
    }

    /**
     * Adds the provided hover string supplier, by using the provided key.<br>
     * The key can be used to remove this string provider later.<br>
     * <b>Note:</b> The returned strings should be localized already.
     */
    public void setHoverStringProvider(String key, Supplier<List<String>> supplier, int priority)
    {
        this.hoverInfoFactory.setStringListProvider(key, supplier, priority);
    }

    /**
     * Adds the provided hover text line supplier, by using the provided key.<br>
     * The key can be used to remove this string provider later.<br>
     */
    public void setHoverTextLineProvider(String key, Function<List<StyledTextLine>, List<StyledTextLine>> supplier, int priority)
    {
        this.hoverInfoFactory.setTextLineProvider(key, supplier, priority);
    }

    public void updateHoverStrings()
    {
        this.hoverInfoFactory.updateList();
    }

    public List<BaseTextFieldWidget> getAllTextFields()
    {
        return Collections.emptyList();
    }

    protected int getTextPositionX(int x)
    {
        return x + this.textOffsetX;
    }

    protected int getTextPositionY(int y)
    {
        return y + this.getCenteredTextOffsetY() + this.textOffsetY;
    }

    protected void renderText(int x, int y, float z, ScreenContext ctx)
    {
        if (this.text != null)
        {
            x = this.getTextPositionX(x);
            y = this.getTextPositionY(y);

            this.renderTextLine(x, y, z + 0.1f, this.defaultTextColor, this.textShadow, ctx, this.text);
        }
    }

    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.renderText(x, y, z, ctx);
    }

    public boolean shouldRenderHoverInfo(ScreenContext ctx)
    {
        return this.getId() == ctx.hoveredWidgetId &&
               this.isHoveredForRender(ctx) &&
               this.canHoverAt(ctx.mouseX, ctx.mouseY, 0);
    }

    public void postRenderHovered(ScreenContext ctx)
    {
        if (this.hasHoverText() && this.shouldRenderHoverInfo(ctx))
        {
            TextRenderUtils.renderStyledHoverText(ctx.mouseX, ctx.mouseY, this.getZLevel() + 0.5f, this.getHoverText());
        }
    }

    @Nullable
    public InteractableWidget getTopHoveredWidget(int mouseX, int mouseY, @Nullable InteractableWidget highestFoundWidget)
    {
        if (this.isMouseOver(mouseX, mouseY) &&
            (highestFoundWidget == null || this.getZLevel() > highestFoundWidget.getZLevel()))
        {
            return this;
        }

        return highestFoundWidget;
    }

    @Nullable
    public static InteractableWidget getTopHoveredWidgetFromList(List<? extends InteractableWidget> widgets, int mouseX, int mouseY, @Nullable InteractableWidget highestFoundWidget)
    {
        if (widgets.isEmpty() == false)
        {
            for (InteractableWidget widget : widgets)
            {
                highestFoundWidget = widget.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
            }
        }

        return highestFoundWidget;
    }

    public interface HoverChecker
    {
        boolean isHovered(ScreenContext ctx);
    }
}

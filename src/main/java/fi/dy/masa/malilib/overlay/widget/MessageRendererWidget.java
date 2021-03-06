package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.overlay.message.Message;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.JsonUtils;

public class MessageRendererWidget extends InfoRendererWidget
{
    protected final List<Message> messages = new ArrayList<>();
    protected int messageGap = 3;
    protected int maxMessages = -1;

    public MessageRendererWidget()
    {
        super();

        this.isOverlay = true;
        this.shouldSerialize = true;
        this.renderBackground = true;

        this.padding.setAll(4, 6, 4, 6);
        this.setLineHeight(10);
        this.setMaxWidth(320);
    }

    public void clearMessages()
    {
        this.messages.clear();
        this.updateSizeAndPosition();
    }

    public void addMessage(int defaultColor, int displayTimeMs, int fadeTimeMs, String translationKey, Object... args)
    {
        if (this.maxMessages > 0 && this.messages.size() >= this.maxMessages)
        {
            this.messages.remove(0);
        }

        int width = this.getMaxMessageWidth();
        this.messages.add(new Message(defaultColor, displayTimeMs, fadeTimeMs, width, translationKey, args));
        this.updateSizeAndPosition();
    }

    protected int getMaxMessageWidth()
    {
        int baseWidth = this.automaticWidth ? this.maxWidth : this.getWidth();
        return baseWidth - this.getPadding().getHorizontalTotal();
    }

    public int getMessageGap()
    {
        return this.messageGap;
    }

    public void setMessageGap(int messageGap)
    {
        this.messageGap = messageGap;
    }

    /**
     * Sets the maximum number of concurrent messages to display.
     * Use -1 for no limit.
     */
    public void setMaxMessages(int maxMessages)
    {
        this.maxMessages = maxMessages;
    }

    @Override
    public void onAdded()
    {
        this.updateSizeAndPosition();
    }

    protected void updateSizeAndPosition()
    {
        this.updateWidth();
        this.updateHeight();
        this.updateWidgetPosition();
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = 0;

            for (Message msg : this.messages)
            {
                width = Math.max(width, msg.getWidth());
            }

            width += this.getPadding().getHorizontalTotal();

            // Don't shrink while there are active messages,
            // to prevent an annoying horizontal move of the messages
            if (width > this.getWidth() || this.messages.isEmpty())
            {
                this.setWidth(width);
            }
        }
    }

    @Override
    public void updateHeight()
    {
        this.setHeight(this.getMessagesHeight() + this.getPadding().getVerticalTotal());
    }

    protected int getMessagesHeight()
    {
        final int messageCount = this.messages.size();

        if (messageCount > 0)
        {
            int height = (messageCount - 1) * this.messageGap;

            for (Message message : this.messages)
            {
                height += message.getLineCount() * this.lineHeight;
            }

            return height - (this.lineHeight - TextRenderer.INSTANCE.getFontHeight());
        }

        return 0;
    }

    @Override
    protected void renderBackground(int x, int y, float z, ScreenContext ctx)
    {
        if (this.renderBackground && this.messages.isEmpty() == false)
        {
            ShapeRenderUtils.renderOutlinedRectangle(x, y, z, this.getWidth(), this.getHeight(), this.backgroundColor, this.borderColor);
        }
    }

    @Override
    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
        this.drawMessages(x, y, z, ctx);
    }

    public void drawMessages(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            x += this.getPadding().getLeft();
            y += this.getPadding().getTop();

            long currentTime = System.nanoTime();
            int countBefore = this.messages.size();

            for (int i = 0; i < this.messages.size(); ++i)
            {
                Message message = this.messages.get(i);

                if (message.hasExpired(currentTime))
                {
                    this.messages.remove(i);
                    --i;
                }
                else
                {
                    message.renderAt(x, y, z + 0.1f, this.lineHeight, currentTime, ctx);
                }

                // Always offset the position to prevent a flicker from the later
                // messages jumping over the fading message when it disappears,
                // before the entire widget gets resized and the messages possibly moving
                // (if the widget is bottom-aligned).
                y += message.getLineCount() * this.lineHeight + this.messageGap;
            }

            if (this.messages.size() != countBefore)
            {
                this.updateSizeAndPosition();
            }
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        obj.addProperty("msg_gap", this.messageGap);
        obj.addProperty("max_messages", this.maxMessages);
        obj.addProperty("width", this.getWidth());

        if (this.hasMaxWidth)
        {
            obj.addProperty("max_width", this.maxWidth);
        }

        if (this.automaticWidth)
        {
            obj.addProperty("width_auto", true);
        }

        if (this.renderAboveScreen)
        {
            obj.addProperty("above_screen", true);
        }

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.messageGap = JsonUtils.getIntegerOrDefault(obj, "msg_gap", this.messageGap);
        this.maxMessages = JsonUtils.getIntegerOrDefault(obj, "max_messages", this.maxMessages);
        this.renderAboveScreen = JsonUtils.getBooleanOrDefault(obj, "above_screen", this.renderAboveScreen);
        this.automaticWidth = JsonUtils.getBooleanOrDefault(obj, "width_auto", this.automaticWidth);
        this.setWidth(JsonUtils.getIntegerOrDefault(obj, "width", this.getWidth()));

        if (JsonUtils.hasInteger(obj, "max_width"))
        {
            this.setMaxWidth(JsonUtils.getInteger(obj, "max_width"));
        }
    }
}

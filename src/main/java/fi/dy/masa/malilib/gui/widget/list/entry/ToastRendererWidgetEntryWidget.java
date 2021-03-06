package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class ToastRendererWidgetEntryWidget extends BaseInfoRendererWidgetEntryWidget<ToastRendererWidget>
{
    public ToastRendererWidgetEntryWidget(int x, int y, int width, int height,
                                          int listIndex, int originalListIndex,
                                          ToastRendererWidget data,
                                          @Nullable DataListWidget<? extends ToastRendererWidget> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.canConfigure = true;
        this.canRemove = true;

        this.setText(StyledTextLine.translate("malilib.gui.hover.toast_renderer_entry_name",
                                              data.getName(), data.getScreenLocation().getDisplayName()));
    }
}

package fi.dy.masa.malilib.gui.config.indicator;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.EdgeIntEditScreen;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorIndicatorWidget;
import fi.dy.masa.malilib.gui.widget.DoubleEditWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.IntegerEditWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.ConfigStatusIndicatorEntryWidget;
import fi.dy.masa.malilib.overlay.InfoOverlay;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import fi.dy.masa.malilib.render.text.TextRenderSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigStatusIndicatorGroupEditScreen extends BaseListScreen<DataListWidget<BaseConfigStatusIndicatorWidget<?>>>
{
    protected final ConfigStatusIndicatorContainerWidget widget;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget backgroundLabelWidget;
    protected final LabelWidget oddBackgroundLabelWidget;
    protected final LabelWidget priorityLabelWidget;
    protected final LabelWidget lineHeightLabelWidget;
    protected final LabelWidget textScaleLabelWidget;
    protected final GenericButton groupEnabledToggleButton;
    protected final GenericButton backgroundEnabledToggleButton;
    protected final GenericButton oddEvenBackgroundToggleButton;
    protected final GenericButton renderNameToggleButton;
    protected final GenericButton marginEditButton;
    protected final GenericButton paddingEditButton;
    protected final GenericButton addConfigsButton;
    protected final ColorIndicatorWidget backgroundColorWidget;
    protected final ColorIndicatorWidget oddBackgroundColorWidget;
    protected final IntegerEditWidget priorityEditWidget;
    protected final IntegerEditWidget lineHeightEditWidget;
    protected final DoubleEditWidget textScaleEditWidget;
    protected final BaseTextFieldWidget nameTextField;

    public ConfigStatusIndicatorGroupEditScreen(ConfigStatusIndicatorContainerWidget widget)
    {
        super(10, 120, 20, 128);

        this.widget = widget;
        this.useTitleHierarchy = false;
        this.title = StringUtils.translate("malilib.gui.title.config_status_indicator_configuration");

        this.locationDropdownWidget = new DropDownListWidget<>(0, 0, -1, 16, 160, 10, ScreenLocation.VALUES,
                                                               OptionListConfigValue::getDisplayName, null);
        this.locationDropdownWidget.setSelectedEntry(widget.getScreenLocation());
        this.locationDropdownWidget.setSelectionListener(this::changeWidgetLocation);

        this.nameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.priorityLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.sort_index.colon");
        this.lineHeightLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.line_height.colon");
        this.backgroundLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.background.colon");
        this.textScaleLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.text_scale.colon");
        this.oddBackgroundLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.config_status_indicator.background_odd.colon");
        this.oddBackgroundLabelWidget.addHoverStrings(StringUtils.translate("malilib.label.config_status_indicator.background_odd.hover"));

        this.nameTextField = new BaseTextFieldWidget(0, 0, 160, 16, widget.getName());
        this.nameTextField.setListener(widget::setName);

        this.priorityEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getSortIndex(), -1000, 1000, widget::setSortIndex);
        this.lineHeightEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getLineHeight(), 6, 40, widget::setLineHeight);
        this.textScaleEditWidget = new DoubleEditWidget(0, 0, 72, 16, widget.getScale(), 0.25, 20, widget::setScale);

        this.addConfigsButton = GenericButton.simple(-1, 16, "malilib.gui.button.add_configs", this::openAddConfigsScreen, "malilib.gui.button.hover.add_configs_to_status_indicator_group");
        this.marginEditButton = GenericButton.simple(-1, 16, "malilib.label.margin", this::openMarginEditScreen);
        this.marginEditButton.setHoverStringProvider("tooltip", this.widget.getMargin()::getHoverTooltip);

        this.paddingEditButton = GenericButton.simple(-1, 16, "malilib.label.padding", this::openPaddingEditScreen);
        this.paddingEditButton.setHoverStringProvider("tooltip", this.widget.getPadding()::getHoverTooltip);

        final TextRenderSettings textSettings = widget.getTextSettings();
        this.groupEnabledToggleButton = OnOffButton.simpleSlider(16, widget::isEnabled, widget::toggleEnabled);
        this.backgroundEnabledToggleButton = OnOffButton.simpleSlider(16, textSettings::getUseBackground, textSettings::toggleUseBackground);
        this.oddEvenBackgroundToggleButton = OnOffButton.simpleSlider(16, textSettings::getUseOddEvenBackground, textSettings::toggleUseOddEvenBackground);
        this.oddEvenBackgroundToggleButton.addHoverStrings(StringUtils.translate("malilib.label.config_status_indicator.background_odd.hover"));
        this.renderNameToggleButton = OnOffButton.simpleSlider(16, widget::getRenderName, widget::toggleRenderName);

        this.backgroundColorWidget = new ColorIndicatorWidget(0, 0, 16, 16, textSettings::getBackgroundColor, textSettings::setBackgroundColor);
        this.oddBackgroundColorWidget = new ColorIndicatorWidget(0, 0, 16, 16, textSettings::getOddRowBackgroundColor, textSettings::setOddRowBackgroundColor);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 24;
        int tmpX;

        this.marginEditButton.updateHoverStrings();
        this.paddingEditButton.updateHoverStrings();

        this.marginEditButton.updateHoverStrings();
        this.paddingEditButton.updateHoverStrings();

        this.locationDropdownWidget.setPosition(x, y);
        this.groupEnabledToggleButton.setPosition(this.locationDropdownWidget.getRight() + 6, y);

        this.marginEditButton.setPosition(this.groupEnabledToggleButton.getRight() + 6, y);
        this.paddingEditButton.setPosition(this.marginEditButton.getRight() + 6, y);

        y += 20;
        this.nameLabelWidget.setPosition(x, y + 4);
        this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);
        this.renderNameToggleButton.setPosition(this.nameTextField.getRight() + 6, y);

        y += 19;
        this.textScaleLabelWidget.setPosition(x, y + 4);
        this.lineHeightLabelWidget.setPosition(x, y + 23);
        this.priorityLabelWidget.setPosition(x, y + 42);

        tmpX = Math.max(this.textScaleLabelWidget.getRight(), this.lineHeightLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.priorityLabelWidget.getRight()) + 6;
        this.textScaleEditWidget.setPosition(tmpX, y);
        this.lineHeightEditWidget.setPosition(tmpX, y + 19);
        this.priorityEditWidget.setPosition(tmpX, y + 38);

        tmpX = this.textScaleEditWidget.getRight() + 6;
        this.backgroundLabelWidget.setPosition(tmpX, y + 4);
        this.oddBackgroundLabelWidget.setPosition(tmpX, y + 23);

        tmpX = Math.max(this.backgroundLabelWidget.getRight(), this.oddBackgroundLabelWidget.getRight()) + 6;
        this.backgroundColorWidget.setPosition(tmpX, y);
        this.oddBackgroundColorWidget.setPosition(tmpX, y + 19);

        tmpX += 22;
        this.backgroundEnabledToggleButton.setPosition(tmpX, y);
        this.oddEvenBackgroundToggleButton.setPosition(tmpX, y + 19);

        tmpX = this.x + this.screenWidth - this.addConfigsButton.getWidth() - 9;
        this.addConfigsButton.setPosition(tmpX, y + 38);

        this.addWidget(this.locationDropdownWidget);
        this.addWidget(this.groupEnabledToggleButton);
        this.addWidget(this.marginEditButton);
        this.addWidget(this.paddingEditButton);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.renderNameToggleButton);

        this.addWidget(this.textScaleLabelWidget);
        this.addWidget(this.textScaleEditWidget);

        this.addWidget(this.lineHeightLabelWidget);
        this.addWidget(this.lineHeightEditWidget);

        this.addWidget(this.priorityLabelWidget);
        this.addWidget(this.priorityEditWidget);

        this.addWidget(this.backgroundLabelWidget);
        this.addWidget(this.backgroundColorWidget);
        this.addWidget(this.backgroundEnabledToggleButton);

        this.addWidget(this.oddBackgroundLabelWidget);
        this.addWidget(this.oddBackgroundColorWidget);
        this.addWidget(this.oddEvenBackgroundToggleButton);

        this.addWidget(this.addConfigsButton);

        this.getListWidget().refreshEntries();
    }

    @Override
    public void onGuiClosed()
    {
        this.widget.setStatusIndicatorWidgets(this.getListWidget().getCurrentContents());

        super.onGuiClosed();
    }

    protected void changeWidgetLocation(ScreenLocation location)
    {
        InfoOverlay.INSTANCE.getOrCreateInfoArea(this.widget.getScreenLocation()).removeWidget(this.widget);
        // This also sets the location in the widget
        InfoOverlay.INSTANCE.getOrCreateInfoArea(location).addWidget(this.widget);
    }

    protected void openAddConfigsScreen()
    {
        ConfigStatusIndicatorGroupAddConfigsScreen screen = new ConfigStatusIndicatorGroupAddConfigsScreen(this.widget);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    protected void openMarginEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getMargin(), false,
                                                         "malilib.gui.title.edit_margin", "malilib.label.margin");
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    protected void openPaddingEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getPadding(), false,
                                                         "malilib.gui.title.edit_padding", "malilib.label.padding");
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    @Nullable
    @Override
    protected DataListWidget<BaseConfigStatusIndicatorWidget<?>> createListWidget(int listX, int listY,
                                                                                  int listWidth, int listHeight)
    {
        DataListWidget<BaseConfigStatusIndicatorWidget<?>> listWidget 
                = new DataListWidget<>(listX, listY, listWidth, listHeight, this.widget::getStatusIndicatorWidgetsForEditScreen);
        listWidget.setEntryWidgetFactory((wx, wy, ww, wh, li, oi, d, lw) ->
                                         new ConfigStatusIndicatorEntryWidget(wx, wy, ww, wh, li, oi,
                                                                              d, lw, this.widget));
        listWidget.setNormalBorderWidth(1);
        listWidget.setListEntryWidgetFixedHeight(16);
        listWidget.setFetchFromSupplierOnRefresh(true);

        return listWidget;
    }
}

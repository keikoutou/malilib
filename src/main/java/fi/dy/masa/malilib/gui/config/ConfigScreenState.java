package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;

public class ConfigScreenState
{
    @Nullable public ConfigTab currentTab;
    public int currentTabStartIndex;

    public ConfigScreenState(@Nullable ConfigTab currentTab)
    {
        this.currentTab = currentTab;
    }
}

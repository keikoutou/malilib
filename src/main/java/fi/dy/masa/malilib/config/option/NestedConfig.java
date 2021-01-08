package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigInfo;

/**
 * This is a wrapper used for the config screens, to embed the nesting level information
 * of configs contained within a ConfigGroup, without having to add that data to the ConfigInfo interface.
 */
public class NestedConfig implements ConfigInfo
{
    protected final ConfigInfo config;
    protected final int nestingLevel;

    public NestedConfig(ConfigInfo config, int nestingLevel)
    {
        this.config = config;
        this.nestingLevel = nestingLevel;
    }

    public ConfigInfo getConfig()
    {
        return this.config;
    }

    public int getNestingLevel()
    {
        return this.nestingLevel;
    }

    @Override
    public String getName()
    {
        return this.config.getName();
    }

    @Override
    public String getDisplayName()
    {
        return this.config.getDisplayName();
    }

    @Nullable
    @Override
    public String getComment()
    {
        return this.config.getComment();
    }

    @Override
    public boolean isModified()
    {
        return this.config.isModified();
    }

    @Override
    public void resetToDefault()
    {
        this.config.resetToDefault();
    }
}

package fi.dy.masa.malilib.config;

import java.io.File;
import java.util.List;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class BaseModConfig implements ModConfig
{
    protected final ModInfo modInfo;
    protected final String configFileName;
    protected final List<ConfigOptionCategory> configOptionCategories;
    protected final int currentConfigVersion;
    protected String backupDirectoryName = "config_backups";
    protected int backupCount = MaLiLibConfigs.Generic.CONFIG_BACKUP_COUNT.getIntegerValue();
    protected int savedConfigVersion;
    protected boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

    public BaseModConfig(ModInfo modInfo, String configFileName, int currentConfigVersion,
                         List<ConfigOptionCategory> configOptionCategories)
    {
        this.modInfo = modInfo;
        this.configFileName = configFileName;
        this.configOptionCategories = configOptionCategories;
        this.currentConfigVersion = currentConfigVersion;
    }

    @Override
    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    public String getConfigFileName()
    {
        return this.configFileName;
    }

    @Override
    public List<ConfigOptionCategory> getConfigOptionCategories()
    {
        return this.configOptionCategories;
    }

    @Override
    public int getConfigVersion()
    {
        return this.currentConfigVersion;
    }

    protected File getConfigBackupDirectory(File configDirectory)
    {
        return new File(configDirectory, this.backupDirectoryName);
    }

    /**
     * Sets the maximum number of backup copies to keep of the config file
     */
    public void setBackupCount(int backupCount)
    {
        this.backupCount = backupCount;
    }

    /**
     * A convenience method to create the default/recommended mod config for the current platform.
     * On 1.13+ Forge this will be TomlModConfig, in all other cases it will be JsonModConfig.
     */
    public static ModConfig createDefaultModConfig(ModInfo modInfo, int configVersion,
                                                   List<ConfigOptionCategory> configOptionCategories)
    {
        return new JsonModConfig(modInfo, configVersion, configOptionCategories);
    }
}

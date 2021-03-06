package fi.dy.masa.malilib.config.category;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.util.data.NameIdentifiable;

public interface ConfigOptionCategory extends NameIdentifiable
{
    /**
     * Returns whether or not the configs in this category should be saved to file
     * @return
     */
    boolean shouldSaveToFile();

    /**
     * Returns the list of config options in this category.
     * This list is used in the config saving and loading methods,
     * and also for checking if some values are dirty and the config
     * should be saved to file again.
     * @return
     */
    List<? extends ConfigOption<?>> getConfigOptions();
}

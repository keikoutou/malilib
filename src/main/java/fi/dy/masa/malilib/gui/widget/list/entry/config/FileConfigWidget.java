package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.FileSelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;

public class FileConfigWidget extends BaseConfigOptionWidget<File, FileConfig>
{
    public FileConfigWidget(int x, int y, int width, int height, int listIndex,
                            int originalListIndex, FileConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        File file = this.config.getValue().getAbsoluteFile();
        final File rootDir = new File("/");
        final File dir = file == null || file.isDirectory() == false ? (file != null ? file.getParentFile() : rootDir) : file;

        FileSelectorScreenFactory factory = () -> new FileSelectorScreen(dir, rootDir, (d) -> {
            this.config.setValueFromString(d.getAbsolutePath());
            this.reAddSubWidgets();
        });

        this.createFileSelectorWidgets(this.getY(), this.config, factory,
                                       "malilib.gui.button.config.select_file",
                                       "malilib.gui.button.config.hover.selected_file");
    }
}

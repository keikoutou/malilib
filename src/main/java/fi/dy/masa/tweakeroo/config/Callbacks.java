package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.tweakeroo.config.KeybindMulti.KeyAction;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.PlacementTweaks.FastMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class Callbacks
{
    public static final IFeatureCallback FEATURE_CALLBACK_GAMMA = new FeatureCallbackGamma(FeatureToggle.TWEAK_GAMMA_OVERRIDE, Minecraft.getMinecraft());

    public static void init()
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.setCallback(Callbacks.FEATURE_CALLBACK_GAMMA);

        Minecraft mc = Minecraft.getMinecraft();

        IHotkeyCallback callback = new KeyCallbackHotkeys(mc);
        IHotkeyCallback callbackMessage = new KeyCallbackHotkeyWithMessage(mc);

        Hotkeys.FAST_MODE_PLANE.getKeybind().setCallback(callback);
        Hotkeys.FAST_MODE_FACE.getKeybind().setCallback(callback);
        Hotkeys.FAST_MODE_COLUMN.getKeybind().setCallback(callback);
        Hotkeys.HOTBAR_SWAP_1.getKeybind().setCallback(callback);
        Hotkeys.HOTBAR_SWAP_2.getKeybind().setCallback(callback);
        Hotkeys.HOTBAR_SWAP_3.getKeybind().setCallback(callback);

        Hotkeys.SKIP_RENDERING.getKeybind().setCallback(callbackMessage);

        FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().setCallback(new KeyCallbackToggleAfterClicker(FeatureToggle.TWEAK_AFTER_CLICKER, mc));
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeybind().setCallback(new KeyCallbackToggleFastMode(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT, mc));
    }

    public static class FeatureCallbackGamma implements IFeatureCallback
    {
        private final Minecraft mc;
        private float originalGamma;

        public FeatureCallbackGamma(FeatureToggle feature, Minecraft mc)
        {
            this.mc = mc;
            this.originalGamma = this.mc.gameSettings.gammaSetting;

            // If the feature is enabled on game launch, apply it here
            if (feature.getBooleanValue())
            {
                this.mc.gameSettings.gammaSetting = ConfigsGeneric.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
        }

        @Override
        public void onValueChange(FeatureToggle feature)
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (feature.getBooleanValue())
            {
                this.originalGamma = mc.gameSettings.gammaSetting;
                mc.gameSettings.gammaSetting = ConfigsGeneric.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
            else
            {
                mc.gameSettings.gammaSetting = this.originalGamma;
            }
        }
    }

    public static class KeyCallbackHotkeyWithMessage implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeyWithMessage(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                if (key == Hotkeys.SKIP_RENDERING.getKeybind())
                {
                    this.mc.skipRenderWorld = ! this.mc.skipRenderWorld;

                    String pre = mc.skipRenderWorld ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
                    String status = I18n.format("tweakeroo.message.value." + (this.mc.skipRenderWorld ? "on" : "off"));
                    String message = I18n.format("tweakeroo.message.toggled", "Skip rendering", pre + status + TextFormatting.RESET);
                    printMessage(this.mc, message);
                }
            }
        }
    }

    public static class KeyCallbackToggleFeatureWithMessage implements IHotkeyCallback
    {
        protected final FeatureToggle feature;

        public KeyCallbackToggleFeatureWithMessage(FeatureToggle feature)
        {
            this.feature = feature;
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                this.feature.setBooleanValue(this.feature.getBooleanValue() == false);

                final boolean enabled = this.feature.getBooleanValue();
                String pre = enabled ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
                String status = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String message = I18n.format("tweakeroo.message.toggled", this.feature.getToggleMessage(), pre + status + TextFormatting.RESET);
                printMessage(Minecraft.getMinecraft(), message);
            }
        }
    }

    private static class KeyCallbackHotkeys implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeys(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                if (key == Hotkeys.HOTBAR_SWAP_1.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 0);
                }
                else if (key == Hotkeys.HOTBAR_SWAP_2.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 1);
                }
                else if (key == Hotkeys.HOTBAR_SWAP_3.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 2);
                }
                // The values will be toggled after the callback (see above), thus inversed check here
                else if (key == Hotkeys.FAST_MODE_PLANE.getKeybind())
                {
                    PlacementTweaks.setFastPlacementMode(FastMode.PLANE);
                }
                else if (key == Hotkeys.FAST_MODE_FACE.getKeybind())
                {
                    PlacementTweaks.setFastPlacementMode(FastMode.FACE);
                }
                else if (key == Hotkeys.FAST_MODE_COLUMN.getKeybind())
                {
                    PlacementTweaks.setFastPlacementMode(FastMode.COLUMN);
                }
            }
        }
    }

    private static class KeyCallbackToggleFastMode implements IHotkeyCallback
    {
        private final FeatureToggle feature;
        private final Minecraft mc;

        private KeyCallbackToggleFastMode(FeatureToggle feature, Minecraft mc)
        {
            this.feature = feature;
            this.mc = mc;
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                this.feature.setBooleanValue(this.feature.getBooleanValue() == false);

                boolean enabled = this.feature.getBooleanValue();
                String strStatus = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String preGreen = TextFormatting.GREEN.toString();
                String preRed = TextFormatting.RED.toString();
                String rst = TextFormatting.RESET.toString();
                strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

                if (enabled)
                {
                    String strMode = PlacementTweaks.getFastPlacementMode().name();
                    printMessage(this.mc, "tweakeroo.message.toggled_fast_placement_mode_on", strStatus, preGreen + strMode + rst);
                }
                else
                {
                    printMessage(this.mc, "tweakeroo.message.toggled", this.feature.getToggleMessage(), strStatus);
                }
            }
        }
    }

    private static class KeyCallbackToggleAfterClicker implements IHotkeyCallback
    {
        private final FeatureToggle feature;
        private final Minecraft mc;

        private KeyCallbackToggleAfterClicker(FeatureToggle feature, Minecraft mc)
        {
            this.feature = feature;
            this.mc = mc;
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                this.feature.setBooleanValue(this.feature.getBooleanValue() == false);

                boolean enabled = this.feature.getBooleanValue();
                String strStatus = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String preGreen = TextFormatting.GREEN.toString();
                String preRed = TextFormatting.RED.toString();
                String rst = TextFormatting.RESET.toString();
                strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

                if (enabled)
                {
                    String strValue = ConfigsGeneric.AFTER_CLICKER_CLICK_COUNT.getStringValue();
                    printMessage(this.mc, "tweakeroo.message.toggled_after_clicker_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    printMessage(this.mc, "tweakeroo.message.toggled", this.feature.getToggleMessage(), strStatus);
                }
            }
        }
    }

    public static void printMessage(Minecraft mc, String key, Object... args)
    {
        mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }
}

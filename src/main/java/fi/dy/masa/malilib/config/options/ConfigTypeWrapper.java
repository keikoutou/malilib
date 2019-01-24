package fi.dy.masa.malilib.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigDouble;
import fi.dy.masa.malilib.config.IConfigInteger;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.IConfigNotifiable;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;

public class ConfigTypeWrapper implements IConfigBoolean, IConfigDouble, IConfigInteger, IConfigOptionList, IHotkey, IConfigNotifiable
{
    private final ConfigType wrappedType;
    private final IConfigBase wrappedConfig;
    
    public ConfigTypeWrapper(ConfigType wrappedType, IConfigBase wrappedConfig)
    {
        this.wrappedType = wrappedType;
        this.wrappedConfig = wrappedConfig;
    }

    @Override
    public ConfigType getType()
    {
        return this.wrappedType;
    }

    @Override
    public String getName()
    {
        return this.wrappedConfig.getName();
    }

    @Override
    public String getComment()
    {
        return this.wrappedConfig.getComment();
    }

    @Override
    public String getPrettyName()
    {
        return this.wrappedConfig.getPrettyName();
    }

    @Override
    public void onValueChanged()
    {
        if (this.wrappedConfig instanceof IConfigNotifiable)
        {
            ((IConfigNotifiable) this.wrappedConfig).onValueChanged();
        }
    }

    @Override
    public void setValueChangeCallback(IValueChangeCallback callback)
    {
        if (this.wrappedConfig instanceof IConfigNotifiable)
        {
            ((IConfigNotifiable) this.wrappedConfig).setValueChangeCallback(callback);
        }
    }

    @Override
    public String getStringValue()
    {
        switch (this.wrappedType)
        {
            case BOOLEAN:       return String.valueOf(((IConfigBoolean) this.wrappedConfig).getBooleanValue());
            case DOUBLE:        return String.valueOf(((IConfigDouble) this.wrappedConfig).getDoubleValue());
            case INTEGER:       return String.valueOf(((IConfigInteger) this.wrappedConfig).getIntegerValue());
            case COLOR:         return String.format("#%08X", ((IConfigInteger) this.wrappedConfig).getIntegerValue());
            case OPTION_LIST:   return ((IConfigOptionList) this.wrappedConfig).getOptionListValue().getStringValue();
            case HOTKEY:        return ((IHotkey) this.wrappedConfig).getKeybind().getStringValue();
            case STRING:
            default:            return ((IStringRepresentable) this.wrappedConfig).getStringValue();
        }
    }

    @Override
    public String getDefaultStringValue()
    {
        switch (this.wrappedType)
        {
            case BOOLEAN:       return String.valueOf(((IConfigBoolean) this.wrappedConfig).getDefaultBooleanValue());
            case DOUBLE:        return String.valueOf(((IConfigDouble) this.wrappedConfig).getDefaultDoubleValue());
            case INTEGER:       return String.valueOf(((IConfigInteger) this.wrappedConfig).getDefaultIntegerValue());
            case COLOR:         return String.format("#%08X", ((IConfigInteger) this.wrappedConfig).getDefaultIntegerValue());
            case OPTION_LIST:   return ((IConfigOptionList) this.wrappedConfig).getDefaultOptionListValue().getStringValue();
            case HOTKEY:        return ((IHotkey) this.wrappedConfig).getKeybind().getDefaultStringValue();
            case STRING:
            default:            return ((IStringRepresentable) this.wrappedConfig).getDefaultStringValue();
        }
    }

    @Override
    public void setValueFromString(String value)
    {
        try
        {
            switch (this.wrappedType)
            {
                case HOTKEY:
                    ((IHotkey) this.wrappedConfig).getKeybind().setValueFromString(value);
                    break;
                case BOOLEAN:
                    ((IConfigBoolean) this.wrappedConfig).setBooleanValue(Boolean.parseBoolean(value));
                    break;
                case DOUBLE:
                    ((IConfigDouble) this.wrappedConfig).setDoubleValue(Double.parseDouble(value));
                    break;
                case INTEGER:
                    ((IConfigInteger) this.wrappedConfig).setIntegerValue(Integer.parseInt(value));
                    break;
                case STRING:
                    ((IStringRepresentable) this.wrappedConfig).setValueFromString(value);
                    break;
                case COLOR:
                    ((IConfigInteger) this.wrappedConfig).setValueFromString(value);
                    break;
                case OPTION_LIST:
                    IConfigOptionList option = (IConfigOptionList) this.wrappedConfig;
                    option.setOptionListValue(option.getOptionListValue().fromString(value));
                    break;
                default:
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    @Override
    public boolean isModified()
    {
        switch (this.wrappedType)
        {
            case HOTKEY:
                return ((IHotkey) this.wrappedConfig).getKeybind().isModified();
            case BOOLEAN:
            {
                IConfigBoolean config = (IConfigBoolean) this.wrappedConfig;
                return config.getBooleanValue() != config.getDefaultBooleanValue();
            }
            case DOUBLE:
            {
                IConfigDouble config = (IConfigDouble) this.wrappedConfig;
                return config.getDoubleValue() != config.getDefaultDoubleValue();
            }
            case INTEGER:
            case COLOR:
            {
                IConfigInteger config = (IConfigInteger) this.wrappedConfig;
                return config.getIntegerValue() != config.getDefaultIntegerValue();
            }
            case OPTION_LIST:
            {
                IConfigOptionList config = (IConfigOptionList) this.wrappedConfig;
                return config.getOptionListValue() != config.getDefaultOptionListValue();
            }
            case STRING:
            {
                IStringRepresentable config = (IStringRepresentable) this.wrappedConfig;
                return config.getStringValue().equals(config.getDefaultStringValue()) == false;
            }
            default:
                return false;
        }
    }

    @Override
    public boolean isModified(String newValue)
    {
        switch (this.wrappedType)
        {
            case HOTKEY:
                return ((IHotkey) this.wrappedConfig).getKeybind().isModified(newValue);
            case BOOLEAN:
                return String.valueOf(((IConfigBoolean) this.wrappedConfig).getBooleanValue()).equals(newValue) == false;
            case DOUBLE:
                return String.valueOf(((IConfigDouble) this.wrappedConfig).getDoubleValue()).equals(newValue) == false;
            case INTEGER:
                return String.valueOf(((IConfigInteger) this.wrappedConfig).getIntegerValue()).equals(newValue) == false;
            case COLOR:
                return ((ConfigColor) this.wrappedConfig).getStringValue().equals(newValue) == false;
            case OPTION_LIST:
                return ((IConfigOptionList) this.wrappedConfig).getOptionListValue().getStringValue().equals(newValue) == false;
            case STRING:
            default:
                return ((IStringRepresentable) this.wrappedConfig).getStringValue().equals(newValue) == false;
        }
    }

    @Override
    public void resetToDefault()
    {
        try
        {
            switch (this.wrappedType)
            {
                case HOTKEY:
                    ((IHotkey) this.wrappedConfig).getKeybind().resetToDefault();
                    break;
                case BOOLEAN:
                {
                    IConfigBoolean config = (IConfigBoolean) this.wrappedConfig;
                    config.setBooleanValue(config.getDefaultBooleanValue());
                    break;
                }
                case DOUBLE:
                {
                    IConfigDouble config = (IConfigDouble) this.wrappedConfig;
                    config.setDoubleValue(config.getDefaultDoubleValue());
                    break;
                }
                case INTEGER:
                case COLOR:
                {
                    IConfigInteger config = (IConfigInteger) this.wrappedConfig;
                    config.setIntegerValue(config.getDefaultIntegerValue());
                    break;
                }
                case OPTION_LIST:
                {
                    IConfigOptionList config = (IConfigOptionList) this.wrappedConfig;
                    config.setOptionListValue(config.getDefaultOptionListValue());
                    break;
                }
                case STRING:
                default:
                {
                    IStringRepresentable config = (IStringRepresentable) this.wrappedConfig;
                    config.setValueFromString(config.getDefaultStringValue());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to reset config value for {}", this.getName(), e);
        }
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.wrappedType == ConfigType.BOOLEAN ? ((IConfigBoolean) this.wrappedConfig).getBooleanValue() : false;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.wrappedType == ConfigType.BOOLEAN ? ((IConfigBoolean) this.wrappedConfig).getDefaultBooleanValue() : false;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        if (this.wrappedType == ConfigType.BOOLEAN)
        {
            ((IConfigBoolean) this.wrappedConfig).setBooleanValue(value);
        }
    }

    @Override
    public int getIntegerValue()
    {
        return this.wrappedType == ConfigType.INTEGER ? ((IConfigInteger) this.wrappedConfig).getIntegerValue() : 0;
    }

    @Override
    public int getDefaultIntegerValue()
    {
        return this.wrappedType == ConfigType.INTEGER ? ((IConfigInteger) this.wrappedConfig).getDefaultIntegerValue() : 0;
    }

    @Override
    public void setIntegerValue(int value)
    {
        if (this.wrappedType == ConfigType.INTEGER)
        {
            ((IConfigInteger) this.wrappedConfig).setIntegerValue(value);
        }
    }

    @Override
    public double getDoubleValue()
    {
        return this.wrappedType == ConfigType.DOUBLE ? ((IConfigDouble) this.wrappedConfig).getDoubleValue() : 0;
    }

    @Override
    public double getDefaultDoubleValue()
    {
        return this.wrappedType == ConfigType.DOUBLE ? ((IConfigDouble) this.wrappedConfig).getDefaultDoubleValue() : 0;
    }

    @Override
    public void setDoubleValue(double value)
    {
        if (this.wrappedType == ConfigType.DOUBLE)
        {
            ((IConfigDouble) this.wrappedConfig).setDoubleValue(value);
        }
    }

    @Override
    public IConfigOptionListEntry getOptionListValue()
    {
        return this.wrappedType == ConfigType.OPTION_LIST ? ((IConfigOptionList) this.wrappedConfig).getOptionListValue() : null;
    }

    @Override
    public IConfigOptionListEntry getDefaultOptionListValue()
    {
        return this.wrappedType == ConfigType.OPTION_LIST ? ((IConfigOptionList) this.wrappedConfig).getDefaultOptionListValue() : null;
    }

    @Override
    public void setOptionListValue(IConfigOptionListEntry value)
    {
        if (this.wrappedType == ConfigType.OPTION_LIST)
        {
            ((IConfigOptionList) this.wrappedConfig).setOptionListValue(value);
        }
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.wrappedType == ConfigType.HOTKEY ? ((IHotkey) this.wrappedConfig).getKeybind() : null;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            switch (this.wrappedType)
            {
                case BOOLEAN:
                    ((IConfigBoolean) this.wrappedConfig).setBooleanValue(element.getAsBoolean());
                    break;
                case DOUBLE:
                    ((IConfigDouble) this.wrappedConfig).setDoubleValue(element.getAsDouble());
                    break;
                case INTEGER:
                    ((IConfigInteger) this.wrappedConfig).setIntegerValue(element.getAsInt());
                    break;
                case STRING:
                    ((IConfigValue) this.wrappedConfig).setValueFromString(element.getAsString());
                    break;
                case COLOR:
                    ((IConfigInteger) this.wrappedConfig).setValueFromString(element.getAsString());
                    break;
                case OPTION_LIST:
                    IConfigOptionList option = (IConfigOptionList) this.wrappedConfig;
                    option.setOptionListValue(option.getOptionListValue().fromString(element.getAsString()));
                    break;
                case HOTKEY:
                    ((IHotkey) this.wrappedConfig).getKeybind().setValueFromString(element.getAsString());
                    break;
                default:
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        switch (this.wrappedType)
        {
            case BOOLEAN:
                return new JsonPrimitive(((IConfigBoolean) this.wrappedConfig).getBooleanValue());
            case DOUBLE:
                return new JsonPrimitive(((IConfigDouble) this.wrappedConfig).getDoubleValue());
            case INTEGER:
                return new JsonPrimitive(((IConfigInteger) this.wrappedConfig).getIntegerValue());
            case STRING:
                return new JsonPrimitive(((IConfigValue) this.wrappedConfig).getStringValue());
            case COLOR:
                return new JsonPrimitive(((IConfigInteger) this.wrappedConfig).getStringValue());
            case OPTION_LIST:
                return new JsonPrimitive(((IConfigOptionList) this.wrappedConfig).getOptionListValue().getStringValue());
            case HOTKEY:
                return new JsonPrimitive(((IHotkey) this.wrappedConfig).getKeybind().getStringValue());
            default:
                return new JsonPrimitive(this.getStringValue());
        }
    }
}
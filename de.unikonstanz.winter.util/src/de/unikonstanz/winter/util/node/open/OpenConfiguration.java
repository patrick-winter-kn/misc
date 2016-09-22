package de.unikonstanz.winter.util.node.open;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class OpenConfiguration {
	
	private static final String CFG_ARGUMENT = "argument";
	
	private static final String CFG_REPLACEMENT_COLUMN = "replacementColumn";
	
	private static final String CFG_USE_CUSTOM_COMMAND = "useCustomCommand";
	
	private static final String CFG_CUSTOM_COMMAND = "customCommand";
	
	private static final String CFG_USE_SIZE_LIMIT = "useSizeLimit";
	
	private static final String CFG_SIZE_LIMIT = "sizeLimit";
	
	private static final String DEFAULT_ARGUMENT = "https://www.google.com/webhp?ie=UTF-8#q=$";
	
	private static final String DEFAULT_REPLACEMENT_COLUMN = "";
	
	private static final boolean DEFAULT_USE_CUSTOM_COMMAND = false;
	
	private static final String DEFAULT_CUSTOM_COMMAND = "";
	
	private static final boolean DEFAULT_USE_SIZE_LIMIT = true;
	
	private static final int DEFAULT_SIZE_LIMIT = 5;
	
	private String m_argument = DEFAULT_ARGUMENT;
	
	private String m_replacementColumn = DEFAULT_REPLACEMENT_COLUMN;
	
	private boolean m_useCustomCommand = DEFAULT_USE_CUSTOM_COMMAND;
	
	private String m_customCommand = DEFAULT_CUSTOM_COMMAND;
	
	private boolean m_useSizeLimit = DEFAULT_USE_SIZE_LIMIT;
	
	private int m_sizeLimit = DEFAULT_SIZE_LIMIT;
	
    /**
     * @param settings The settings to load from
     * @throws InvalidSettingsException If the settings are invalid
     */
    public void loadInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
    	m_argument = settings.getString(CFG_ARGUMENT);
    	m_replacementColumn = settings.getString(CFG_REPLACEMENT_COLUMN);
    	m_useCustomCommand = settings.getBoolean(CFG_USE_CUSTOM_COMMAND);
    	m_customCommand = settings.getString(CFG_CUSTOM_COMMAND);
    	m_useSizeLimit = settings.getBoolean(CFG_USE_SIZE_LIMIT);
    	m_sizeLimit = settings.getInt(CFG_SIZE_LIMIT);
    }

    /**
     * @param settings The settings to load from
     */
    public void loadInDialog(final NodeSettingsRO settings) {
    	m_argument = settings.getString(CFG_ARGUMENT, DEFAULT_ARGUMENT);
    	m_replacementColumn = settings.getString(CFG_REPLACEMENT_COLUMN, DEFAULT_REPLACEMENT_COLUMN);
    	m_useCustomCommand = settings.getBoolean(CFG_USE_CUSTOM_COMMAND, DEFAULT_USE_CUSTOM_COMMAND);
    	m_customCommand = settings.getString(CFG_CUSTOM_COMMAND, DEFAULT_CUSTOM_COMMAND);
    	m_useSizeLimit = settings.getBoolean(CFG_USE_SIZE_LIMIT, DEFAULT_USE_SIZE_LIMIT);
    	m_sizeLimit = settings.getInt(CFG_SIZE_LIMIT, DEFAULT_SIZE_LIMIT);
    }

    /**
     * @param settings The settings to save to
     */
    public void save(final NodeSettingsWO settings) {
    	settings.addString(CFG_ARGUMENT, m_argument);
    	settings.addString(CFG_REPLACEMENT_COLUMN, m_replacementColumn);
    	settings.addBoolean(CFG_USE_CUSTOM_COMMAND, m_useCustomCommand);
    	settings.addString(CFG_CUSTOM_COMMAND, m_customCommand);
    	settings.addBoolean(CFG_USE_SIZE_LIMIT, m_useSizeLimit);
    	settings.addInt(CFG_SIZE_LIMIT, m_sizeLimit);
    }
    
    public String getArgument() {
		return m_argument;
	}
    
    public void setArgument(final String argument) {
		this.m_argument = argument;
	}
    
    public String getReplacementColumn() {
		return m_replacementColumn;
	}
    
    public void setReplacementColumn(final String replacementColumn) {
		this.m_replacementColumn = replacementColumn;
	}

    public boolean getUseCustomCommand() {
		return m_useCustomCommand;
	}
    
    public void setUseCustomCommand(final boolean useCustomCommand) {
    	m_useCustomCommand = useCustomCommand;
    }
    
    public String getCustomCommand() {
    	return m_customCommand;
    }
    
    public void setCustomCommand(final String customCommand) {
    	m_customCommand = customCommand;
    }
    
    public boolean getUseSizeLimit() {
    	return m_useSizeLimit;
    }
    
    public void setUseSizeLimit(final boolean useSizeLimit) {
    	m_useSizeLimit = useSizeLimit;
    }
    
    public int getSizeLimit() {
    	return m_sizeLimit;
    }
    
    public void setSizeLimit(final int sizeLimit) {
    	m_sizeLimit = sizeLimit;
    }
    
}

/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2011
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement n° FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse.ui.editor.preferences;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.grammaticalframework.eclipse.GFPreferences;
import org.grammaticalframework.eclipse.ui.GFUiModule;
import org.grammaticalframework.eclipse.ui.internal.GFActivator;

// TODO: Auto-generated Javadoc
/**
 * The Class GFPreferenceInitializer.
 */
public class GFPreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		
		// Set defaults from environment variables
		IPreferenceStore store = GFActivator.getInstance().getPreferenceStore();
		
		try {
			store.setDefault(GFPreferences.GF_BIN_PATH, System.getenv("HOME") + "/.cabal/bin/gf");
		} catch (SecurityException _) {	}
		
		try {
			store.setDefault(GFPreferences.GF_LIB_PATH, System.getenv("GF_LIB_PATH"));
		} catch (SecurityException _) {	}
		
		store.setDefault(GFPreferences.LOG_LEVEL, "INFO");
		
		// Listener for changing logging  level as needed
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(GFPreferences.LOG_LEVEL)) {
					GFUiModule.log.setLevel(Level.toLevel(event.getNewValue().toString(), Level.INFO));
				}
			}
		});
	}

}
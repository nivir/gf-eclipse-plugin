package org.grammaticalframework.eclipse.ui.editor.preferences;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.grammaticalframework.eclipse.GFPreferences;
import org.grammaticalframework.eclipse.ui.GFUiModule;
import org.grammaticalframework.eclipse.ui.internal.GFActivator;

public class GFPreferenceInitializer extends AbstractPreferenceInitializer {

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
		store.setDefault(GFPreferences.SHOW_DEBUG, true);
		
		// Listener for changing logging  level as needed
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(GFPreferences.SHOW_DEBUG)) {
					if ((Boolean)event.getNewValue()) {
						GFUiModule.log.setLevel(Level.DEBUG);
					} else {
						GFUiModule.log.setLevel(Level.ERROR);
					}
				}
			}
		});
	}

}

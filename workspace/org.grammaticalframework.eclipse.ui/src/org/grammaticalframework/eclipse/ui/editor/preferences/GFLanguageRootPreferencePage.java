package org.grammaticalframework.eclipse.ui.editor.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.xtext.ui.editor.preferences.LanguageRootPreferencePage;
import org.grammaticalframework.eclipse.GFPreferences;
import org.grammaticalframework.eclipse.ui.internal.GFActivator;

public class GFLanguageRootPreferencePage extends LanguageRootPreferencePage {
	
	private static final String PAGE_DESCRIPTION = "Settings for GF development."; //$NON-NLS-1$
	
	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(GFPreferences.GF_BIN_PATH, "&Runtime path:", getFieldEditorParent()));
		addField(new StringFieldEditor(GFPreferences.GF_LIB_PATH, "&Library path:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(GFPreferences.SHOW_DEBUG, "&Show debug messages", getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(GFActivator.getInstance().getPreferenceStore());
		setDescription(PAGE_DESCRIPTION);
	}
	
}

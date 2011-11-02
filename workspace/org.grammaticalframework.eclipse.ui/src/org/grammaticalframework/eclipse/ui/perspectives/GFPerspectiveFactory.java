package org.grammaticalframework.eclipse.ui.perspectives;

import java.io.PrintStream;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.MessageConsoleStream;
import org.grammaticalframework.eclipse.launch.GFLaunchConfigurationDelegate;

/**
 * Ref: http://www.eclipsepluginsite.com/perspectives.html
 * 
 * @author John J. Camilleri
 * 
 */
public class GFPerspectiveFactory implements IPerspectiveFactory {
	
	public void createInitialLayout(IPageLayout layout) {
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		// topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		//bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

		// Bottom right: Console
		// See also: http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM, 0.66f, editorArea);
		
		// Add Run/debug buttons
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		
		// Create console
		createConsole();
	}
	
	public static MessageConsoleStream createConsole() {
		MessageConsoleStream mcs = GFConsole.getStream();
		GFLaunchConfigurationDelegate.setConsole(new PrintStream( GFConsole.getStream() ));
		return mcs;
	}
	
}

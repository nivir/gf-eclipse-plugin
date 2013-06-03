/*
 * generated by Xtext
 */
package org.grammaticalframework.eclipse.ui;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

import org.grammaticalframework.eclipse.ui.internal.GFActivator;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class GFExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return GFActivator.getInstance().getBundle();
	}
	
	@Override
	protected Injector getInjector() {
		return GFActivator.getInstance().getInjector(GFActivator.ORG_GRAMMATICALFRAMEWORK_ECLIPSE_GF);
	}
	
}

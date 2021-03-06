/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2012
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement no. FP7-ICT-247914.
 */

package org.grammaticalframework.eclipse;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class GFStandaloneSetup extends GFStandaloneSetupGenerated{

	public static void doSetup() {
		new GFStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}


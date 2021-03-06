/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2012
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement no. FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse.launch;

/**
 * Launch Config preferences
 */
public interface IGFLaunchConfigConstants {
	
	public static String GF_LAUNCH_CONFIG_TYPE_ID = "org.grammaticalframework.eclipse.GFLaunchConfigurationType";
	
	static String prefix = "org.grammaticalframework.eclipse.launch.config.";

	public static String DEFAULT_OPTIONS = ""; //"--force-recomp";
	public static String DEFAULT_TREEBANK_COMMAND = "l -table";
	
	/**
	 * Working directory for running GF
	 */
	public static final String WORKING_DIR = prefix + "WORKING_DIR";
	
	/**
	 * Files to pass to the compiler
	 */
	public static final String FILENAMES = prefix + "FILENAMES";

	/**
	 * Command line flags
	 */
	public static final String OPTIONS = prefix + "OPTIONS";
	
	/**
	 * Arbitrary commands
	 */
	public static final String COMMANDS = prefix + "COMMANDS";
	
	/**
	 * Run in interactive mode (mutually exclusive with batch mode)
	 */
	public static final String INTERACTIVE_MODE = prefix + "INTERACTIVE_MODE";
	
	/**
	 * Run in batch mode (mutually exclusive with interactive mode)
	 */
	public static final String BATCH_MODE = prefix + "BATCH_MODE";
	
	/**
	 * Run in treebank mode (when in batch mode)
	 */
	public static final String TREEBANK_MODE = prefix + "TREEBANK_MODE";
	
	/**
	 * Treebank test type
	 */
	public static final String TREEBANK_TYPE_LINEARIZE = prefix + "TREEBANK_TYPE_LINEARIZE";
	
	/**
	 * Flags for the treebank command (e.g. -table)
	 */
	public static final String TREEBANK_COMMAND_FLAGS = prefix + "TREEBANK_COMMAND";
	
	/**
	 * Name of treebank file
	 */
	public static final String TREEBANK_FILENAME = prefix + "TREEBANK_FILENAME";
	
	/**
	 * Hidden options for making a treebank file
	 */
	public static final String MAKE_TREEBANK = prefix + "MAKE_TREEBANK";
	public static final String MAKE_TREEBANK_NAME = prefix + "MAKE_TREEBANK_NAME";
	public static final String MAKE_TREEBANK_COMMAND = prefix + "MAKE_TREEBANK_COMMAND";
	
	/**
	 * Hidden option for making a gold standard file
	 */
	public static final String MAKE_GOLD_STANDARD = prefix + "MAKE_GOLD_STANDARD";
	
}

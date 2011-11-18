package org.grammaticalframework.eclipse.scoping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractScope;
import org.grammaticalframework.eclipse.gF.GFPackage;
import com.google.inject.Inject;

public class GFTagBasedScope extends AbstractScope {

	/**
	 * Qualified name converter
	 */
	@Inject
	private IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

	/**
	 * The logger
	 */
	private static final Logger log = Logger.getLogger(GFTagBasedGlobalScopeProvider.class);

	/**
	 * The name of the module this scope represents
	 */
	private final String moduleName;
	
	/**
	 * The object descriptions.
	 */
	private final ArrayList<IEObjectDescription> descriptions;
	
	/**
	 * Blank constructor
	 * 
	 * @param parent
	 * @param ignoreCase
	 */
	protected GFTagBasedScope(IScope parent, String moduleName, boolean ignoreCase) {
		super(parent==null ? IScope.NULLSCOPE : parent, ignoreCase);
		this.moduleName = moduleName;
		descriptions = new ArrayList<IEObjectDescription>();
	}
	
	/**
	 * Constructs a new scope with the given descriptions
	 * 
	 * @param parent
	 * @param ignoreCase
	 */
//	protected GFTagBasedScope(IScope parent, IResourceDescription resourceDescription, boolean ignoreCase) {
//		super(parent==null ? IScope.NULLSCOPE : parent, ignoreCase);
//		moduleName = resourceDescription.getURI().lastSegment().substring(0, resourceDescription.getURI().lastSegment().lastIndexOf('.'));
//		descriptions = new ArrayList<IEObjectDescription>();
//		for (IEObjectDescription desc : resourceDescription.getExportedObjects()) {
////			QualifiedName newName = getUnQualifiedName(desc.getQualifiedName()); // Strip off qualified bit of name (maybe)
////			descriptions.add(EObjectDescription.create(newName, desc.getEObjectOrProxy()));
//			descriptions.add(desc);
//		}
//	}
	
	/**
	 * Add the collection of tags to the scope. The resource descriptions are used for looking up the corresponding EObjects
	 * 
	 * @param resourceDescriptions
	 * @param tags
	 */
	public void addTags(IResourceDescriptions resourceDescriptions, Collection<TagEntry> tags) {
		for (TagEntry tag : tags) {
			try {
				QualifiedName fullyQualifiedName = converter.toQualifiedName(tag.getQualifiedName());
				QualifiedName trueQualifiedName = converter.toQualifiedName(tag.getTrueQualifiedName());
				QualifiedName unQualifiedName = getUnQualifiedName(trueQualifiedName);
				Map<String, String> userData = tag.getProperties();
				
				IEObjectDescription eObjectDescription = null;
				
				// First try trueQualifiedName
				Iterable<IEObjectDescription> matchingEObjects1 = resourceDescriptions.getExportedObjects(GFPackage.Literals.IDENT, trueQualifiedName, false);
				Iterator<IEObjectDescription> iter1 = matchingEObjects1.iterator();
				if (iter1.hasNext()) { // This just always chooses first occurance... is that bad?
					eObjectDescription = iter1.next();
//				} else {
//					// If not, then try fullyQualifiedName
//					Iterable<IEObjectDescription> matchingEObjects2 = resourceDescriptions.getExportedObjects(GFPackage.Literals.IDENT, fullyQualifiedName, false);
//					Iterator<IEObjectDescription> iter2 = matchingEObjects2.iterator();
//					if (iter2.hasNext()) {
//						eObjectDescription = iter2.next();
//					}
				}
				
				// Did we find anything?
				if (eObjectDescription != null) {
					// Duplicate the object description, so that we can edit the qualified name and add the user data
					IEObjectDescription eObjectDescription2 = new EObjectDescription(fullyQualifiedName, eObjectDescription.getEObjectOrProxy(), userData);
					descriptions.add(eObjectDescription2);
				}
				else {
					log.debug("No EObject found for " + tag.getQualifiedName());
				}
				
				
			} catch (IllegalStateException _) {
				// Sometimes happens when you save during a build/validation, etc. Qisu ma ġara xejn. 
			}
		}
	}
	

	private QualifiedName getUnQualifiedName(QualifiedName qn) {
		return qn.skipFirst(qn.getSegmentCount()-1);
	}
	
	@Override
	protected Iterable<IEObjectDescription> getAllLocalElements() {
		return descriptions;
	}

	protected int localElementCount() {
		return descriptions.size();
	}

	protected String getModuleName() {
		return moduleName;
	}

}
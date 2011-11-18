/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2011
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement n° FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse.scoping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.grammaticalframework.eclipse.GFException;
import org.grammaticalframework.eclipse.gF.ModDef;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Global scope provider is responsible for defining what is visible from
 * outside the current resource, for any given reference.
 * 
 * In our case, this means considering;
 * - Anything exended/inherited in this resource (remember inheritance is transitive)
 * - Anything opened in this resource
 * - If this is a concrete module, anything in its abstract
 * (where "this" means the resource in which the reference is defined)
 * 
 */

public class GFTagBasedGlobalScopeProvider extends AbstractGlobalScopeProvider {
	
	/**
	 * The logger
	 */
	private static final Logger log = Logger.getLogger(GFTagBasedGlobalScopeProvider.class);

	/**
	 * Instantiates a new gF global scope provider.
	 */
	public GFTagBasedGlobalScopeProvider() {
		super();
	}
	
	@Inject
	private GFLibraryAgent libAgent;
	
	@Inject
	private ExtensibleURIConverterImpl uriConverter; 
	
	@Inject
	private IResourceScopeCache cache;
	
	public void setCache(IResourceScopeCache cache) {
		this.cache = cache;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.xtext.scoping.impl.AbstractGlobalScopeProvider#getScope(org.eclipse.emf.ecore.resource.Resource, boolean, org.eclipse.emf.ecore.EClass, com.google.common.base.Predicate)
	 */
	@Override
	protected IScope getScope(Resource resource, boolean ignoreCase, EClass type, Predicate<IEObjectDescription> filter) {
		
		// Do the parsing, possibly hitting the cache
		Map<URI, Collection<TagEntry>> uriTagMap = getURITagMap(resource);
		
//		/* ----- Method 1: Just use the URIs ----- */
//		// Load all descriptions from all mentioned files/URIs
//		Set<URI> uniqueImportURIs = uriTagMap.keySet();
//		IResourceDescriptions resourceDescriptions = getResourceDescriptions(resource, uniqueImportURIs);
//
//		// Add everything from all the URIs mentioned in the tags file
//		IScope scope = IScope.NULLSCOPE;
//		for (IResourceDescription resDesc : resourceDescriptions.getAllResourceDescriptions()) {
//			GFTagBasedScope newScope = new GFTagBasedScope(scope, resDesc, ignoreCase);
//			if (newScope.localElementCount() > 0)
//				scope = newScope;
//		}
//		
//		return scope;

		/* ----- Method 2: Use the tags themselves ----- */
		try {
			GFTagBasedScope gfScope = null;
			IResourceDescriptions resourceDescriptions = getResourceDescriptions(resource, uriTagMap.keySet());
			for (Map.Entry<URI, Collection<TagEntry>> entry : uriTagMap.entrySet()) {
				
				// Get module name from URI
				String lastSegment = entry.getKey().lastSegment();
				int dotIx = lastSegment.lastIndexOf('.');
				String moduleName = (dotIx > 0)	? lastSegment.substring(0, dotIx) : lastSegment;

				// Append new scope for the current module/uri
				gfScope = new GFTagBasedScope(gfScope, moduleName, ignoreCase);
				gfScope.addTags(resourceDescriptions, entry.getValue());
			}
			return (gfScope == null) ? IScope.NULLSCOPE : gfScope;
		} catch (NullPointerException _) {
			return IScope.NULLSCOPE;
		}
	}
	
	/**
	 * Get the import URIs for a source file, possibly from cache
	 * @param resource
	 * @return
	 */
//	private Set<URI> getImportedURIs(final Resource resource) {
//		return cache.get(GFTagBasedGlobalScopeProvider.class.getName(), resource, new Provider<Set<URI>>(){
//			public Set<URI> get() {
//				return parseTagsFile(resource).keySet();
//			}
//		});
//	}
	
	/**
	 * Get list of all tags as a one-dimensional list, possibly from cache
	 * @param resource
	 * @return
	 */
//	private Collection<TagEntry> getTags(final Resource resource) {
//		return cache.get(GFTagBasedGlobalScopeProvider.class.getName(), resource, new Provider<Collection<TagEntry>>(){
//			public Collection<TagEntry> get() {
//				Collection<Collection<TagEntry>> tags2D = parseTagsFile(resource).values(); 
//				Collection<TagEntry> tags1D = new ArrayList<TagEntry>(); 
//				for (Collection<TagEntry> tagsItem : tags2D) {
//					tags1D.addAll(tagsItem);
//				}
//				return tags1D;
//			}
//		});
//	}
	
	/**
	 * Get list of all tags grouped by URI, possibly from cache
	 * 
	 * TODO Make sure this cache is being flushed correctly.
	 * 
	 * @param resource
	 * @return
	 */
	private Hashtable<URI, Collection<TagEntry>> getURITagMap(final Resource resource) {
		return cache.get(GFTagBasedGlobalScopeProvider.class.getName(), resource, new Provider<Hashtable<URI, Collection<TagEntry>>>(){
			public Hashtable<URI, Collection<TagEntry>> get() {
				return parseTagsFile(resource); 
			}
		});
	}
	
	/**
	 * For a given resource, find its tags file and get a list of the all the files mentioned
	 * there, and return them as a list of unique URIs
	 * 
	 * @param resource
	 * @return
	 */
	private Hashtable<URI, Collection<TagEntry>> parseTagsFile(final Resource resource) {
		
		// Get module definition
		ModDef moduleDef;
		String moduleName;
		try {
			moduleDef = (ModDef)resource.getContents().get(0);
			moduleName = moduleDef.getType().getName().getS();
		} catch (Exception _) {
			// This means there's a mother syntax error (mid-way during editing). Just return quietly.
			return new Hashtable<URI, Collection<TagEntry>>();
		}
		
		// Find the corresponding tags file & parse it (1st pass)
		URI tagFileURI = libAgent.getTagsFile(resource, moduleName);
		Predicate<TagEntry> includePredicate = new Predicate<TagEntry>() {
			// Ignore references to self, ie local scope
			public boolean apply(TagEntry tag) {
				return !tag.getFile().endsWith(resource.getURI().lastSegment());
			}
		};
		Hashtable<URI, Collection<TagEntry>> uriTagMap = parseSingleTagsFile(tagFileURI, includePredicate, null);
		
		// Iterate again to replace references to indir tags files with proper references (2nd pass)
		Hashtable<URI, Collection<TagEntry>> resolvedUriTagMap = new Hashtable<URI, Collection<TagEntry>>(uriTagMap.size());
//		Iterator<URI> uriIter = uriTagMap.keySet().iterator();
		Iterator<Entry<URI, Collection<TagEntry>>> uriIter = uriTagMap.entrySet().iterator();
		
		while(uriIter.hasNext()) {
			Entry<URI, Collection<TagEntry>> iterEntry = uriIter.next();
			URI uri = iterEntry.getKey();
			Collection<TagEntry> tagList = iterEntry.getValue();
			
			// Just remove invalid URIs
			if (!EcoreUtil2.isValidUri(resource, uri)) {
				uriIter.remove();
				log.warn("Removed invalid URI: " + uri);
			}
			// Resolve refs to other tags files and replace, but making sure to keep original qualifier & alias
			else if (uri.fileExtension().equals("gf-tags")) {
				
				// Super low-tech solution: iterate over all tags, capture all the different Qualifier/Alias combos
//				ArrayListMultimap<String, String> qualifierAliasCombos = ArrayListMultimap.create();
//				for (TagEntry tag : tagList) {
//					if (!qualifierAliasCombos.containsEntry(tag.getQualifier(), tag.getAlias()))
//						qualifierAliasCombos.put(tag.getQualifier(), tag.getAlias());
//				}
				HashSet<String> qualifiers = new HashSet<String>();
				for (TagEntry tag : tagList) {
					qualifiers.add(tag.getQualifier());
					qualifiers.add(tag.getAlias()); // this also allows for empty aliases! i.e. when inheriting
				}
				
//				for (Map.Entry<String, String> qaCombo : qualifierAliasCombos.entries()) {
//					final String qualifier = qaCombo.getKey();
//					final String alias = qaCombo.getValue();
//					
				Iterator<String> qIter = qualifiers.iterator();
				while (qIter.hasNext()) {
					final String qualifier = qIter.next();
					final String alias = "doesnt-matter"; // TODO Clean up this!
				
				// Get the qualifier and alias from the first entry
				// (since everything from a given module is qualified in the same way, this should be safe)
//				TagEntry tagSpecimen = uriTagMap.get(uri).iterator().next(); 
//				final String qualifier = tagSpecimen.getQualifier();
//				final String alias = tagSpecimen.getAlias();

					Predicate<TagEntry> includePredicate2 = new Predicate<TagEntry>() {
						// Only include tags FROM the respective tags file (opposite of above)
						public boolean apply(TagEntry tag) {
	//						return tag.getFile().endsWith(uri.lastSegment().substring(0, uri.lastSegment().length()-5));
							return !tag.getFile().endsWith(".gf-tags") && !tag.getType().equals("overload-type") ;
						}
					};
					Function<TagEntry, TagEntry> customFunction = new Function<TagEntry, TagEntry>() {
						public TagEntry apply(TagEntry from) {
							from.setQualifier(qualifier);
							from.setAlias(alias);
							return from; // return value not actually used
						}
					};
					Hashtable<URI, Collection<TagEntry>> newUriTagMap = parseSingleTagsFile(uri, includePredicate2, customFunction);
					
					// Make sure to add all new refs withouth overwriting
					for (Map.Entry<URI, Collection<TagEntry>> entry : newUriTagMap.entrySet()) {
						if (!resolvedUriTagMap.containsKey(entry.getKey()))
							resolvedUriTagMap.put(entry.getKey(), entry.getValue());
						else
							resolvedUriTagMap.get(entry.getKey()).addAll(entry.getValue());
					}
				}
				uriIter.remove();
			}
		}
		
		// Combine them & return
		uriTagMap.putAll(resolvedUriTagMap);
		return uriTagMap;
	}
	
	/**
	 * Parse the specified tags file, returning collections of {@link TagEntry}'s grouped by {@link URI}. 
	 * These URIs may point to other tags files, not necessarily to the original source.
	 * 
	 * @param tagFileURI The tag file to parse (using low-level file streams)
	 * @param includePredicate Predicate applied to tag returns a boolean, determining whether to include the tag or not
	 * @param customFunction Custom function for transforming each tag as required
	 * @return collections of tags grouped by URI
	 */
	private Hashtable<URI, Collection<TagEntry>> parseSingleTagsFile(URI tagFileURI, Predicate<TagEntry> includePredicate, Function<TagEntry, TagEntry> customFunction) {
		Hashtable<String, Collection<TagEntry>> strTagMap = new Hashtable<String, Collection<TagEntry>>();
		try {
			InputStream is = uriConverter.createInputStream(tagFileURI);
			BufferedReader reader = new BufferedReader( new InputStreamReader(is) );
			String line;
			while ((line = reader.readLine()) != null) {
				TagEntry tag;
				try {
					tag = new TagEntry(line);
				} catch (GFException e) {
					log.warn(e); // Would happen if the tags file is malformed somehow
					continue;
				}
				if (includePredicate != null && !includePredicate.apply(tag))
					continue;
				if (customFunction != null)
					customFunction.apply(tag);
				if (!strTagMap.containsKey(tag.getFile())) {
					strTagMap.put(tag.getFile(), new ArrayList<TagEntry>());
				}
				strTagMap.get(tag.getFile()).add(tag);
			}
			// Clean up
			reader.close();
			is.close();
		} catch (IOException e) {
			log.debug("Couldn't find tags file " + tagFileURI);
		}
		
		// Convert from String keys to URI keys (this is an optimisation thing)
		Hashtable<URI, Collection<TagEntry>> uriTagMap = new Hashtable<URI, Collection<TagEntry>>();
		for (Entry<String, Collection<TagEntry>> entry : strTagMap.entrySet()) {
			URI importURI = URI.createFileURI(entry.getKey());
			uriTagMap.put(importURI, entry.getValue());
		}
		return uriTagMap;
	}
	
	
	@Inject
	private Provider<LoadOnDemandResourceDescriptions> loadOnDemandDescriptions;
	
	/**
	 * Gets the descriptions of resources listed in importUris
	 *
	 * @param resource the resource
	 * @param importUris the import uris
	 * @return the resource descriptions
	 */
	private IResourceDescriptions getResourceDescriptions(Resource resource, Collection<URI> importUris) {
		IResourceDescriptions result = getResourceDescriptions(resource);
		LoadOnDemandResourceDescriptions demandResourceDescriptions = loadOnDemandDescriptions.get();
		demandResourceDescriptions.initialize(result, importUris, resource);
		return demandResourceDescriptions;
	}
	
}
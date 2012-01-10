/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2011
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement n° FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse.formatting;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.util.Pair;
import org.grammaticalframework.eclipse.services.GFGrammarAccess;

import com.google.inject.Inject;

/**
 * This class contains custom formatting description.
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an
 * example
 */
public class GFFormatter extends AbstractDeclarativeFormatter {

	protected IIndentationInformation getIndentInfo() {
		return indentInfo;
	}
	@Inject(optional = true)
	private IIndentationInformation indentInfo = new IIndentationInformation() {
		public String getIndentString() {
			return "  ";
		}
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter#configureFormatting(org.eclipse.xtext.formatting.impl.FormattingConfig)
	 */
	protected void configureFormatting(FormattingConfig c) {
		GFGrammarAccess f = (GFGrammarAccess) getGrammarAccess();
		
		// For general use
		Keyword[] ks;

		// Never auto-wrap lines
		c.setNoLinewrap();

		// Preserve newlines around comments
		c.setLinewrap(0, 1, 2).before(f.getSL_COMMENTRule());
		c.setLinewrap(0, 1, 2).before(f.getML_COMMENTRule());
		c.setLinewrap(0, 1, 1).after(f.getML_COMMENTRule());

		// Find common keywords and specify formatting for them
		for (Pair<Keyword, Keyword> pair : f.findKeywordPairs("(", ")")) {
			c.setNoSpace().after(pair.getFirst());
			c.setNoSpace().before(pair.getSecond());
		}
		for (Keyword period : f.findKeywords(".")) {
			c.setNoSpace().before(period);
			c.setNoSpace().after(period);
		}
		for (Keyword comma : f.findKeywords(",")) {
			c.setNoSpace().before(comma);
		}
		for (Keyword semicolon : f.findKeywords(";")) {
			c.setLinewrap().after(semicolon);
		}
//		for (Pair<Keyword, Keyword> pair : f.findKeywordPairs("{", "}")) {
//			c.setLinewrap().after(pair.getFirst());
//			c.setIndentationIncrement().after(pair.getFirst());
//			c.setIndentationDecrement().before(pair.getSecond());
//			c.setLinewrap().before(pair.getSecond());
//		}
		for (Pair<Keyword, Keyword> pair : f.findKeywordPairs("[", "]")) {
			c.setNoSpace().after(pair.getFirst());
			c.setNoSpace().before(pair.getSecond());
		}
		
		// For formatting module definitions..

		// Special case when using `open (Alias = Name)`
		Keyword k2 = f.getOpenAccess().getLeftParenthesisKeyword_2_0();
		c.setLinewrap().before(k2);
		c.setIndentationIncrement().before(k2);
		c.setIndentationDecrement().after(k2); // don't cascade the indentation!
		
		// "open"
		ks = new Keyword[] {
			f.getModBodyAccess().getOpenKeyword_0_2_0(),
			f.getModBodyAccess().getOpenKeyword_3_5_0(),
			f.getModBodyAccess().getOpenKeyword_5_7_0(),
		};
		for (Keyword k : ks) {
			c.setLinewrap().before(k);
			c.setIndentationIncrement().before(k);
			c.setIndentationDecrement().after(k); // don't cascade the indentation!
		}
		
		// Indentation within the ModDef's body
//		c.setIndentation(f.getModBodyAccess().getLeftCurlyBracketKeyword_0_3(), f.getModBodyAccess().getRightCurlyBracketKeyword_0_5());
//		c.setIndentation(f.getModBodyAccess().getLeftCurlyBracketKeyword_3_6(), f.getModBodyAccess().getRightCurlyBracketKeyword_3_8());
//		c.setIndentation(f.getModBodyAccess().getLeftCurlyBracketKeyword_5_8(), f.getModBodyAccess().getRightCurlyBracketKeyword_5_10());

		// Top def groupings (cat, fun, lin ....)
		ks = new Keyword[] {
			f.getTopDefAccess().getCatCatKeyword_0_0_0(),
			f.getTopDefAccess().getFunFunKeyword_1_0_0(),
			f.getTopDefAccess().getDataDataKeyword_2_0_0(),
			f.getTopDefAccess().getDefDefKeyword_3_0_0(),
			f.getTopDefAccess().getDataDataKeyword_4_0_0(),
			f.getTopDefAccess().getParamParamKeyword_5_0_0(),
			f.getTopDefAccess().getOperOperKeyword_6_0_0(),
			f.getTopDefAccess().getLincatLincatKeyword_7_0_0(),
			f.getTopDefAccess().getLindefLindefKeyword_8_0_0(),
			f.getTopDefAccess().getLinLinKeyword_9_0_0(),
			f.getTopDefAccess().getPrintnamePrintnameKeyword_10_0_0(),
			f.getTopDefAccess().getPrintnamePrintnameKeyword_11_0_0(),
			f.getTopDefAccess().getFlagsFlagsKeyword_12_0_0(),
		};
		for (Keyword k : ks) {
			c.setLinewrap(2).before(k);
			c.setLinewrap().after(k);
			c.setIndentationIncrement().after(k);
		}
		c.setIndentationDecrement().after( f.getTopDefRule() );
		
		// TODO Formatting for table
		// TODO Formatting for case
		// TODO Formatting for let
		
	}
}

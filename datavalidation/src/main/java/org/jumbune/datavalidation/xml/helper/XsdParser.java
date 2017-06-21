/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.parser.XSOMParser;



/**
 * @author vivek.shivhare
 * 
 */
public class XsdParser {

	File schema;

	private HashSet<XSElementDecl> globalelements = null;

	private HashSet<XSElementDecl> childelements = null;

	/**
	 * @param schema
	 */
	public XsdParser(File schema) {
		this.schema = schema;

		globalelements = new HashSet<XSElementDecl>();
		childelements = new HashSet<XSElementDecl>();
	}

	public String processSchema() {
		
		String rootelem = null;

		try {
			XSOMParser parser = new XSOMParser();
			parser.parse(this.schema);
			XSSchemaSet schemaSet = parser.getResult();
			XSSchema xsSchema = schemaSet.getSchema(1);

			Iterator<XSElementDecl> itre = schemaSet.iterateElementDecls();

			while (itre.hasNext()) {

				XSElementDecl xse = (XSElementDecl) itre.next();
				if (xse.isGlobal()) {
					XSComplexType xscomp = xse.getType().asComplexType();
					if (xscomp != null) {
						globalelements.add(xse);
					}
				}
			}

			for (XSElementDecl element : globalelements) {
				XSComplexType fxscomp = element.getType().asComplexType();
				XSContentType fxscont = fxscomp.getContentType();
				XSParticle fparticle = fxscont.asParticle();

				if (fparticle != null) {
					XSTerm fterm = fparticle.getTerm();

					if (fterm.isModelGroupDecl()) {

						XSModelGroupDecl fmodelGD = fterm.asModelGroupDecl();

						XSModelGroup fmodelG = fmodelGD.getModelGroup();
						XSParticle[] fparrs = fmodelG.getChildren();
						for (XSParticle fpar : fparrs) {
							if (fpar != null) {
								XSTerm fcterm = fpar.getTerm();
								if (fcterm.isElementDecl()) {
									XSComplexType fxxscomp = fcterm
											.asElementDecl().getType()
											.asComplexType();
									childelements.add(fcterm.asElementDecl());
									if (fxxscomp != null) {
										traverseChilds(fcterm);
									}

								} else {
									traverseChilds(fcterm);
								}
							}
						}
					} else if (fterm.isModelGroup()) {
						XSModelGroup smodel = fterm.asModelGroup();
						XSParticle[] sparrs = smodel.getChildren();
						for (XSParticle spar : sparrs) {
							if (spar != null) {
								XSTerm sterm = spar.getTerm();
								if (sterm.isElementDecl()) {
									childelements.add(sterm.asElementDecl());
									XSComplexType scomp = sterm.asElementDecl()
											.getType().asComplexType();
									if (scomp != null) {
										traverseChilds(sterm);
									}

								} else {
									traverseChilds(sterm);
								}

							}
						}
					} else {
						XSComplexType tcomp = fterm.asElementDecl().getType()
								.asComplexType();
						childelements.add(fterm.asElementDecl());
						if (tcomp != null) {
							traverseChilds(fterm);
						}
					}

				}
			}
			
			for(XSElementDecl element : childelements){
	        	globalelements.remove(element);
	        }
			
			if(globalelements.size() == 1){
				
				for(XSElementDecl root: globalelements){
					rootelem = root.getName();
				}
			}
			
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return rootelem;
	}

	private void traverseChilds(XSTerm rfTerm) {

		if (rfTerm.isModelGroupDecl()) {

			XSModelGroupDecl rfmodelGD = rfTerm.asModelGroupDecl();

			XSModelGroup rfmodelG = rfmodelGD.getModelGroup();
			XSParticle[] rfparrs = rfmodelG.getChildren();
			for (XSParticle rfpar : rfparrs) {
				XSTerm rfsterm = rfpar.getTerm();
				if (rfsterm.isElementDecl()) {
					XSComplexType rfcomp = rfsterm.asElementDecl().getType()
							.asComplexType();
					if (rfcomp != null) {
						traverseChilds(rfsterm);
					}

				} else {
					traverseChilds(rfsterm);
				}
			}
		} else if (rfTerm.isModelGroup()) {
			XSModelGroup rmodel = rfTerm.asModelGroup();
			XSParticle[] rparrs = rmodel.getChildren();
			for (XSParticle rpar : rparrs) {
				if (rpar != null) {
					XSTerm rterm = rpar.getTerm();
					if (rterm.isElementDecl()) {
						childelements.add(rterm.asElementDecl());
						XSComplexType rcomp = rterm.asElementDecl().getType()
								.asComplexType();
						if (rcomp != null) {
							traverseChilds(rterm);
						}
					} else {
						traverseChilds(rterm);
					}

				}
			}
		} else {
			XSComplexType rtcomp = rfTerm.asElementDecl().getType()
					.asComplexType();
			XSContentType rfxscont = rtcomp.getContentType();
			XSParticle rfparticle = rfxscont.asParticle();
			if (rfparticle != null) {
				XSTerm rsterm = rfparticle.getTerm();
				traverseChilds(rsterm);
			} else {
				childelements.add(rfTerm.asElementDecl());
			}
		}
	}

}

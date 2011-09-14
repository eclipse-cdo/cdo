/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Diagram;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Diagram</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class DiagramImpl extends BodyElementImpl implements Diagram
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DiagramImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.DIAGRAM;
  }

  public void generate(HtmlWriter out, StructuralElement linkSource) throws IOException
  {
    // TODO: implement DiagramImpl.generate(out, linkSource)
    throw new UnsupportedOperationException();
  }

} // DiagramImpl

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.ExternalTarget;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>External Target</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class ExternalTargetImpl extends LinkTargetImpl implements ExternalTarget
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ExternalTargetImpl()
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
    return ArticlePackage.Literals.EXTERNAL_TARGET;
  }

  @Override
  public String linkFrom(StructuralElement source)
  {
    // TODO: implement ExternalTargetImpl.linkFrom(source)
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getId()
  {
    // TODO: implement ExternalTargetImpl.getId()
    throw new UnsupportedOperationException();
  }

  @Override
  public String getLabel()
  {
    // TODO: implement ExternalTargetImpl.getLabel()
    throw new UnsupportedOperationException();
  }

} // ExternalTargetImpl

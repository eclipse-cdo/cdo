/**
 * <copyright>
 * </copyright>
 *
 * $Id: GenRefSingleNonContainedImpl.java,v 1.3 2008-09-18 12:56:49 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.model4Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Gen Ref Single Non Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.GenRefSingleNonContainedImpl#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class GenRefSingleNonContainedImpl extends CDOObjectImpl implements GenRefSingleNonContained
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected GenRefSingleNonContainedImpl()
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
    return model4Package.Literals.GEN_REF_SINGLE_NON_CONTAINED;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EObject getElement()
  {
    return (EObject)eGet(model4Package.Literals.GEN_REF_SINGLE_NON_CONTAINED__ELEMENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setElement(EObject newElement)
  {
    eSet(model4Package.Literals.GEN_REF_SINGLE_NON_CONTAINED__ELEMENT, newElement);
  }

} // GenRefSingleNonContainedImpl

/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefSingleNonContainedNPLImpl.java,v 1.3 2008-09-18 12:56:49 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.model4Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Ref Single Non Contained NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.RefSingleNonContainedNPLImpl#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RefSingleNonContainedNPLImpl extends CDOObjectImpl implements RefSingleNonContainedNPL
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected RefSingleNonContainedNPLImpl()
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
    return model4Package.Literals.REF_SINGLE_NON_CONTAINED_NPL;
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
  public ContainedElementNoOpposite getElement()
  {
    return (ContainedElementNoOpposite)eGet(model4Package.Literals.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setElement(ContainedElementNoOpposite newElement)
  {
    eSet(model4Package.Literals.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT, newElement);
  }

} // RefSingleNonContainedNPLImpl

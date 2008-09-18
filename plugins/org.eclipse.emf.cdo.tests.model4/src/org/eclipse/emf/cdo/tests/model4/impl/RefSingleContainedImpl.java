/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefSingleContainedImpl.java,v 1.3 2008-09-18 12:56:49 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Ref Single Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.RefSingleContainedImpl#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RefSingleContainedImpl extends CDOObjectImpl implements RefSingleContained
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected RefSingleContainedImpl()
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
    return model4Package.Literals.REF_SINGLE_CONTAINED;
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
  public SingleContainedElement getElement()
  {
    return (SingleContainedElement)eGet(model4Package.Literals.REF_SINGLE_CONTAINED__ELEMENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setElement(SingleContainedElement newElement)
  {
    eSet(model4Package.Literals.REF_SINGLE_CONTAINED__ELEMENT, newElement);
  }

} // RefSingleContainedImpl

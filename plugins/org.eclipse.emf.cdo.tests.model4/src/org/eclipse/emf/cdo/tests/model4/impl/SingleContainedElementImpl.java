/**
 * <copyright>
 * </copyright>
 *
 * $Id: SingleContainedElementImpl.java,v 1.3 2008-09-18 12:56:49 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Single Contained Element</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.SingleContainedElementImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.SingleContainedElementImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class SingleContainedElementImpl extends CDOObjectImpl implements SingleContainedElement
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected SingleContainedElementImpl()
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
    return model4Package.Literals.SINGLE_CONTAINED_ELEMENT;
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
  public String getName()
  {
    return (String)eGet(model4Package.Literals.SINGLE_CONTAINED_ELEMENT__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eSet(model4Package.Literals.SINGLE_CONTAINED_ELEMENT__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public RefSingleContained getParent()
  {
    return (RefSingleContained)eGet(model4Package.Literals.SINGLE_CONTAINED_ELEMENT__PARENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setParent(RefSingleContained newParent)
  {
    eSet(model4Package.Literals.SINGLE_CONTAINED_ELEMENT__PARENT, newParent);
  }

} // SingleContainedElementImpl

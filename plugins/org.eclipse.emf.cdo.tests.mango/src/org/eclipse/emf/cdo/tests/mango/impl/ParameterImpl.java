/**
 * <copyright>
 * </copyright>
 *
 * $Id: ParameterImpl.java,v 1.2 2008-09-18 12:56:15 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.mango.impl;

import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.mango.Parameter;
import org.eclipse.emf.cdo.tests.mango.ParameterPassing;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.mango.impl.ParameterImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.mango.impl.ParameterImpl#getPassing <em>Passing</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ParameterImpl extends CDOObjectImpl implements Parameter
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ParameterImpl()
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
    return MangoPackage.Literals.PARAMETER;
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
    return (String)eGet(MangoPackage.Literals.PARAMETER__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eSet(MangoPackage.Literals.PARAMETER__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ParameterPassing getPassing()
  {
    return (ParameterPassing)eGet(MangoPackage.Literals.PARAMETER__PASSING, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setPassing(ParameterPassing newPassing)
  {
    eSet(MangoPackage.Literals.PARAMETER__PASSING, newPassing);
  }

} // ParameterImpl

/**
 * <copyright>
 * </copyright>
 *
 * $Id: Class2Impl.java,v 1.2 2008-09-18 12:57:20 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3.subpackage.impl;

import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Class2</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.subpackage.impl.Class2Impl#getClass1 <em>Class1</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Class2Impl extends CDOObjectImpl implements Class2
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Class2Impl()
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
    return SubpackagePackage.Literals.CLASS2;
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
  @SuppressWarnings("unchecked")
  public EList<Class1> getClass1()
  {
    return (EList<Class1>)eGet(SubpackagePackage.Literals.CLASS2__CLASS1, true);
  }

} // Class2Impl

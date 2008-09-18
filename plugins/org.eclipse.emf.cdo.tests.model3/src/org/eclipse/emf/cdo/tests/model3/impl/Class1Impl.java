/**
 * <copyright>
 * </copyright>
 *
 * $Id: Class1Impl.java,v 1.2 2008-09-18 12:57:20 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Class1</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.impl.Class1Impl#getClass2 <em>Class2</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Class1Impl extends CDOObjectImpl implements Class1
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Class1Impl()
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
    return Model3Package.Literals.CLASS1;
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
  public EList<Class2> getClass2()
  {
    return (EList<Class2>)eGet(Model3Package.Literals.CLASS1__CLASS2, true);
  }

} // Class1Impl

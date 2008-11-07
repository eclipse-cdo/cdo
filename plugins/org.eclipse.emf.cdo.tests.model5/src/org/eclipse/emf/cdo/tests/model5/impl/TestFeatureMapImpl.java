/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestFeatureMapImpl.java,v 1.1 2008-11-07 02:50:07 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model5.impl;

import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.Model5Package;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Test Feature Map</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl#getManagers <em>Managers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl#getDoctors <em>Doctors</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl#getPeople <em>People</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TestFeatureMapImpl extends CDOObjectImpl implements TestFeatureMap
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TestFeatureMapImpl()
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
    return Model5Package.Literals.TEST_FEATURE_MAP;
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
  public EList<Manager> getManagers()
  {
    return (EList<Manager>)eGet(Model5Package.Literals.TEST_FEATURE_MAP__MANAGERS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Doctor> getDoctors()
  {
    return (EList<Doctor>)eGet(Model5Package.Literals.TEST_FEATURE_MAP__DOCTORS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public FeatureMap getPeople()
  {
    return (FeatureMap)eGet(Model5Package.Literals.TEST_FEATURE_MAP__PEOPLE, true);
  }

} // TestFeatureMapImpl

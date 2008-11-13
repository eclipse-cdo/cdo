/**
 * <copyright>
 * </copyright>
 *
 * $Id: GenListOfIntegerImpl.java,v 1.1 2008-11-13 14:48:45 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model5.impl;

import org.eclipse.emf.cdo.tests.model5.GenListOfInteger;
import org.eclipse.emf.cdo.tests.model5.Model5Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Gen List Of Integer</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntegerImpl#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class GenListOfIntegerImpl extends CDOObjectImpl implements GenListOfInteger
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected GenListOfIntegerImpl()
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
    return Model5Package.Literals.GEN_LIST_OF_INTEGER;
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
  public EList<Integer> getElements()
  {
    return (EList<Integer>)eGet(Model5Package.Literals.GEN_LIST_OF_INTEGER__ELEMENTS, true);
  }

} // GenListOfIntegerImpl

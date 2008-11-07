/**
 * <copyright>
 * </copyright>
 *
 * $Id: GenListOfIntImpl.java,v 1.1 2008-11-07 02:50:07 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model5.impl;

import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.Model5Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Gen List Of Int</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class GenListOfIntImpl extends CDOObjectImpl implements GenListOfInt
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected GenListOfIntImpl()
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
    return Model5Package.Literals.GEN_LIST_OF_INT;
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
    return (EList<Integer>)eGet(Model5Package.Literals.GEN_LIST_OF_INT__ELEMENTS, true);
  }

} // GenListOfIntImpl

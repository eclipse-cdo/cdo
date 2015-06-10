/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.impl;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>AClass</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl#getSubClasses <em>Sub Classes</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl#getImplementedInterfaces <em>Implemented
 * Interfaces</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl#getAssociations <em>Associations</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl#getCompositions <em>Compositions</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl#getAggregations <em>Aggregations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AClassImpl extends ABasicClassImpl implements AClass
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected AClassImpl()
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
    return AcorePackage.Literals.ACLASS;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AClass> getSubClasses()
  {
    return (EList<AClass>)eGet(AcorePackage.Literals.ACLASS__SUB_CLASSES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AInterface> getImplementedInterfaces()
  {
    return (EList<AInterface>)eGet(AcorePackage.Literals.ACLASS__IMPLEMENTED_INTERFACES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AClass> getAssociations()
  {
    return (EList<AClass>)eGet(AcorePackage.Literals.ACLASS__ASSOCIATIONS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AClass> getCompositions()
  {
    return (EList<AClass>)eGet(AcorePackage.Literals.ACLASS__COMPOSITIONS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AClass> getAggregations()
  {
    return (EList<AClass>)eGet(AcorePackage.Literals.ACLASS__AGGREGATIONS, true);
  }

} // AClassImpl

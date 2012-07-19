/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>ACore Root</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ACoreRootImpl#getTitle <em>Title</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ACoreRootImpl#getClasses <em>Classes</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ACoreRootImpl#getInterfaces <em>Interfaces</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ACoreRootImpl extends CDOObjectImpl implements ACoreRoot
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ACoreRootImpl()
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
    return AcorePackage.Literals.ACORE_ROOT;
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
  public String getTitle()
  {
    return (String)eGet(AcorePackage.Literals.ACORE_ROOT__TITLE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setTitle(String newTitle)
  {
    eSet(AcorePackage.Literals.ACORE_ROOT__TITLE, newTitle);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AClass> getClasses()
  {
    return (EList<AClass>)eGet(AcorePackage.Literals.ACORE_ROOT__CLASSES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AInterface> getInterfaces()
  {
    return (EList<AInterface>)eGet(AcorePackage.Literals.ACORE_ROOT__INTERFACES, true);
  }

} // ACoreRootImpl

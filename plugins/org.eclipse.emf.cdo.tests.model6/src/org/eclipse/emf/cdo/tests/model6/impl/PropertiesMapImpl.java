/*
 * Copyright (c) 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.Model6Package;
import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Properties Map</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapImpl#getPersistentMap <em>Persistent Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapImpl#getTransientMap <em>Transient Map</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PropertiesMapImpl extends CDOObjectImpl implements PropertiesMap
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PropertiesMapImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model6Package.Literals.PROPERTIES_MAP;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLabel()
  {
    return (String)eGet(Model6Package.Literals.PROPERTIES_MAP__LABEL, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLabel(String newLabel)
  {
    eSet(Model6Package.Literals.PROPERTIES_MAP__LABEL, newLabel);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EMap<String, PropertiesMapEntryValue> getPersistentMap()
  {
    return (EMap<String, PropertiesMapEntryValue>)eGet(Model6Package.Literals.PROPERTIES_MAP__PERSISTENT_MAP, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EMap<String, PropertiesMapEntryValue> getTransientMap()
  {
    return (EMap<String, PropertiesMapEntryValue>)eGet(Model6Package.Literals.PROPERTIES_MAP__TRANSIENT_MAP, true);
  }

} // PropertiesMapImpl

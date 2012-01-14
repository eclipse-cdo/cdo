/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs.impl;

import org.eclipse.net4j.util.defs.Def;
import org.eclipse.net4j.util.defs.DefContainer;
import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Defs Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.defs.impl.DefContainerImpl#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.impl.DefContainerImpl#getDefaultDefinition <em>Default Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DefContainerImpl extends EObjectImpl implements DefContainer
{
  /**
   * The cached value of the '{@link #getDefinitions() <em>Definitions</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDefinitions()
   * @generated
   * @ordered
   */
  protected EList<Def> definitions;

  /**
   * The cached value of the '{@link #getDefaultDefinition() <em>Default Definition</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDefaultDefinition()
   * @generated
   * @ordered
   */
  protected Def defaultDefinition;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DefContainerImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jUtilDefsPackage.Literals.DEF_CONTAINER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EList<Def> getDefinitions()
  {
    if (definitions == null)
    {
      definitions = new EObjectContainmentEList<Def>(Def.class, this, Net4jUtilDefsPackage.DEF_CONTAINER__DEFINITIONS);
    }
    return definitions;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Def getDefaultDefinition()
  {
    if (defaultDefinition != null && defaultDefinition.eIsProxy())
    {
      InternalEObject oldDefaultDefinition = (InternalEObject)defaultDefinition;
      defaultDefinition = (Def)eResolveProxy(oldDefaultDefinition);
      if (defaultDefinition != oldDefaultDefinition)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              Net4jUtilDefsPackage.DEF_CONTAINER__DEFAULT_DEFINITION, oldDefaultDefinition, defaultDefinition));
      }
    }
    return defaultDefinition;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Def basicGetDefaultDefinition()
  {
    return defaultDefinition;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultDefinition(Def newDefaultDefinition)
  {
    Def oldDefaultDefinition = defaultDefinition;
    defaultDefinition = newDefaultDefinition;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jUtilDefsPackage.DEF_CONTAINER__DEFAULT_DEFINITION,
          oldDefaultDefinition, defaultDefinition));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFINITIONS:
      return ((InternalEList<?>)getDefinitions()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFINITIONS:
      return getDefinitions();
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFAULT_DEFINITION:
      if (resolve)
        return getDefaultDefinition();
      return basicGetDefaultDefinition();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFINITIONS:
      getDefinitions().clear();
      getDefinitions().addAll((Collection<? extends Def>)newValue);
      return;
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFAULT_DEFINITION:
      setDefaultDefinition((Def)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFINITIONS:
      getDefinitions().clear();
      return;
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFAULT_DEFINITION:
      setDefaultDefinition((Def)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFINITIONS:
      return definitions != null && !definitions.isEmpty();
    case Net4jUtilDefsPackage.DEF_CONTAINER__DEFAULT_DEFINITION:
      return defaultDefinition != null;
    }
    return super.eIsSet(featureID);
  }

} // DefsContainerImpl

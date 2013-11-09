/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.ManualSourceLocator;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Manual Source Locator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ManualSourceLocatorImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ManualSourceLocatorImpl#getComponentNamePattern <em>Component Name Pattern</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ManualSourceLocatorImpl#getComponentTypes <em>Component Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ManualSourceLocatorImpl extends SourceLocatorImpl implements ManualSourceLocator
{
  /**
   * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocation()
   * @generated
   * @ordered
   */
  protected static final String LOCATION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLocation()
   * @generated
   * @ordered
   */
  protected String location = LOCATION_EDEFAULT;

  /**
   * The default value of the '{@link #getComponentNamePattern() <em>Component Name Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponentNamePattern()
   * @generated
   * @ordered
   */
  protected static final String COMPONENT_NAME_PATTERN_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getComponentNamePattern() <em>Component Name Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponentNamePattern()
   * @generated
   * @ordered
   */
  protected String componentNamePattern = COMPONENT_NAME_PATTERN_EDEFAULT;

  /**
   * The cached value of the '{@link #getComponentTypes() <em>Component Types</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponentTypes()
   * @generated
   * @ordered
   */
  protected EList<ComponentType> componentTypes;

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected ManualSourceLocatorImpl()
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
    return SetupPackage.Literals.MANUAL_SOURCE_LOCATOR;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLocation()
  {
    return location;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setLocation(String newLocation)
  {
    String oldLocation = location;
    location = newLocation;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MANUAL_SOURCE_LOCATOR__LOCATION, oldLocation,
          location));
    }
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public String getComponentNamePattern()
  {
    return componentNamePattern;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setComponentNamePattern(String newComponentNamePattern)
  {
    String oldComponentNamePattern = componentNamePattern;
    componentNamePattern = newComponentNamePattern;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN,
          oldComponentNamePattern, componentNamePattern));
    }
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public EList<ComponentType> getComponentTypes()
  {
    if (componentTypes == null)
    {
      componentTypes = new EDataTypeUniqueEList<ComponentType>(ComponentType.class, this,
          SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES);
    }
    return componentTypes;
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.MANUAL_SOURCE_LOCATOR__LOCATION:
      return getLocation();
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN:
      return getComponentNamePattern();
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES:
      return getComponentTypes();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.MANUAL_SOURCE_LOCATOR__LOCATION:
      setLocation((String)newValue);
      return;
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN:
      setComponentNamePattern((String)newValue);
      return;
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES:
      getComponentTypes().clear();
      getComponentTypes().addAll((Collection<? extends ComponentType>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.MANUAL_SOURCE_LOCATOR__LOCATION:
      setLocation(LOCATION_EDEFAULT);
      return;
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN:
      setComponentNamePattern(COMPONENT_NAME_PATTERN_EDEFAULT);
      return;
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES:
      getComponentTypes().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.MANUAL_SOURCE_LOCATOR__LOCATION:
      return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_NAME_PATTERN:
      return COMPONENT_NAME_PATTERN_EDEFAULT == null ? componentNamePattern != null : !COMPONENT_NAME_PATTERN_EDEFAULT
          .equals(componentNamePattern);
    case SetupPackage.MANUAL_SOURCE_LOCATOR__COMPONENT_TYPES:
      return componentTypes != null && !componentTypes.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (location: ");
    result.append(location);
    result.append(", componentNamePattern: ");
    result.append(componentNamePattern);
    result.append(", componentTypes: ");
    result.append(componentTypes);
    result.append(')');
    return result.toString();
  }

} // ManualSourceLocatorImpl

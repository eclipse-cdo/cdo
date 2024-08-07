/******************************************************************************
 * Copyright (c) 2018-2020 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.Filtering;
import org.eclipse.gmf.runtime.notation.FilteringStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Filtering Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FilteringStyleImpl#getFiltering <em>Filtering</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FilteringStyleImpl#getFilteringKeys <em>Filtering Keys</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FilteringStyleImpl#getFilteredObjects <em>Filtered Objects</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class FilteringStyleImpl extends CDOObjectImpl implements FilteringStyle
{
  /**
  * The default value of the '{@link #getFiltering() <em>Filtering</em>}' attribute.
  * <!-- begin-user-doc --> <!-- end-user-doc -->
  * @see #getFiltering()
  * @generated
  * @ordered
  */
  protected static final Filtering FILTERING_EDEFAULT = Filtering.NONE_LITERAL;

  /**
   * The default value of the '{@link #getFilteringKeys() <em>Filtering
   * Keys</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFilteringKeys()
   * @generated NOT
   * @ordered
   */
  protected static final List FILTERING_KEYS_EDEFAULT = Collections.EMPTY_LIST;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected FilteringStyleImpl()
  {
    super();
    setFilteringKeysGen(FILTERING_KEYS_EDEFAULT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.FILTERING_STYLE;
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Filtering getFiltering()
  {
    return (Filtering)eDynamicGet(NotationPackage.FILTERING_STYLE__FILTERING, NotationPackage.Literals.FILTERING_STYLE__FILTERING, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFiltering(Filtering newFiltering)
  {
    eDynamicSet(NotationPackage.FILTERING_STYLE__FILTERING, NotationPackage.Literals.FILTERING_STYLE__FILTERING, newFiltering);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public List getFilteringKeys()
  {
    return Collections.unmodifiableList(getFilteringKeysGen());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public List getFilteringKeysGen()
  {
    return (List)eDynamicGet(NotationPackage.FILTERING_STYLE__FILTERING_KEYS, NotationPackage.Literals.FILTERING_STYLE__FILTERING_KEYS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public void setFilteringKeys(List newFilteringKeys)
  {
    if (newFilteringKeys == null)
    {
      throw new NullPointerException("the 'newFilteringKeys' parameter is null"); //$NON-NLS-1$
    }

    if (newFilteringKeys.isEmpty())
    {
      setFilteringKeysGen(FILTERING_KEYS_EDEFAULT);
    }
    else
    {
      List tempList = new ArrayList(newFilteringKeys.size());
      for (Iterator i = newFilteringKeys.iterator(); i.hasNext();)
      {
        Object key = i.next();
        if (!(key instanceof String))
        {
          throw new IllegalArgumentException("One or more objects in the list is not of type java.lang.String"); //$NON-NLS-1$
        }
        tempList.add(key);
      }
      setFilteringKeysGen(tempList);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setFilteringKeysGen(List newFilteringKeys)
  {
    eDynamicSet(NotationPackage.FILTERING_STYLE__FILTERING_KEYS, NotationPackage.Literals.FILTERING_STYLE__FILTERING_KEYS, newFilteringKeys);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getFilteredObjects()
  {
    return (EList)eDynamicGet(NotationPackage.FILTERING_STYLE__FILTERED_OBJECTS, NotationPackage.Literals.FILTERING_STYLE__FILTERED_OBJECTS, true, true);
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
    case NotationPackage.FILTERING_STYLE__FILTERING:
      return getFiltering();
    case NotationPackage.FILTERING_STYLE__FILTERING_KEYS:
      return getFilteringKeys();
    case NotationPackage.FILTERING_STYLE__FILTERED_OBJECTS:
      return getFilteredObjects();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.FILTERING_STYLE__FILTERING:
      setFiltering((Filtering)newValue);
      return;
    case NotationPackage.FILTERING_STYLE__FILTERING_KEYS:
      setFilteringKeys((List)newValue);
      return;
    case NotationPackage.FILTERING_STYLE__FILTERED_OBJECTS:
      getFilteredObjects().clear();
      getFilteredObjects().addAll((Collection)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.FILTERING_STYLE__FILTERING:
      setFiltering(FILTERING_EDEFAULT);
      return;
    case NotationPackage.FILTERING_STYLE__FILTERING_KEYS:
      setFilteringKeys(FILTERING_KEYS_EDEFAULT);
      return;
    case NotationPackage.FILTERING_STYLE__FILTERED_OBJECTS:
      getFilteredObjects().clear();
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.FILTERING_STYLE__FILTERING:
      return getFiltering() != FILTERING_EDEFAULT;
    case NotationPackage.FILTERING_STYLE__FILTERING_KEYS:
      return FILTERING_KEYS_EDEFAULT == null ? getFilteringKeysGen() != null : !FILTERING_KEYS_EDEFAULT.equals(getFilteringKeysGen());
    case NotationPackage.FILTERING_STYLE__FILTERED_OBJECTS:
      return !getFilteredObjects().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // FilteringStyleImpl

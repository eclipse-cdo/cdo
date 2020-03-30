/******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Sorting;
import org.eclipse.gmf.runtime.notation.SortingDirection;
import org.eclipse.gmf.runtime.notation.SortingStyle;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sorting Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.SortingStyleImpl#getSorting <em>Sorting</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.SortingStyleImpl#getSortingKeys <em>Sorting Keys</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.SortingStyleImpl#getSortedObjects <em>Sorted Objects</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class SortingStyleImpl extends CDOObjectImpl implements SortingStyle
{
  /**
  * The default value of the '{@link #getSorting() <em>Sorting</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getSorting()
  * @generated
  * @ordered
  */
  protected static final Sorting SORTING_EDEFAULT = Sorting.NONE_LITERAL;

  /**
  * The default value of the '{@link #getSortingKeys() <em>Sorting Keys</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getSortingKeys()
  * @generated NOT
  * @ordered
  */
  protected static final Map SORTING_KEYS_EDEFAULT = Collections.EMPTY_MAP;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected SortingStyleImpl()
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
    return NotationPackage.Literals.SORTING_STYLE;
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
  public Sorting getSorting()
  {
    return (Sorting)eDynamicGet(NotationPackage.SORTING_STYLE__SORTING, NotationPackage.Literals.SORTING_STYLE__SORTING, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setSorting(Sorting newSorting)
  {
    eDynamicSet(NotationPackage.SORTING_STYLE__SORTING, NotationPackage.Literals.SORTING_STYLE__SORTING, newSorting);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Map getSortingKeys()
  {
    return Collections.unmodifiableMap(getSortingKeysGen());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map getSortingKeysGen()
  {
    return (Map)eDynamicGet(NotationPackage.SORTING_STYLE__SORTING_KEYS, NotationPackage.Literals.SORTING_STYLE__SORTING_KEYS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setSortingKeys(Map newSortingKeys)
  {
    if (newSortingKeys == null)
    {
      throw new NullPointerException("the 'newSortingKeys' parameter is null"); //$NON-NLS-1$
    }

    if (newSortingKeys.isEmpty())
    {
      setSortingKeysGen(SORTING_KEYS_EDEFAULT);
    }
    else
    {
      Map tempMap = new LinkedHashMap(newSortingKeys.size());
      for (Iterator i = newSortingKeys.keySet().iterator(); i.hasNext();)
      {
        Object key = i.next();
        if (!(key instanceof String))
        {
          throw new IllegalArgumentException("One or more keys in the map is not of type java.lang.String"); //$NON-NLS-1$
        }
        Object value = newSortingKeys.get(key);
        if (!(value instanceof SortingDirection))
        {
          throw new IllegalArgumentException("One or more values in the map is not of type org.eclipse.gmf.runtime.notation.SortingDirection"); //$NON-NLS-1$
        }
        tempMap.put(key, value);
      }
      setSortingKeysGen(tempMap);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSortingKeysGen(Map newSortingKeys)
  {
    eDynamicSet(NotationPackage.SORTING_STYLE__SORTING_KEYS, NotationPackage.Literals.SORTING_STYLE__SORTING_KEYS, newSortingKeys);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public EList getSortedObjects()
  {
    return (EList)eDynamicGet(NotationPackage.SORTING_STYLE__SORTED_OBJECTS, NotationPackage.Literals.SORTING_STYLE__SORTED_OBJECTS, true, true);
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
    case NotationPackage.SORTING_STYLE__SORTING:
      return getSorting();
    case NotationPackage.SORTING_STYLE__SORTING_KEYS:
      return getSortingKeys();
    case NotationPackage.SORTING_STYLE__SORTED_OBJECTS:
      return getSortedObjects();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
  * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.SORTING_STYLE__SORTING:
      setSorting((Sorting)newValue);
      return;
    case NotationPackage.SORTING_STYLE__SORTING_KEYS:
      setSortingKeys((Map)newValue);
      return;
    case NotationPackage.SORTING_STYLE__SORTED_OBJECTS:
      getSortedObjects().clear();
      getSortedObjects().addAll((Collection)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.SORTING_STYLE__SORTING:
      setSorting(SORTING_EDEFAULT);
      return;
    case NotationPackage.SORTING_STYLE__SORTING_KEYS:
      setSortingKeys(SORTING_KEYS_EDEFAULT);
      return;
    case NotationPackage.SORTING_STYLE__SORTED_OBJECTS:
      getSortedObjects().clear();
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.SORTING_STYLE__SORTING:
      return getSorting() != SORTING_EDEFAULT;
    case NotationPackage.SORTING_STYLE__SORTING_KEYS:
      return SORTING_KEYS_EDEFAULT == null ? getSortingKeys() != null : !SORTING_KEYS_EDEFAULT.equals(getSortingKeys());
    case NotationPackage.SORTING_STYLE__SORTED_OBJECTS:
      return !getSortedObjects().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // SortingStyleImpl

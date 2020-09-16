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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.Filtering;
import org.eclipse.gmf.runtime.notation.FilteringStyle;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.ListCompartment;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Sorting;
import org.eclipse.gmf.runtime.notation.SortingStyle;
import org.eclipse.gmf.runtime.notation.TitleStyle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>List
 * Compartment</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListCompartmentImpl#getSorting <em>Sorting</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListCompartmentImpl#getSortingKeys <em>Sorting Keys</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListCompartmentImpl#getSortedObjects <em>Sorted Objects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListCompartmentImpl#getFiltering <em>Filtering</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListCompartmentImpl#getFilteringKeys <em>Filtering Keys</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListCompartmentImpl#getFilteredObjects <em>Filtered Objects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ListCompartmentImpl#isShowTitle <em>Show Title</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ListCompartmentImpl extends BasicCompartmentImpl implements ListCompartment
{
  /**
   * The default value of the '{@link #getSorting() <em>Sorting</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getSorting()
   * @generated
   * @ordered
   */
  protected static final Sorting SORTING_EDEFAULT = Sorting.NONE_LITERAL;

  /**
   * The default value of the '{@link #getSortingKeys() <em>Sorting Keys</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getSortingKeys()
   * @generated NOT
   * @ordered
   */
  protected static final Map SORTING_KEYS_EDEFAULT = Collections.EMPTY_MAP;

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
   * The default value of the '{@link #isShowTitle() <em>Show Title</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isShowTitle()
   * @generated
   * @ordered
   */
  protected static final boolean SHOW_TITLE_EDEFAULT = false;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ListCompartmentImpl()
  {
    super();
    setSortingKeys(SORTING_KEYS_EDEFAULT);
    setFilteringKeys(FILTERING_KEYS_EDEFAULT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.LIST_COMPARTMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Sorting getSorting()
  {
    return (Sorting)eDynamicGet(NotationPackage.LIST_COMPARTMENT__SORTING - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.SORTING_STYLE__SORTING, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSorting(Sorting newSorting)
  {
    eDynamicSet(NotationPackage.LIST_COMPARTMENT__SORTING - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.SORTING_STYLE__SORTING, newSorting);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Map getSortingKeys()
  {
    return (Map)eDynamicGet(NotationPackage.LIST_COMPARTMENT__SORTING_KEYS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.SORTING_STYLE__SORTING_KEYS, true,
        true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSortingKeys(Map newSortingKeys)
  {
    eDynamicSet(NotationPackage.LIST_COMPARTMENT__SORTING_KEYS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.SORTING_STYLE__SORTING_KEYS, newSortingKeys);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getSortedObjects()
  {
    return (EList)eDynamicGet(NotationPackage.LIST_COMPARTMENT__SORTED_OBJECTS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.SORTING_STYLE__SORTED_OBJECTS,
        true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Filtering getFiltering()
  {
    return (Filtering)eDynamicGet(NotationPackage.LIST_COMPARTMENT__FILTERING - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILTERING_STYLE__FILTERING,
        true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFiltering(Filtering newFiltering)
  {
    eDynamicSet(NotationPackage.LIST_COMPARTMENT__FILTERING - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILTERING_STYLE__FILTERING, newFiltering);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List getFilteringKeys()
  {
    return (List)eDynamicGet(NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILTERING_STYLE__FILTERING_KEYS,
        true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFilteringKeys(List newFilteringKeys)
  {
    eDynamicSet(NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILTERING_STYLE__FILTERING_KEYS,
        newFilteringKeys);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getFilteredObjects()
  {
    return (EList)eDynamicGet(NotationPackage.LIST_COMPARTMENT__FILTERED_OBJECTS - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.FILTERING_STYLE__FILTERED_OBJECTS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isShowTitle()
  {
    return ((Boolean)eDynamicGet(NotationPackage.LIST_COMPARTMENT__SHOW_TITLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.TITLE_STYLE__SHOW_TITLE, true,
        true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setShowTitle(boolean newShowTitle)
  {
    eDynamicSet(NotationPackage.LIST_COMPARTMENT__SHOW_TITLE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.TITLE_STYLE__SHOW_TITLE,
        new Boolean(newShowTitle));
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
    case NotationPackage.LIST_COMPARTMENT__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.LIST_COMPARTMENT__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.LIST_COMPARTMENT__TYPE:
      return getType();
    case NotationPackage.LIST_COMPARTMENT__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.LIST_COMPARTMENT__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.LIST_COMPARTMENT__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.LIST_COMPARTMENT__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.LIST_COMPARTMENT__STYLES:
      return getStyles();
    case NotationPackage.LIST_COMPARTMENT__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.LIST_COMPARTMENT__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.LIST_COMPARTMENT__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.LIST_COMPARTMENT__LAYOUT_CONSTRAINT:
      return getLayoutConstraint();
    case NotationPackage.LIST_COMPARTMENT__COLLAPSED:
      return isCollapsed() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.LIST_COMPARTMENT__SORTING:
      return getSorting();
    case NotationPackage.LIST_COMPARTMENT__SORTING_KEYS:
      return getSortingKeys();
    case NotationPackage.LIST_COMPARTMENT__SORTED_OBJECTS:
      return getSortedObjects();
    case NotationPackage.LIST_COMPARTMENT__FILTERING:
      return getFiltering();
    case NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS:
      return getFilteringKeys();
    case NotationPackage.LIST_COMPARTMENT__FILTERED_OBJECTS:
      return getFilteredObjects();
    case NotationPackage.LIST_COMPARTMENT__SHOW_TITLE:
      return isShowTitle() ? Boolean.TRUE : Boolean.FALSE;
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.LIST_COMPARTMENT__SOURCE_EDGES:
    case NotationPackage.LIST_COMPARTMENT__TARGET_EDGES:
      return;
    default:
      eSetGen(featureID, newValue);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSetGen(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.LIST_COMPARTMENT__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.LIST_COMPARTMENT__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.LIST_COMPARTMENT__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__COLLAPSED:
      setCollapsed(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.LIST_COMPARTMENT__SORTING:
      setSorting((Sorting)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__SORTING_KEYS:
      setSortingKeys((Map)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__SORTED_OBJECTS:
      getSortedObjects().clear();
      getSortedObjects().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__FILTERING:
      setFiltering((Filtering)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS:
      setFilteringKeys((List)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__FILTERED_OBJECTS:
      getFilteredObjects().clear();
      getFilteredObjects().addAll((Collection)newValue);
      return;
    case NotationPackage.LIST_COMPARTMENT__SHOW_TITLE:
      setShowTitle(((Boolean)newValue).booleanValue());
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.LIST_COMPARTMENT__SOURCE_EDGES:
    case NotationPackage.LIST_COMPARTMENT__TARGET_EDGES:
      return;
    default:
      eUnsetGen(featureID);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnsetGen(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.LIST_COMPARTMENT__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.LIST_COMPARTMENT__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)null);
      return;
    case NotationPackage.LIST_COMPARTMENT__COLLAPSED:
      setCollapsed(COLLAPSED_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__SORTING:
      setSorting(SORTING_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__SORTING_KEYS:
      setSortingKeys(SORTING_KEYS_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__SORTED_OBJECTS:
      getSortedObjects().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__FILTERING:
      setFiltering(FILTERING_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS:
      setFilteringKeys(FILTERING_KEYS_EDEFAULT);
      return;
    case NotationPackage.LIST_COMPARTMENT__FILTERED_OBJECTS:
      getFilteredObjects().clear();
      return;
    case NotationPackage.LIST_COMPARTMENT__SHOW_TITLE:
      setShowTitle(SHOW_TITLE_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.LIST_COMPARTMENT__SOURCE_EDGES:
    case NotationPackage.LIST_COMPARTMENT__TARGET_EDGES:
      return false;
    default:
      return eIsSetGen(featureID);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSetGen(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.LIST_COMPARTMENT__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.LIST_COMPARTMENT__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.LIST_COMPARTMENT__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.LIST_COMPARTMENT__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__ELEMENT:
      return isSetElement();
    case NotationPackage.LIST_COMPARTMENT__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.LIST_COMPARTMENT__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__LAYOUT_CONSTRAINT:
      return getLayoutConstraint() != null;
    case NotationPackage.LIST_COMPARTMENT__COLLAPSED:
      return isCollapsed() != COLLAPSED_EDEFAULT;
    case NotationPackage.LIST_COMPARTMENT__SORTING:
      return getSorting() != SORTING_EDEFAULT;
    case NotationPackage.LIST_COMPARTMENT__SORTING_KEYS:
      return SORTING_KEYS_EDEFAULT == null ? getSortingKeys() != null : !SORTING_KEYS_EDEFAULT.equals(getSortingKeys());
    case NotationPackage.LIST_COMPARTMENT__SORTED_OBJECTS:
      return !getSortedObjects().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__FILTERING:
      return getFiltering() != FILTERING_EDEFAULT;
    case NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS:
      return FILTERING_KEYS_EDEFAULT == null ? getFilteringKeys() != null : !FILTERING_KEYS_EDEFAULT.equals(getFilteringKeys());
    case NotationPackage.LIST_COMPARTMENT__FILTERED_OBJECTS:
      return !getFilteredObjects().isEmpty();
    case NotationPackage.LIST_COMPARTMENT__SHOW_TITLE:
      return isShowTitle() != SHOW_TITLE_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass)
  {
    if (baseClass == SortingStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.LIST_COMPARTMENT__SORTING:
        return NotationPackage.SORTING_STYLE__SORTING;
      case NotationPackage.LIST_COMPARTMENT__SORTING_KEYS:
        return NotationPackage.SORTING_STYLE__SORTING_KEYS;
      case NotationPackage.LIST_COMPARTMENT__SORTED_OBJECTS:
        return NotationPackage.SORTING_STYLE__SORTED_OBJECTS;
      default:
        return -1;
      }
    }
    if (baseClass == FilteringStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.LIST_COMPARTMENT__FILTERING:
        return NotationPackage.FILTERING_STYLE__FILTERING;
      case NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS:
        return NotationPackage.FILTERING_STYLE__FILTERING_KEYS;
      case NotationPackage.LIST_COMPARTMENT__FILTERED_OBJECTS:
        return NotationPackage.FILTERING_STYLE__FILTERED_OBJECTS;
      default:
        return -1;
      }
    }
    if (baseClass == TitleStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.LIST_COMPARTMENT__SHOW_TITLE:
        return NotationPackage.TITLE_STYLE__SHOW_TITLE;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass)
  {
    if (baseClass == SortingStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.SORTING_STYLE__SORTING:
        return NotationPackage.LIST_COMPARTMENT__SORTING;
      case NotationPackage.SORTING_STYLE__SORTING_KEYS:
        return NotationPackage.LIST_COMPARTMENT__SORTING_KEYS;
      case NotationPackage.SORTING_STYLE__SORTED_OBJECTS:
        return NotationPackage.LIST_COMPARTMENT__SORTED_OBJECTS;
      default:
        return -1;
      }
    }
    if (baseClass == FilteringStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.FILTERING_STYLE__FILTERING:
        return NotationPackage.LIST_COMPARTMENT__FILTERING;
      case NotationPackage.FILTERING_STYLE__FILTERING_KEYS:
        return NotationPackage.LIST_COMPARTMENT__FILTERING_KEYS;
      case NotationPackage.FILTERING_STYLE__FILTERED_OBJECTS:
        return NotationPackage.LIST_COMPARTMENT__FILTERED_OBJECTS;
      default:
        return -1;
      }
    }
    if (baseClass == TitleStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.TITLE_STYLE__SHOW_TITLE:
        return NotationPackage.LIST_COMPARTMENT__SHOW_TITLE;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ListCompartmentImpl

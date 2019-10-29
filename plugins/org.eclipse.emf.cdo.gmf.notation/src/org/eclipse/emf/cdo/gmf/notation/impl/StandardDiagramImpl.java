/******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.DescriptionStyle;
import org.eclipse.gmf.runtime.notation.DiagramStyle;
import org.eclipse.gmf.runtime.notation.GuideStyle;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.PageStyle;
import org.eclipse.gmf.runtime.notation.StandardDiagram;
import org.eclipse.gmf.runtime.notation.Style;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Standard Diagram</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StandardDiagramImpl#getPageX <em>Page X</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StandardDiagramImpl#getPageY <em>Page Y</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StandardDiagramImpl#getPageWidth <em>Page Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StandardDiagramImpl#getPageHeight <em>Page Height</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StandardDiagramImpl#getHorizontalGuides <em>Horizontal Guides</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StandardDiagramImpl#getVerticalGuides <em>Vertical Guides</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.StandardDiagramImpl#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StandardDiagramImpl extends DiagramImpl implements StandardDiagram
{
  /**
   * The default value of the '{@link #getPageX() <em>Page X</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPageX()
   * @generated
   * @ordered
   */
  protected static final int PAGE_X_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getPageY() <em>Page Y</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPageY()
   * @generated
   * @ordered
   */
  protected static final int PAGE_Y_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getPageWidth() <em>Page Width</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPageWidth()
   * @generated
   * @ordered
   */
  protected static final int PAGE_WIDTH_EDEFAULT = 100;

  /**
   * The default value of the '{@link #getPageHeight() <em>Page Height</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPageHeight()
   * @generated
   * @ordered
   */
  protected static final int PAGE_HEIGHT_EDEFAULT = 100;

  /**
   * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected static final String DESCRIPTION_EDEFAULT = ""; //$NON-NLS-1$

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected StandardDiagramImpl()
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
    return NotationPackage.Literals.STANDARD_DIAGRAM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getPageX()
  {
    return ((Integer)eDynamicGet(NotationPackage.STANDARD_DIAGRAM__PAGE_X - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_X, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPageX(int newPageX)
  {
    eDynamicSet(NotationPackage.STANDARD_DIAGRAM__PAGE_X - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_X, new Integer(newPageX));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getPageY()
  {
    return ((Integer)eDynamicGet(NotationPackage.STANDARD_DIAGRAM__PAGE_Y - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_Y, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPageY(int newPageY)
  {
    eDynamicSet(NotationPackage.STANDARD_DIAGRAM__PAGE_Y - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_Y, new Integer(newPageY));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getPageWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_WIDTH, true,
        true)).intValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPageWidth(int newPageWidth)
  {
    eDynamicSet(NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_WIDTH,
        new Integer(newPageWidth));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getPageHeight()
  {
    return ((Integer)eDynamicGet(NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_HEIGHT, true,
        true)).intValue();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPageHeight(int newPageHeight)
  {
    eDynamicSet(NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.PAGE_STYLE__PAGE_HEIGHT,
        new Integer(newPageHeight));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getHorizontalGuides()
  {
    return (EList)eDynamicGet(NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.GUIDE_STYLE__HORIZONTAL_GUIDES, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getVerticalGuides()
  {
    return (EList)eDynamicGet(NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.GUIDE_STYLE__VERTICAL_GUIDES,
        true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDescription()
  {
    return (String)eDynamicGet(NotationPackage.STANDARD_DIAGRAM__DESCRIPTION - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION,
        true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDescription(String newDescription)
  {
    eDynamicSet(NotationPackage.STANDARD_DIAGRAM__DESCRIPTION - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION, newDescription);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.STANDARD_DIAGRAM__EANNOTATIONS:
      return ((InternalEList)getEAnnotations()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__SOURCE_EDGES:
      return ((InternalEList)getSourceEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__TARGET_EDGES:
      return ((InternalEList)getTargetEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_CHILDREN:
      return ((InternalEList)getPersistedChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__STYLES:
      return ((InternalEList)getStyles()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_CHILDREN:
      return ((InternalEList)getTransientChildren()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_EDGES:
      return ((InternalEList)getPersistedEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_EDGES:
      return ((InternalEList)getTransientEdges()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES:
      return ((InternalEList)getHorizontalGuides()).basicRemove(otherEnd, msgs);
    case NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES:
      return ((InternalEList)getVerticalGuides()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case NotationPackage.STANDARD_DIAGRAM__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.STANDARD_DIAGRAM__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.STANDARD_DIAGRAM__TYPE:
      return getType();
    case NotationPackage.STANDARD_DIAGRAM__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.STANDARD_DIAGRAM__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.STANDARD_DIAGRAM__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.STANDARD_DIAGRAM__STYLES:
      return getStyles();
    case NotationPackage.STANDARD_DIAGRAM__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.STANDARD_DIAGRAM__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.STANDARD_DIAGRAM__NAME:
      return getName();
    case NotationPackage.STANDARD_DIAGRAM__MEASUREMENT_UNIT:
      return getMeasurementUnit();
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_EDGES:
      return getPersistedEdges();
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_EDGES:
      return getTransientEdges();
    case NotationPackage.STANDARD_DIAGRAM__PAGE_X:
      return new Integer(getPageX());
    case NotationPackage.STANDARD_DIAGRAM__PAGE_Y:
      return new Integer(getPageY());
    case NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH:
      return new Integer(getPageWidth());
    case NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT:
      return new Integer(getPageHeight());
    case NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES:
      return getHorizontalGuides();
    case NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES:
      return getVerticalGuides();
    case NotationPackage.STANDARD_DIAGRAM__DESCRIPTION:
      return getDescription();
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
    case NotationPackage.STANDARD_DIAGRAM__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.STANDARD_DIAGRAM__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.STANDARD_DIAGRAM__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__NAME:
      setName((String)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__MEASUREMENT_UNIT:
      setMeasurementUnit((MeasurementUnit)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_EDGES:
      getPersistedEdges().clear();
      getPersistedEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_EDGES:
      getTransientEdges().clear();
      getTransientEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_X:
      setPageX(((Integer)newValue).intValue());
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_Y:
      setPageY(((Integer)newValue).intValue());
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH:
      setPageWidth(((Integer)newValue).intValue());
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT:
      setPageHeight(((Integer)newValue).intValue());
      return;
    case NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES:
      getHorizontalGuides().clear();
      getHorizontalGuides().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES:
      getVerticalGuides().clear();
      getVerticalGuides().addAll((Collection)newValue);
      return;
    case NotationPackage.STANDARD_DIAGRAM__DESCRIPTION:
      setDescription((String)newValue);
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
    case NotationPackage.STANDARD_DIAGRAM__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__NAME:
      setName(NAME_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__MEASUREMENT_UNIT:
      unsetMeasurementUnit();
      return;
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_EDGES:
      getPersistedEdges().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_EDGES:
      getTransientEdges().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_X:
      setPageX(PAGE_X_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_Y:
      setPageY(PAGE_Y_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH:
      setPageWidth(PAGE_WIDTH_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT:
      setPageHeight(PAGE_HEIGHT_EDEFAULT);
      return;
    case NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES:
      getHorizontalGuides().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES:
      getVerticalGuides().clear();
      return;
    case NotationPackage.STANDARD_DIAGRAM__DESCRIPTION:
      setDescription(DESCRIPTION_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("null")
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.STANDARD_DIAGRAM__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.STANDARD_DIAGRAM__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.STANDARD_DIAGRAM__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.STANDARD_DIAGRAM__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__ELEMENT:
      return isSetElement();
    case NotationPackage.STANDARD_DIAGRAM__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case NotationPackage.STANDARD_DIAGRAM__MEASUREMENT_UNIT:
      return isSetMeasurementUnit();
    case NotationPackage.STANDARD_DIAGRAM__PERSISTED_EDGES:
      return !getPersistedEdges().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__TRANSIENT_EDGES:
      return !getTransientEdges().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__PAGE_X:
      return getPageX() != PAGE_X_EDEFAULT;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_Y:
      return getPageY() != PAGE_Y_EDEFAULT;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH:
      return getPageWidth() != PAGE_WIDTH_EDEFAULT;
    case NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT:
      return getPageHeight() != PAGE_HEIGHT_EDEFAULT;
    case NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES:
      return !getHorizontalGuides().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES:
      return !getVerticalGuides().isEmpty();
    case NotationPackage.STANDARD_DIAGRAM__DESCRIPTION:
      return DESCRIPTION_EDEFAULT == null ? getDescription() != null : !DESCRIPTION_EDEFAULT.equals(getDescription());
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass)
  {
    if (baseClass == Style.class)
    {
      switch (derivedFeatureID)
      {
      default:
        return -1;
      }
    }
    if (baseClass == PageStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.STANDARD_DIAGRAM__PAGE_X:
        return NotationPackage.PAGE_STYLE__PAGE_X;
      case NotationPackage.STANDARD_DIAGRAM__PAGE_Y:
        return NotationPackage.PAGE_STYLE__PAGE_Y;
      case NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH:
        return NotationPackage.PAGE_STYLE__PAGE_WIDTH;
      case NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT:
        return NotationPackage.PAGE_STYLE__PAGE_HEIGHT;
      default:
        return -1;
      }
    }
    if (baseClass == GuideStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES:
        return NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES;
      case NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES:
        return NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES;
      default:
        return -1;
      }
    }
    if (baseClass == DescriptionStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.STANDARD_DIAGRAM__DESCRIPTION:
        return NotationPackage.DESCRIPTION_STYLE__DESCRIPTION;
      default:
        return -1;
      }
    }
    if (baseClass == DiagramStyle.class)
    {
      switch (derivedFeatureID)
      {
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass)
  {
    if (baseClass == Style.class)
    {
      switch (baseFeatureID)
      {
      default:
        return -1;
      }
    }
    if (baseClass == PageStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.PAGE_STYLE__PAGE_X:
        return NotationPackage.STANDARD_DIAGRAM__PAGE_X;
      case NotationPackage.PAGE_STYLE__PAGE_Y:
        return NotationPackage.STANDARD_DIAGRAM__PAGE_Y;
      case NotationPackage.PAGE_STYLE__PAGE_WIDTH:
        return NotationPackage.STANDARD_DIAGRAM__PAGE_WIDTH;
      case NotationPackage.PAGE_STYLE__PAGE_HEIGHT:
        return NotationPackage.STANDARD_DIAGRAM__PAGE_HEIGHT;
      default:
        return -1;
      }
    }
    if (baseClass == GuideStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES:
        return NotationPackage.STANDARD_DIAGRAM__HORIZONTAL_GUIDES;
      case NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES:
        return NotationPackage.STANDARD_DIAGRAM__VERTICAL_GUIDES;
      default:
        return -1;
      }
    }
    if (baseClass == DescriptionStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.DESCRIPTION_STYLE__DESCRIPTION:
        return NotationPackage.STANDARD_DIAGRAM__DESCRIPTION;
      default:
        return -1;
      }
    }
    if (baseClass == DiagramStyle.class)
    {
      switch (baseFeatureID)
      {
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // StandardDiagramImpl

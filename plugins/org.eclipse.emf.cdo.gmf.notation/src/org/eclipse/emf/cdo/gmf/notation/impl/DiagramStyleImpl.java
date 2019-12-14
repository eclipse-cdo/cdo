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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.DescriptionStyle;
import org.eclipse.gmf.runtime.notation.DiagramStyle;
import org.eclipse.gmf.runtime.notation.GuideStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Diagram Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramStyleImpl#getHorizontalGuides <em>Horizontal Guides</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramStyleImpl#getVerticalGuides <em>Vertical Guides</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramStyleImpl#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class DiagramStyleImpl extends PageStyleImpl implements DiagramStyle
{

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
  protected DiagramStyleImpl()
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
    return NotationPackage.Literals.DIAGRAM_STYLE;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public EList getHorizontalGuides()
  {
    return (EList)eDynamicGet(NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES, NotationPackage.Literals.GUIDE_STYLE__HORIZONTAL_GUIDES, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public EList getVerticalGuides()
  {
    return (EList)eDynamicGet(NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES, NotationPackage.Literals.GUIDE_STYLE__VERTICAL_GUIDES, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public String getDescription()
  {
    return (String)eDynamicGet(NotationPackage.DIAGRAM_STYLE__DESCRIPTION, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setDescription(String newDescription)
  {
    eDynamicSet(NotationPackage.DIAGRAM_STYLE__DESCRIPTION, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION, newDescription);
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
    case NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES:
      return ((InternalEList)getHorizontalGuides()).basicRemove(otherEnd, msgs);
    case NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES:
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
    case NotationPackage.DIAGRAM_STYLE__PAGE_X:
      return new Integer(getPageX());
    case NotationPackage.DIAGRAM_STYLE__PAGE_Y:
      return new Integer(getPageY());
    case NotationPackage.DIAGRAM_STYLE__PAGE_WIDTH:
      return new Integer(getPageWidth());
    case NotationPackage.DIAGRAM_STYLE__PAGE_HEIGHT:
      return new Integer(getPageHeight());
    case NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES:
      return getHorizontalGuides();
    case NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES:
      return getVerticalGuides();
    case NotationPackage.DIAGRAM_STYLE__DESCRIPTION:
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
    case NotationPackage.DIAGRAM_STYLE__PAGE_X:
      setPageX(((Integer)newValue).intValue());
      return;
    case NotationPackage.DIAGRAM_STYLE__PAGE_Y:
      setPageY(((Integer)newValue).intValue());
      return;
    case NotationPackage.DIAGRAM_STYLE__PAGE_WIDTH:
      setPageWidth(((Integer)newValue).intValue());
      return;
    case NotationPackage.DIAGRAM_STYLE__PAGE_HEIGHT:
      setPageHeight(((Integer)newValue).intValue());
      return;
    case NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES:
      getHorizontalGuides().clear();
      getHorizontalGuides().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES:
      getVerticalGuides().clear();
      getVerticalGuides().addAll((Collection)newValue);
      return;
    case NotationPackage.DIAGRAM_STYLE__DESCRIPTION:
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
    case NotationPackage.DIAGRAM_STYLE__PAGE_X:
      setPageX(PAGE_X_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM_STYLE__PAGE_Y:
      setPageY(PAGE_Y_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM_STYLE__PAGE_WIDTH:
      setPageWidth(PAGE_WIDTH_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM_STYLE__PAGE_HEIGHT:
      setPageHeight(PAGE_HEIGHT_EDEFAULT);
      return;
    case NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES:
      getHorizontalGuides().clear();
      return;
    case NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES:
      getVerticalGuides().clear();
      return;
    case NotationPackage.DIAGRAM_STYLE__DESCRIPTION:
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
    case NotationPackage.DIAGRAM_STYLE__PAGE_X:
      return getPageX() != PAGE_X_EDEFAULT;
    case NotationPackage.DIAGRAM_STYLE__PAGE_Y:
      return getPageY() != PAGE_Y_EDEFAULT;
    case NotationPackage.DIAGRAM_STYLE__PAGE_WIDTH:
      return getPageWidth() != PAGE_WIDTH_EDEFAULT;
    case NotationPackage.DIAGRAM_STYLE__PAGE_HEIGHT:
      return getPageHeight() != PAGE_HEIGHT_EDEFAULT;
    case NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES:
      return !getHorizontalGuides().isEmpty();
    case NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES:
      return !getVerticalGuides().isEmpty();
    case NotationPackage.DIAGRAM_STYLE__DESCRIPTION:
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
    if (baseClass == GuideStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES:
        return NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES;
      case NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES:
        return NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES;
      default:
        return -1;
      }
    }
    if (baseClass == DescriptionStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.DIAGRAM_STYLE__DESCRIPTION:
        return NotationPackage.DESCRIPTION_STYLE__DESCRIPTION;
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
    if (baseClass == GuideStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES:
        return NotationPackage.DIAGRAM_STYLE__HORIZONTAL_GUIDES;
      case NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES:
        return NotationPackage.DIAGRAM_STYLE__VERTICAL_GUIDES;
      default:
        return -1;
      }
    }
    if (baseClass == DescriptionStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.DESCRIPTION_STYLE__DESCRIPTION:
        return NotationPackage.DIAGRAM_STYLE__DESCRIPTION;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // DiagramStyleImpl

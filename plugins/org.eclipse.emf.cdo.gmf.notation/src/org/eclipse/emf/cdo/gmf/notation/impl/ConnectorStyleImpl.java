/******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
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

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.ConnectorStyle;
import org.eclipse.gmf.runtime.notation.JumpLinkStatus;
import org.eclipse.gmf.runtime.notation.JumpLinkType;
import org.eclipse.gmf.runtime.notation.LineStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.gmf.runtime.notation.Smoothness;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Connector Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorStyleImpl#getLineColor <em>Line Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorStyleImpl#getLineWidth <em>Line Width</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class ConnectorStyleImpl extends RoutingStyleImpl implements ConnectorStyle
{

  /**
   * The default value of the '{@link #getLineColor() <em>Line Color</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getLineColor()
   * @generated
   * @ordered
   */
  protected static final int LINE_COLOR_EDEFAULT = 11579568;

  /**
   * The default value of the '{@link #getLineWidth() <em>Line Width</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getLineWidth()
   * @generated
   * @ordered
   */
  protected static final int LINE_WIDTH_EDEFAULT = -1;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ConnectorStyleImpl()
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
    return NotationPackage.Literals.CONNECTOR_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getLineColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.CONNECTOR_STYLE__LINE_COLOR, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineColor(int newLineColor)
  {
    eDynamicSet(NotationPackage.CONNECTOR_STYLE__LINE_COLOR, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, new Integer(newLineColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getLineWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.CONNECTOR_STYLE__LINE_WIDTH, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineWidth(int newLineWidth)
  {
    eDynamicSet(NotationPackage.CONNECTOR_STYLE__LINE_WIDTH, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, new Integer(newLineWidth));
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
    case NotationPackage.CONNECTOR_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      return new Integer(getRoundedBendpointsRadius());
    case NotationPackage.CONNECTOR_STYLE__ROUTING:
      return getRouting();
    case NotationPackage.CONNECTOR_STYLE__SMOOTHNESS:
      return getSmoothness();
    case NotationPackage.CONNECTOR_STYLE__AVOID_OBSTRUCTIONS:
      return isAvoidObstructions() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR_STYLE__CLOSEST_DISTANCE:
      return isClosestDistance() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_STATUS:
      return getJumpLinkStatus();
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_TYPE:
      return getJumpLinkType();
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINKS_REVERSE:
      return isJumpLinksReverse() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR_STYLE__LINE_COLOR:
      return new Integer(getLineColor());
    case NotationPackage.CONNECTOR_STYLE__LINE_WIDTH:
      return new Integer(getLineWidth());
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
    case NotationPackage.CONNECTOR_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(((Integer)newValue).intValue());
      return;
    case NotationPackage.CONNECTOR_STYLE__ROUTING:
      setRouting((Routing)newValue);
      return;
    case NotationPackage.CONNECTOR_STYLE__SMOOTHNESS:
      setSmoothness((Smoothness)newValue);
      return;
    case NotationPackage.CONNECTOR_STYLE__AVOID_OBSTRUCTIONS:
      setAvoidObstructions(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR_STYLE__CLOSEST_DISTANCE:
      setClosestDistance(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_STATUS:
      setJumpLinkStatus((JumpLinkStatus)newValue);
      return;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_TYPE:
      setJumpLinkType((JumpLinkType)newValue);
      return;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINKS_REVERSE:
      setJumpLinksReverse(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR_STYLE__LINE_COLOR:
      setLineColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.CONNECTOR_STYLE__LINE_WIDTH:
      setLineWidth(((Integer)newValue).intValue());
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
    case NotationPackage.CONNECTOR_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(ROUNDED_BENDPOINTS_RADIUS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__ROUTING:
      setRouting(ROUTING_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__SMOOTHNESS:
      setSmoothness(SMOOTHNESS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__AVOID_OBSTRUCTIONS:
      setAvoidObstructions(AVOID_OBSTRUCTIONS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__CLOSEST_DISTANCE:
      setClosestDistance(CLOSEST_DISTANCE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_STATUS:
      setJumpLinkStatus(JUMP_LINK_STATUS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_TYPE:
      setJumpLinkType(JUMP_LINK_TYPE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINKS_REVERSE:
      setJumpLinksReverse(JUMP_LINKS_REVERSE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__LINE_COLOR:
      setLineColor(LINE_COLOR_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR_STYLE__LINE_WIDTH:
      setLineWidth(LINE_WIDTH_EDEFAULT);
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
    case NotationPackage.CONNECTOR_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      return getRoundedBendpointsRadius() != ROUNDED_BENDPOINTS_RADIUS_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__ROUTING:
      return getRouting() != ROUTING_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__SMOOTHNESS:
      return getSmoothness() != SMOOTHNESS_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__AVOID_OBSTRUCTIONS:
      return isAvoidObstructions() != AVOID_OBSTRUCTIONS_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__CLOSEST_DISTANCE:
      return isClosestDistance() != CLOSEST_DISTANCE_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_STATUS:
      return getJumpLinkStatus() != JUMP_LINK_STATUS_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINK_TYPE:
      return getJumpLinkType() != JUMP_LINK_TYPE_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__JUMP_LINKS_REVERSE:
      return isJumpLinksReverse() != JUMP_LINKS_REVERSE_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__LINE_COLOR:
      return getLineColor() != LINE_COLOR_EDEFAULT;
    case NotationPackage.CONNECTOR_STYLE__LINE_WIDTH:
      return getLineWidth() != LINE_WIDTH_EDEFAULT;
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
    if (baseClass == LineStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.CONNECTOR_STYLE__LINE_COLOR:
        return NotationPackage.LINE_STYLE__LINE_COLOR;
      case NotationPackage.CONNECTOR_STYLE__LINE_WIDTH:
        return NotationPackage.LINE_STYLE__LINE_WIDTH;
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
    if (baseClass == LineStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.LINE_STYLE__LINE_COLOR:
        return NotationPackage.CONNECTOR_STYLE__LINE_COLOR;
      case NotationPackage.LINE_STYLE__LINE_WIDTH:
        return NotationPackage.CONNECTOR_STYLE__LINE_WIDTH;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ConnectorStyleImpl

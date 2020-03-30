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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.Anchor;
import org.eclipse.gmf.runtime.notation.Bendpoints;
import org.eclipse.gmf.runtime.notation.Connector;
import org.eclipse.gmf.runtime.notation.ConnectorStyle;
import org.eclipse.gmf.runtime.notation.JumpLinkStatus;
import org.eclipse.gmf.runtime.notation.JumpLinkType;
import org.eclipse.gmf.runtime.notation.LineStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.RoundedCornersStyle;
import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.gmf.runtime.notation.RoutingStyle;
import org.eclipse.gmf.runtime.notation.Smoothness;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.View;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Connector</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#getRoundedBendpointsRadius <em>Rounded Bendpoints Radius</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#getRouting <em>Routing</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#getSmoothness <em>Smoothness</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#isAvoidObstructions <em>Avoid Obstructions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#isClosestDistance <em>Closest Distance</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#getJumpLinkStatus <em>Jump Link Status</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#getJumpLinkType <em>Jump Link Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#isJumpLinksReverse <em>Jump Links Reverse</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#getLineColor <em>Line Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ConnectorImpl#getLineWidth <em>Line Width</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConnectorImpl extends EdgeImpl implements Connector
{
  /**
   * The default value of the '{@link #getRoundedBendpointsRadius() <em>Rounded
   * Bendpoints Radius</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getRoundedBendpointsRadius()
   * @generated
   * @ordered
   */
  protected static final int ROUNDED_BENDPOINTS_RADIUS_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getRouting() <em>Routing</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getRouting()
   * @generated
   * @ordered
   */
  protected static final Routing ROUTING_EDEFAULT = Routing.MANUAL_LITERAL;

  /**
   * The default value of the '{@link #getSmoothness() <em>Smoothness</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getSmoothness()
   * @generated
   * @ordered
   */
  protected static final Smoothness SMOOTHNESS_EDEFAULT = Smoothness.NONE_LITERAL;

  /**
   * The default value of the '{@link #isAvoidObstructions() <em>Avoid Obstructions</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isAvoidObstructions()
   * @generated
   * @ordered
   */
  protected static final boolean AVOID_OBSTRUCTIONS_EDEFAULT = false;

  /**
   * The default value of the '{@link #isClosestDistance() <em>Closest Distance</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isClosestDistance()
   * @generated
   * @ordered
   */
  protected static final boolean CLOSEST_DISTANCE_EDEFAULT = false;

  /**
   * The default value of the '{@link #getJumpLinkStatus() <em>Jump Link Status</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getJumpLinkStatus()
   * @generated
   * @ordered
   */
  protected static final JumpLinkStatus JUMP_LINK_STATUS_EDEFAULT = JumpLinkStatus.NONE_LITERAL;

  /**
   * The default value of the '{@link #getJumpLinkType() <em>Jump Link Type</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getJumpLinkType()
   * @generated
   * @ordered
   */
  protected static final JumpLinkType JUMP_LINK_TYPE_EDEFAULT = JumpLinkType.SEMICIRCLE_LITERAL;

  /**
   * The default value of the '{@link #isJumpLinksReverse() <em>Jump Links Reverse</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isJumpLinksReverse()
   * @generated
   * @ordered
   */
  protected static final boolean JUMP_LINKS_REVERSE_EDEFAULT = false;

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
  protected ConnectorImpl()
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
    return NotationPackage.Literals.CONNECTOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Routing getRouting()
  {
    return (Routing)eDynamicGet(NotationPackage.CONNECTOR__ROUTING - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__ROUTING, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRouting(Routing newRouting)
  {
    eDynamicSet(NotationPackage.CONNECTOR__ROUTING - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__ROUTING, newRouting);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Smoothness getSmoothness()
  {
    return (Smoothness)eDynamicGet(NotationPackage.CONNECTOR__SMOOTHNESS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__SMOOTHNESS, true,
        true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSmoothness(Smoothness newSmoothness)
  {
    eDynamicSet(NotationPackage.CONNECTOR__SMOOTHNESS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__SMOOTHNESS, newSmoothness);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isAvoidObstructions()
  {
    return ((Boolean)eDynamicGet(NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.ROUTING_STYLE__AVOID_OBSTRUCTIONS, true, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAvoidObstructions(boolean newAvoidObstructions)
  {
    eDynamicSet(NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__AVOID_OBSTRUCTIONS,
        new Boolean(newAvoidObstructions));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isClosestDistance()
  {
    return ((Boolean)eDynamicGet(NotationPackage.CONNECTOR__CLOSEST_DISTANCE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__CLOSEST_DISTANCE,
        true, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setClosestDistance(boolean newClosestDistance)
  {
    eDynamicSet(NotationPackage.CONNECTOR__CLOSEST_DISTANCE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__CLOSEST_DISTANCE,
        new Boolean(newClosestDistance));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JumpLinkStatus getJumpLinkStatus()
  {
    return (JumpLinkStatus)eDynamicGet(NotationPackage.CONNECTOR__JUMP_LINK_STATUS - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_STATUS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setJumpLinkStatus(JumpLinkStatus newJumpLinkStatus)
  {
    eDynamicSet(NotationPackage.CONNECTOR__JUMP_LINK_STATUS - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_STATUS,
        newJumpLinkStatus);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JumpLinkType getJumpLinkType()
  {
    return (JumpLinkType)eDynamicGet(NotationPackage.CONNECTOR__JUMP_LINK_TYPE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_TYPE,
        true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setJumpLinkType(JumpLinkType newJumpLinkType)
  {
    eDynamicSet(NotationPackage.CONNECTOR__JUMP_LINK_TYPE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_TYPE, newJumpLinkType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isJumpLinksReverse()
  {
    return ((Boolean)eDynamicGet(NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.ROUTING_STYLE__JUMP_LINKS_REVERSE, true, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setJumpLinksReverse(boolean newJumpLinksReverse)
  {
    eDynamicSet(NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINKS_REVERSE,
        new Boolean(newJumpLinksReverse));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getRoundedBendpointsRadius()
  {
    return ((Integer)eDynamicGet(NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRoundedBendpointsRadius(int newRoundedBendpointsRadius)
  {
    eDynamicSet(NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS, new Integer(newRoundedBendpointsRadius));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getLineColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.CONNECTOR__LINE_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineColor(int newLineColor)
  {
    eDynamicSet(NotationPackage.CONNECTOR__LINE_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, new Integer(newLineColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getLineWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.CONNECTOR__LINE_WIDTH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineWidth(int newLineWidth)
  {
    eDynamicSet(NotationPackage.CONNECTOR__LINE_WIDTH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, new Integer(newLineWidth));
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
    case NotationPackage.CONNECTOR__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.CONNECTOR__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR__TYPE:
      return getType();
    case NotationPackage.CONNECTOR__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.CONNECTOR__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.CONNECTOR__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.CONNECTOR__STYLES:
      return getStyles();
    case NotationPackage.CONNECTOR__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.CONNECTOR__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.CONNECTOR__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.CONNECTOR__SOURCE:
      return getSource();
    case NotationPackage.CONNECTOR__TARGET:
      return getTarget();
    case NotationPackage.CONNECTOR__BENDPOINTS:
      return getBendpoints();
    case NotationPackage.CONNECTOR__SOURCE_ANCHOR:
      return getSourceAnchor();
    case NotationPackage.CONNECTOR__TARGET_ANCHOR:
      return getTargetAnchor();
    case NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS:
      return new Integer(getRoundedBendpointsRadius());
    case NotationPackage.CONNECTOR__ROUTING:
      return getRouting();
    case NotationPackage.CONNECTOR__SMOOTHNESS:
      return getSmoothness();
    case NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS:
      return isAvoidObstructions() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR__CLOSEST_DISTANCE:
      return isClosestDistance() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR__JUMP_LINK_STATUS:
      return getJumpLinkStatus();
    case NotationPackage.CONNECTOR__JUMP_LINK_TYPE:
      return getJumpLinkType();
    case NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE:
      return isJumpLinksReverse() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.CONNECTOR__LINE_COLOR:
      return new Integer(getLineColor());
    case NotationPackage.CONNECTOR__LINE_WIDTH:
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
    case NotationPackage.CONNECTOR__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.CONNECTOR__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.CONNECTOR__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.CONNECTOR__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.CONNECTOR__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.CONNECTOR__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.CONNECTOR__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.CONNECTOR__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.CONNECTOR__SOURCE:
      setSource((View)newValue);
      return;
    case NotationPackage.CONNECTOR__TARGET:
      setTarget((View)newValue);
      return;
    case NotationPackage.CONNECTOR__BENDPOINTS:
      setBendpoints((Bendpoints)newValue);
      return;
    case NotationPackage.CONNECTOR__SOURCE_ANCHOR:
      setSourceAnchor((Anchor)newValue);
      return;
    case NotationPackage.CONNECTOR__TARGET_ANCHOR:
      setTargetAnchor((Anchor)newValue);
      return;
    case NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(((Integer)newValue).intValue());
      return;
    case NotationPackage.CONNECTOR__ROUTING:
      setRouting((Routing)newValue);
      return;
    case NotationPackage.CONNECTOR__SMOOTHNESS:
      setSmoothness((Smoothness)newValue);
      return;
    case NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS:
      setAvoidObstructions(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR__CLOSEST_DISTANCE:
      setClosestDistance(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR__JUMP_LINK_STATUS:
      setJumpLinkStatus((JumpLinkStatus)newValue);
      return;
    case NotationPackage.CONNECTOR__JUMP_LINK_TYPE:
      setJumpLinkType((JumpLinkType)newValue);
      return;
    case NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE:
      setJumpLinksReverse(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.CONNECTOR__LINE_COLOR:
      setLineColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.CONNECTOR__LINE_WIDTH:
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
    case NotationPackage.CONNECTOR__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.CONNECTOR__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.CONNECTOR__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.CONNECTOR__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.CONNECTOR__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.CONNECTOR__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.CONNECTOR__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.CONNECTOR__SOURCE:
      setSource((View)null);
      return;
    case NotationPackage.CONNECTOR__TARGET:
      setTarget((View)null);
      return;
    case NotationPackage.CONNECTOR__BENDPOINTS:
      setBendpoints((Bendpoints)null);
      return;
    case NotationPackage.CONNECTOR__SOURCE_ANCHOR:
      setSourceAnchor((Anchor)null);
      return;
    case NotationPackage.CONNECTOR__TARGET_ANCHOR:
      setTargetAnchor((Anchor)null);
      return;
    case NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(ROUNDED_BENDPOINTS_RADIUS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__ROUTING:
      setRouting(ROUTING_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__SMOOTHNESS:
      setSmoothness(SMOOTHNESS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS:
      setAvoidObstructions(AVOID_OBSTRUCTIONS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__CLOSEST_DISTANCE:
      setClosestDistance(CLOSEST_DISTANCE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__JUMP_LINK_STATUS:
      setJumpLinkStatus(JUMP_LINK_STATUS_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__JUMP_LINK_TYPE:
      setJumpLinkType(JUMP_LINK_TYPE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE:
      setJumpLinksReverse(JUMP_LINKS_REVERSE_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__LINE_COLOR:
      setLineColor(LINE_COLOR_EDEFAULT);
      return;
    case NotationPackage.CONNECTOR__LINE_WIDTH:
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
    case NotationPackage.CONNECTOR__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.CONNECTOR__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.CONNECTOR__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.CONNECTOR__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.CONNECTOR__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.CONNECTOR__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.CONNECTOR__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.CONNECTOR__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.CONNECTOR__ELEMENT:
      return isSetElement();
    case NotationPackage.CONNECTOR__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.CONNECTOR__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.CONNECTOR__SOURCE:
      return getSource() != null;
    case NotationPackage.CONNECTOR__TARGET:
      return getTarget() != null;
    case NotationPackage.CONNECTOR__BENDPOINTS:
      return getBendpoints() != null;
    case NotationPackage.CONNECTOR__SOURCE_ANCHOR:
      return getSourceAnchor() != null;
    case NotationPackage.CONNECTOR__TARGET_ANCHOR:
      return getTargetAnchor() != null;
    case NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS:
      return getRoundedBendpointsRadius() != ROUNDED_BENDPOINTS_RADIUS_EDEFAULT;
    case NotationPackage.CONNECTOR__ROUTING:
      return getRouting() != ROUTING_EDEFAULT;
    case NotationPackage.CONNECTOR__SMOOTHNESS:
      return getSmoothness() != SMOOTHNESS_EDEFAULT;
    case NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS:
      return isAvoidObstructions() != AVOID_OBSTRUCTIONS_EDEFAULT;
    case NotationPackage.CONNECTOR__CLOSEST_DISTANCE:
      return isClosestDistance() != CLOSEST_DISTANCE_EDEFAULT;
    case NotationPackage.CONNECTOR__JUMP_LINK_STATUS:
      return getJumpLinkStatus() != JUMP_LINK_STATUS_EDEFAULT;
    case NotationPackage.CONNECTOR__JUMP_LINK_TYPE:
      return getJumpLinkType() != JUMP_LINK_TYPE_EDEFAULT;
    case NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE:
      return isJumpLinksReverse() != JUMP_LINKS_REVERSE_EDEFAULT;
    case NotationPackage.CONNECTOR__LINE_COLOR:
      return getLineColor() != LINE_COLOR_EDEFAULT;
    case NotationPackage.CONNECTOR__LINE_WIDTH:
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
    if (baseClass == Style.class)
    {
      switch (derivedFeatureID)
      {
      default:
        return -1;
      }
    }
    if (baseClass == RoundedCornersStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS:
        return NotationPackage.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS;
      default:
        return -1;
      }
    }
    if (baseClass == RoutingStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.CONNECTOR__ROUTING:
        return NotationPackage.ROUTING_STYLE__ROUTING;
      case NotationPackage.CONNECTOR__SMOOTHNESS:
        return NotationPackage.ROUTING_STYLE__SMOOTHNESS;
      case NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS:
        return NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS;
      case NotationPackage.CONNECTOR__CLOSEST_DISTANCE:
        return NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE;
      case NotationPackage.CONNECTOR__JUMP_LINK_STATUS:
        return NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS;
      case NotationPackage.CONNECTOR__JUMP_LINK_TYPE:
        return NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE;
      case NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE:
        return NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE;
      default:
        return -1;
      }
    }
    if (baseClass == LineStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.CONNECTOR__LINE_COLOR:
        return NotationPackage.LINE_STYLE__LINE_COLOR;
      case NotationPackage.CONNECTOR__LINE_WIDTH:
        return NotationPackage.LINE_STYLE__LINE_WIDTH;
      default:
        return -1;
      }
    }
    if (baseClass == ConnectorStyle.class)
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    if (baseClass == RoundedCornersStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS:
        return NotationPackage.CONNECTOR__ROUNDED_BENDPOINTS_RADIUS;
      default:
        return -1;
      }
    }
    if (baseClass == RoutingStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.ROUTING_STYLE__ROUTING:
        return NotationPackage.CONNECTOR__ROUTING;
      case NotationPackage.ROUTING_STYLE__SMOOTHNESS:
        return NotationPackage.CONNECTOR__SMOOTHNESS;
      case NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS:
        return NotationPackage.CONNECTOR__AVOID_OBSTRUCTIONS;
      case NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE:
        return NotationPackage.CONNECTOR__CLOSEST_DISTANCE;
      case NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS:
        return NotationPackage.CONNECTOR__JUMP_LINK_STATUS;
      case NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE:
        return NotationPackage.CONNECTOR__JUMP_LINK_TYPE;
      case NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE:
        return NotationPackage.CONNECTOR__JUMP_LINKS_REVERSE;
      default:
        return -1;
      }
    }
    if (baseClass == LineStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.LINE_STYLE__LINE_COLOR:
        return NotationPackage.CONNECTOR__LINE_COLOR;
      case NotationPackage.LINE_STYLE__LINE_WIDTH:
        return NotationPackage.CONNECTOR__LINE_WIDTH;
      default:
        return -1;
      }
    }
    if (baseClass == ConnectorStyle.class)
    {
      switch (baseFeatureID)
      {
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ConnectorImpl

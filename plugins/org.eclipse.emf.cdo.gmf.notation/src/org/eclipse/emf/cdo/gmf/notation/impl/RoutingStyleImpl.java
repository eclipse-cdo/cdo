/******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.JumpLinkStatus;
import org.eclipse.gmf.runtime.notation.JumpLinkType;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.gmf.runtime.notation.RoutingStyle;
import org.eclipse.gmf.runtime.notation.Smoothness;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Routing Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RoutingStyleImpl#getRouting <em>Routing</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RoutingStyleImpl#getSmoothness <em>Smoothness</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RoutingStyleImpl#isAvoidObstructions <em>Avoid Obstructions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RoutingStyleImpl#isClosestDistance <em>Closest Distance</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RoutingStyleImpl#getJumpLinkStatus <em>Jump Link Status</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RoutingStyleImpl#getJumpLinkType <em>Jump Link Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RoutingStyleImpl#isJumpLinksReverse <em>Jump Links Reverse</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class RoutingStyleImpl extends RoundedCornersStyleImpl implements RoutingStyle
{
  /**
  * The default value of the '{@link #getRouting() <em>Routing</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getRouting()
  * @generated
  * @ordered
  */
  protected static final Routing ROUTING_EDEFAULT = Routing.MANUAL_LITERAL;

  /**
  * The default value of the '{@link #getSmoothness() <em>Smoothness</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getSmoothness()
  * @generated
  * @ordered
  */
  protected static final Smoothness SMOOTHNESS_EDEFAULT = Smoothness.NONE_LITERAL;

  /**
  * The default value of the '{@link #isAvoidObstructions() <em>Avoid Obstructions</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isAvoidObstructions()
  * @generated
  * @ordered
  */
  protected static final boolean AVOID_OBSTRUCTIONS_EDEFAULT = false;

  /**
  * The default value of the '{@link #isClosestDistance() <em>Closest Distance</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isClosestDistance()
  * @generated
  * @ordered
  */
  protected static final boolean CLOSEST_DISTANCE_EDEFAULT = false;

  /**
  * The default value of the '{@link #getJumpLinkStatus() <em>Jump Link Status</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getJumpLinkStatus()
  * @generated
  * @ordered
  */
  protected static final JumpLinkStatus JUMP_LINK_STATUS_EDEFAULT = JumpLinkStatus.NONE_LITERAL;

  /**
  * The default value of the '{@link #getJumpLinkType() <em>Jump Link Type</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getJumpLinkType()
  * @generated
  * @ordered
  */
  protected static final JumpLinkType JUMP_LINK_TYPE_EDEFAULT = JumpLinkType.SEMICIRCLE_LITERAL;

  /**
  * The default value of the '{@link #isJumpLinksReverse() <em>Jump Links Reverse</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isJumpLinksReverse()
  * @generated
  * @ordered
  */
  protected static final boolean JUMP_LINKS_REVERSE_EDEFAULT = false;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected RoutingStyleImpl()
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
    return NotationPackage.Literals.ROUTING_STYLE;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public Routing getRouting()
  {
    return (Routing)eDynamicGet(NotationPackage.ROUTING_STYLE__ROUTING, NotationPackage.Literals.ROUTING_STYLE__ROUTING, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setRouting(Routing newRouting)
  {
    eDynamicSet(NotationPackage.ROUTING_STYLE__ROUTING, NotationPackage.Literals.ROUTING_STYLE__ROUTING, newRouting);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public Smoothness getSmoothness()
  {
    return (Smoothness)eDynamicGet(NotationPackage.ROUTING_STYLE__SMOOTHNESS, NotationPackage.Literals.ROUTING_STYLE__SMOOTHNESS, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setSmoothness(Smoothness newSmoothness)
  {
    eDynamicSet(NotationPackage.ROUTING_STYLE__SMOOTHNESS, NotationPackage.Literals.ROUTING_STYLE__SMOOTHNESS, newSmoothness);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isAvoidObstructions()
  {
    return ((Boolean)eDynamicGet(NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS, NotationPackage.Literals.ROUTING_STYLE__AVOID_OBSTRUCTIONS, true, true))
        .booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setAvoidObstructions(boolean newAvoidObstructions)
  {
    eDynamicSet(NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS, NotationPackage.Literals.ROUTING_STYLE__AVOID_OBSTRUCTIONS,
        new Boolean(newAvoidObstructions));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isClosestDistance()
  {
    return ((Boolean)eDynamicGet(NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE, NotationPackage.Literals.ROUTING_STYLE__CLOSEST_DISTANCE, true, true))
        .booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setClosestDistance(boolean newClosestDistance)
  {
    eDynamicSet(NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE, NotationPackage.Literals.ROUTING_STYLE__CLOSEST_DISTANCE, new Boolean(newClosestDistance));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public JumpLinkStatus getJumpLinkStatus()
  {
    return (JumpLinkStatus)eDynamicGet(NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_STATUS, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setJumpLinkStatus(JumpLinkStatus newJumpLinkStatus)
  {
    eDynamicSet(NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_STATUS, newJumpLinkStatus);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public JumpLinkType getJumpLinkType()
  {
    return (JumpLinkType)eDynamicGet(NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_TYPE, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setJumpLinkType(JumpLinkType newJumpLinkType)
  {
    eDynamicSet(NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINK_TYPE, newJumpLinkType);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isJumpLinksReverse()
  {
    return ((Boolean)eDynamicGet(NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINKS_REVERSE, true, true))
        .booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setJumpLinksReverse(boolean newJumpLinksReverse)
  {
    eDynamicSet(NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE, NotationPackage.Literals.ROUTING_STYLE__JUMP_LINKS_REVERSE,
        new Boolean(newJumpLinksReverse));
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
    case NotationPackage.ROUTING_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      return new Integer(getRoundedBendpointsRadius());
    case NotationPackage.ROUTING_STYLE__ROUTING:
      return getRouting();
    case NotationPackage.ROUTING_STYLE__SMOOTHNESS:
      return getSmoothness();
    case NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS:
      return isAvoidObstructions() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE:
      return isClosestDistance() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS:
      return getJumpLinkStatus();
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE:
      return getJumpLinkType();
    case NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE:
      return isJumpLinksReverse() ? Boolean.TRUE : Boolean.FALSE;
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
    case NotationPackage.ROUTING_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(((Integer)newValue).intValue());
      return;
    case NotationPackage.ROUTING_STYLE__ROUTING:
      setRouting((Routing)newValue);
      return;
    case NotationPackage.ROUTING_STYLE__SMOOTHNESS:
      setSmoothness((Smoothness)newValue);
      return;
    case NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS:
      setAvoidObstructions(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE:
      setClosestDistance(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS:
      setJumpLinkStatus((JumpLinkStatus)newValue);
      return;
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE:
      setJumpLinkType((JumpLinkType)newValue);
      return;
    case NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE:
      setJumpLinksReverse(((Boolean)newValue).booleanValue());
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
    case NotationPackage.ROUTING_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(ROUNDED_BENDPOINTS_RADIUS_EDEFAULT);
      return;
    case NotationPackage.ROUTING_STYLE__ROUTING:
      setRouting(ROUTING_EDEFAULT);
      return;
    case NotationPackage.ROUTING_STYLE__SMOOTHNESS:
      setSmoothness(SMOOTHNESS_EDEFAULT);
      return;
    case NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS:
      setAvoidObstructions(AVOID_OBSTRUCTIONS_EDEFAULT);
      return;
    case NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE:
      setClosestDistance(CLOSEST_DISTANCE_EDEFAULT);
      return;
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS:
      setJumpLinkStatus(JUMP_LINK_STATUS_EDEFAULT);
      return;
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE:
      setJumpLinkType(JUMP_LINK_TYPE_EDEFAULT);
      return;
    case NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE:
      setJumpLinksReverse(JUMP_LINKS_REVERSE_EDEFAULT);
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
    case NotationPackage.ROUTING_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      return getRoundedBendpointsRadius() != ROUNDED_BENDPOINTS_RADIUS_EDEFAULT;
    case NotationPackage.ROUTING_STYLE__ROUTING:
      return getRouting() != ROUTING_EDEFAULT;
    case NotationPackage.ROUTING_STYLE__SMOOTHNESS:
      return getSmoothness() != SMOOTHNESS_EDEFAULT;
    case NotationPackage.ROUTING_STYLE__AVOID_OBSTRUCTIONS:
      return isAvoidObstructions() != AVOID_OBSTRUCTIONS_EDEFAULT;
    case NotationPackage.ROUTING_STYLE__CLOSEST_DISTANCE:
      return isClosestDistance() != CLOSEST_DISTANCE_EDEFAULT;
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_STATUS:
      return getJumpLinkStatus() != JUMP_LINK_STATUS_EDEFAULT;
    case NotationPackage.ROUTING_STYLE__JUMP_LINK_TYPE:
      return getJumpLinkType() != JUMP_LINK_TYPE_EDEFAULT;
    case NotationPackage.ROUTING_STYLE__JUMP_LINKS_REVERSE:
      return isJumpLinksReverse() != JUMP_LINKS_REVERSE_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // RoutingStyleImpl

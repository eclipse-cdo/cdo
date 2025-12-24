/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.Edge;
import org.eclipse.emf.cdo.tests.model3.EdgeTarget;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeImpl#getSourceNode <em>Source Node</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.EdgeImpl#getTargetNode <em>Target Node</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EdgeImpl extends EObjectImpl implements Edge
{
  /**
   * The cached value of the '{@link #getSourceNode() <em>Source Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceNode()
   * @generated
   * @ordered
   */
  protected EdgeTarget sourceNode;

  /**
   * The cached value of the '{@link #getTargetNode() <em>Target Node</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetNode()
   * @generated
   * @ordered
   */
  protected EdgeTarget targetNode;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EdgeImpl()
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
    return Model3Package.eINSTANCE.getEdge();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EdgeTarget getSourceNode()
  {
    if (sourceNode != null && sourceNode.eIsProxy())
    {
      InternalEObject oldSourceNode = (InternalEObject)sourceNode;
      sourceNode = (EdgeTarget)eResolveProxy(oldSourceNode);
      if (sourceNode != oldSourceNode)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model3Package.EDGE__SOURCE_NODE, oldSourceNode, sourceNode));
        }
      }
    }
    return sourceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeTarget basicGetSourceNode()
  {
    return sourceNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSourceNode(EdgeTarget newSourceNode, NotificationChain msgs)
  {
    EdgeTarget oldSourceNode = sourceNode;
    sourceNode = newSourceNode;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model3Package.EDGE__SOURCE_NODE, oldSourceNode, newSourceNode);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSourceNode(EdgeTarget newSourceNode)
  {
    if (newSourceNode != sourceNode)
    {
      NotificationChain msgs = null;
      if (sourceNode != null)
      {
        msgs = ((InternalEObject)sourceNode).eInverseRemove(this, Model3Package.EDGE_TARGET__OUTGOING_EDGES, EdgeTarget.class, msgs);
      }
      if (newSourceNode != null)
      {
        msgs = ((InternalEObject)newSourceNode).eInverseAdd(this, Model3Package.EDGE_TARGET__OUTGOING_EDGES, EdgeTarget.class, msgs);
      }
      msgs = basicSetSourceNode(newSourceNode, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.EDGE__SOURCE_NODE, newSourceNode, newSourceNode));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EdgeTarget getTargetNode()
  {
    if (targetNode != null && targetNode.eIsProxy())
    {
      InternalEObject oldTargetNode = (InternalEObject)targetNode;
      targetNode = (EdgeTarget)eResolveProxy(oldTargetNode);
      if (targetNode != oldTargetNode)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model3Package.EDGE__TARGET_NODE, oldTargetNode, targetNode));
        }
      }
    }
    return targetNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EdgeTarget basicGetTargetNode()
  {
    return targetNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTargetNode(EdgeTarget newTargetNode, NotificationChain msgs)
  {
    EdgeTarget oldTargetNode = targetNode;
    targetNode = newTargetNode;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model3Package.EDGE__TARGET_NODE, oldTargetNode, newTargetNode);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTargetNode(EdgeTarget newTargetNode)
  {
    if (newTargetNode != targetNode)
    {
      NotificationChain msgs = null;
      if (targetNode != null)
      {
        msgs = ((InternalEObject)targetNode).eInverseRemove(this, Model3Package.EDGE_TARGET__INCOMING_EDGES, EdgeTarget.class, msgs);
      }
      if (newTargetNode != null)
      {
        msgs = ((InternalEObject)newTargetNode).eInverseAdd(this, Model3Package.EDGE_TARGET__INCOMING_EDGES, EdgeTarget.class, msgs);
      }
      msgs = basicSetTargetNode(newTargetNode, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.EDGE__TARGET_NODE, newTargetNode, newTargetNode));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model3Package.EDGE__SOURCE_NODE:
      if (sourceNode != null)
      {
        msgs = ((InternalEObject)sourceNode).eInverseRemove(this, Model3Package.EDGE_TARGET__OUTGOING_EDGES, EdgeTarget.class, msgs);
      }
      return basicSetSourceNode((EdgeTarget)otherEnd, msgs);
    case Model3Package.EDGE__TARGET_NODE:
      if (targetNode != null)
      {
        msgs = ((InternalEObject)targetNode).eInverseRemove(this, Model3Package.EDGE_TARGET__INCOMING_EDGES, EdgeTarget.class, msgs);
      }
      return basicSetTargetNode((EdgeTarget)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case Model3Package.EDGE__SOURCE_NODE:
      return basicSetSourceNode(null, msgs);
    case Model3Package.EDGE__TARGET_NODE:
      return basicSetTargetNode(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case Model3Package.EDGE__SOURCE_NODE:
      if (resolve)
      {
        return getSourceNode();
      }
      return basicGetSourceNode();
    case Model3Package.EDGE__TARGET_NODE:
      if (resolve)
      {
        return getTargetNode();
      }
      return basicGetTargetNode();
    }
    return super.eGet(featureID, resolve, coreType);
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
    case Model3Package.EDGE__SOURCE_NODE:
      setSourceNode((EdgeTarget)newValue);
      return;
    case Model3Package.EDGE__TARGET_NODE:
      setTargetNode((EdgeTarget)newValue);
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
    case Model3Package.EDGE__SOURCE_NODE:
      setSourceNode((EdgeTarget)null);
      return;
    case Model3Package.EDGE__TARGET_NODE:
      setTargetNode((EdgeTarget)null);
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
    case Model3Package.EDGE__SOURCE_NODE:
      return sourceNode != null;
    case Model3Package.EDGE__TARGET_NODE:
      return targetNode != null;
    }
    return super.eIsSet(featureID);
  }

} // EdgeImpl

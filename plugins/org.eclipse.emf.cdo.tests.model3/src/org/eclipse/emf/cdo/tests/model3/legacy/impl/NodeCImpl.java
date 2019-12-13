/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.NodeC;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Node C</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeCImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeCImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeCImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeCImpl#getOtherNodes <em>Other Nodes</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.NodeCImpl#getOppositeNodes <em>Opposite Nodes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NodeCImpl extends EObjectImpl implements NodeC
{
  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<NodeC> children;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getOtherNodes() <em>Other Nodes</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOtherNodes()
   * @generated
   * @ordered
   */
  protected EList<NodeC> otherNodes;

  /**
   * The cached value of the '{@link #getOppositeNodes() <em>Opposite Nodes</em>}' reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getOppositeNodes()
   * @generated
   * @ordered
   */
  protected EList<NodeC> oppositeNodes;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected NodeCImpl()
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
    return Model3Package.eINSTANCE.getNodeC();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<NodeC> getChildren()
  {
    if (children == null)
    {
      children = new EObjectContainmentWithInverseEList<NodeC>(NodeC.class, this, Model3Package.NODE_C__CHILDREN, Model3Package.NODE_C__PARENT);
    }
    return children;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeC getParent()
  {
    if (eContainerFeatureID() != Model3Package.NODE_C__PARENT)
    {
      return null;
    }
    return (NodeC)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(NodeC newParent, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newParent, Model3Package.NODE_C__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(NodeC newParent)
  {
    if (newParent != eInternalContainer() || eContainerFeatureID() != Model3Package.NODE_C__PARENT && newParent != null)
    {
      if (EcoreUtil.isAncestor(this, newParent))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newParent != null)
      {
        msgs = ((InternalEObject)newParent).eInverseAdd(this, Model3Package.NODE_C__CHILDREN, NodeC.class, msgs);
      }
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.NODE_C__PARENT, newParent, newParent));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.NODE_C__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<NodeC> getOtherNodes()
  {
    if (otherNodes == null)
    {
      otherNodes = new EObjectWithInverseResolvingEList.ManyInverse<NodeC>(NodeC.class, this, Model3Package.NODE_C__OTHER_NODES,
          Model3Package.NODE_C__OPPOSITE_NODES);
    }
    return otherNodes;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<NodeC> getOppositeNodes()
  {
    if (oppositeNodes == null)
    {
      oppositeNodes = new EObjectWithInverseResolvingEList.ManyInverse<NodeC>(NodeC.class, this, Model3Package.NODE_C__OPPOSITE_NODES,
          Model3Package.NODE_C__OTHER_NODES);
    }
    return oppositeNodes;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model3Package.NODE_C__CHILDREN:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
    case Model3Package.NODE_C__PARENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetParent((NodeC)otherEnd, msgs);
    case Model3Package.NODE_C__OTHER_NODES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getOtherNodes()).basicAdd(otherEnd, msgs);
    case Model3Package.NODE_C__OPPOSITE_NODES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getOppositeNodes()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model3Package.NODE_C__CHILDREN:
      return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
    case Model3Package.NODE_C__PARENT:
      return basicSetParent(null, msgs);
    case Model3Package.NODE_C__OTHER_NODES:
      return ((InternalEList<?>)getOtherNodes()).basicRemove(otherEnd, msgs);
    case Model3Package.NODE_C__OPPOSITE_NODES:
      return ((InternalEList<?>)getOppositeNodes()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case Model3Package.NODE_C__PARENT:
      return eInternalContainer().eInverseRemove(this, Model3Package.NODE_C__CHILDREN, NodeC.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case Model3Package.NODE_C__CHILDREN:
      return getChildren();
    case Model3Package.NODE_C__PARENT:
      return getParent();
    case Model3Package.NODE_C__NAME:
      return getName();
    case Model3Package.NODE_C__OTHER_NODES:
      return getOtherNodes();
    case Model3Package.NODE_C__OPPOSITE_NODES:
      return getOppositeNodes();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model3Package.NODE_C__CHILDREN:
      getChildren().clear();
      getChildren().addAll((Collection<? extends NodeC>)newValue);
      return;
    case Model3Package.NODE_C__PARENT:
      setParent((NodeC)newValue);
      return;
    case Model3Package.NODE_C__NAME:
      setName((String)newValue);
      return;
    case Model3Package.NODE_C__OTHER_NODES:
      getOtherNodes().clear();
      getOtherNodes().addAll((Collection<? extends NodeC>)newValue);
      return;
    case Model3Package.NODE_C__OPPOSITE_NODES:
      getOppositeNodes().clear();
      getOppositeNodes().addAll((Collection<? extends NodeC>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
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
    case Model3Package.NODE_C__CHILDREN:
      getChildren().clear();
      return;
    case Model3Package.NODE_C__PARENT:
      setParent((NodeC)null);
      return;
    case Model3Package.NODE_C__NAME:
      setName(NAME_EDEFAULT);
      return;
    case Model3Package.NODE_C__OTHER_NODES:
      getOtherNodes().clear();
      return;
    case Model3Package.NODE_C__OPPOSITE_NODES:
      getOppositeNodes().clear();
      return;
    }
    super.eUnset(featureID);
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
    case Model3Package.NODE_C__CHILDREN:
      return children != null && !children.isEmpty();
    case Model3Package.NODE_C__PARENT:
      return getParent() != null;
    case Model3Package.NODE_C__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case Model3Package.NODE_C__OTHER_NODES:
      return otherNodes != null && !otherNodes.isEmpty();
    case Model3Package.NODE_C__OPPOSITE_NODES:
      return oppositeNodes != null && !oppositeNodes.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // NodeCImpl

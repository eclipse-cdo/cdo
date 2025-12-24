/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.impl;

import org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage;
import org.eclipse.emf.cdo.ecore.dependencies.Element;
import org.eclipse.emf.cdo.ecore.dependencies.Link;
import org.eclipse.emf.cdo.ecore.dependencies.Model;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl#getModel <em>Model</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl#isExists <em>Exists</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl#getOutgoingLinks <em>Outgoing Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl#getIncomingLinks <em>Incoming Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl#getBrokenLinks <em>Broken Links</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ElementImpl extends MinimalEObjectImpl.Container implements Element
{
  /**
   * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUri()
   * @generated
   * @ordered
   */
  protected static final URI URI_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUri()
   * @generated
   * @ordered
   */
  protected URI uri = URI_EDEFAULT;

  /**
   * The default value of the '{@link #isExists() <em>Exists</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExists()
   * @generated
   * @ordered
   */
  protected static final boolean EXISTS_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isExists() <em>Exists</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isExists()
   * @generated
   * @ordered
   */
  protected boolean exists = EXISTS_EDEFAULT;

  /**
   * The cached value of the '{@link #getOutgoingLinks() <em>Outgoing Links</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutgoingLinks()
   * @generated
   * @ordered
   */
  protected EList<Link> outgoingLinks;

  /**
   * The cached value of the '{@link #getIncomingLinks() <em>Incoming Links</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIncomingLinks()
   * @generated
   * @ordered
   */
  protected EList<Link> incomingLinks;

  private Boolean brokenLinks;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ElementImpl()
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
    return DependenciesPackage.Literals.ELEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public URI getUri()
  {
    return uri;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUri(URI newUri)
  {
    URI oldUri = uri;
    uri = newUri;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.ELEMENT__URI, oldUri, uri));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Model getModel()
  {
    if (eContainerFeatureID() != DependenciesPackage.ELEMENT__MODEL)
    {
      return null;
    }
    return (Model)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetModel(Model newModel, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newModel, DependenciesPackage.ELEMENT__MODEL, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModel(Model newModel)
  {
    if (newModel != eInternalContainer() || eContainerFeatureID() != DependenciesPackage.ELEMENT__MODEL && newModel != null)
    {
      if (EcoreUtil.isAncestor(this, newModel))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newModel != null)
      {
        msgs = ((InternalEObject)newModel).eInverseAdd(this, DependenciesPackage.MODEL__ELEMENTS, Model.class, msgs);
      }
      msgs = basicSetModel(newModel, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.ELEMENT__MODEL, newModel, newModel));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isExists()
  {
    return exists;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setExists(boolean newExists)
  {
    boolean oldExists = exists;
    exists = newExists;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.ELEMENT__EXISTS, oldExists, exists));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Link> getOutgoingLinks()
  {
    if (outgoingLinks == null)
    {
      outgoingLinks = new EObjectContainmentWithInverseEList<>(Link.class, this, DependenciesPackage.ELEMENT__OUTGOING_LINKS,
          DependenciesPackage.LINK__SOURCE);
    }
    return outgoingLinks;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Link> getIncomingLinks()
  {
    if (incomingLinks == null)
    {
      incomingLinks = new EObjectWithInverseEList<>(Link.class, this, DependenciesPackage.ELEMENT__INCOMING_LINKS, DependenciesPackage.LINK__TARGET);
    }
    return incomingLinks;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Link> getBrokenLinks()
  {
    EList<Link> result = new EObjectEList<>(Link.class, this, DependenciesPackage.ELEMENT__BROKEN_LINKS);
    for (Link link : getOutgoingLinks())
    {
      if (link.isBroken())
      {
        result.add(link);
      }
    }

    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean hasBrokenLinks()
  {
    if (brokenLinks == null)
    {
      brokenLinks = false;

      for (Link link : getOutgoingLinks())
      {
        if (link.isBroken())
        {
          brokenLinks = true;
          break;
        }
      }
    }

    return brokenLinks;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case DependenciesPackage.ELEMENT__MODEL:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetModel((Model)otherEnd, msgs);
    case DependenciesPackage.ELEMENT__OUTGOING_LINKS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingLinks()).basicAdd(otherEnd, msgs);
    case DependenciesPackage.ELEMENT__INCOMING_LINKS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingLinks()).basicAdd(otherEnd, msgs);
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
    case DependenciesPackage.ELEMENT__MODEL:
      return basicSetModel(null, msgs);
    case DependenciesPackage.ELEMENT__OUTGOING_LINKS:
      return ((InternalEList<?>)getOutgoingLinks()).basicRemove(otherEnd, msgs);
    case DependenciesPackage.ELEMENT__INCOMING_LINKS:
      return ((InternalEList<?>)getIncomingLinks()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case DependenciesPackage.ELEMENT__MODEL:
      return eInternalContainer().eInverseRemove(this, DependenciesPackage.MODEL__ELEMENTS, Model.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case DependenciesPackage.ELEMENT__URI:
      return getUri();
    case DependenciesPackage.ELEMENT__MODEL:
      return getModel();
    case DependenciesPackage.ELEMENT__EXISTS:
      return isExists();
    case DependenciesPackage.ELEMENT__OUTGOING_LINKS:
      return getOutgoingLinks();
    case DependenciesPackage.ELEMENT__INCOMING_LINKS:
      return getIncomingLinks();
    case DependenciesPackage.ELEMENT__BROKEN_LINKS:
      return getBrokenLinks();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case DependenciesPackage.ELEMENT__URI:
      setUri((URI)newValue);
      return;
    case DependenciesPackage.ELEMENT__MODEL:
      setModel((Model)newValue);
      return;
    case DependenciesPackage.ELEMENT__EXISTS:
      setExists((Boolean)newValue);
      return;
    case DependenciesPackage.ELEMENT__OUTGOING_LINKS:
      getOutgoingLinks().clear();
      getOutgoingLinks().addAll((Collection<? extends Link>)newValue);
      return;
    case DependenciesPackage.ELEMENT__INCOMING_LINKS:
      getIncomingLinks().clear();
      getIncomingLinks().addAll((Collection<? extends Link>)newValue);
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
    case DependenciesPackage.ELEMENT__URI:
      setUri(URI_EDEFAULT);
      return;
    case DependenciesPackage.ELEMENT__MODEL:
      setModel((Model)null);
      return;
    case DependenciesPackage.ELEMENT__EXISTS:
      setExists(EXISTS_EDEFAULT);
      return;
    case DependenciesPackage.ELEMENT__OUTGOING_LINKS:
      getOutgoingLinks().clear();
      return;
    case DependenciesPackage.ELEMENT__INCOMING_LINKS:
      getIncomingLinks().clear();
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
    case DependenciesPackage.ELEMENT__URI:
      return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
    case DependenciesPackage.ELEMENT__MODEL:
      return getModel() != null;
    case DependenciesPackage.ELEMENT__EXISTS:
      return exists != EXISTS_EDEFAULT;
    case DependenciesPackage.ELEMENT__OUTGOING_LINKS:
      return outgoingLinks != null && !outgoingLinks.isEmpty();
    case DependenciesPackage.ELEMENT__INCOMING_LINKS:
      return incomingLinks != null && !incomingLinks.isEmpty();
    case DependenciesPackage.ELEMENT__BROKEN_LINKS:
      return !getBrokenLinks().isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case DependenciesPackage.ELEMENT___HAS_BROKEN_LINKS:
      return hasBrokenLinks();
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
    result.append(" (uri: ");
    result.append(uri);
    result.append(", exists: ");
    result.append(exists);
    result.append(')');
    return result.toString();
  }

} // ElementImpl

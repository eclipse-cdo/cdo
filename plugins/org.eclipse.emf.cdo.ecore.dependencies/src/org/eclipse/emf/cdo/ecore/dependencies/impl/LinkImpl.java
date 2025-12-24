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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Unresolved Proxy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl#isBroken <em>Broken</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LinkImpl extends MinimalEObjectImpl.Container implements Link
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
   * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTarget()
   * @generated
   * @ordered
   */
  protected Element target;

  /**
   * The cached value of the '{@link #getReference() <em>Reference</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReference()
   * @generated
   * @ordered
   */
  protected EReference reference;

  /**
   * The default value of the '{@link #isBroken() <em>Broken</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isBroken()
   * @generated
   * @ordered
   */
  protected static final boolean BROKEN_EDEFAULT = false;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LinkImpl()
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
    return DependenciesPackage.Literals.LINK;
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
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.LINK__URI, oldUri, uri));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Element getSource()
  {
    if (eContainerFeatureID() != DependenciesPackage.LINK__SOURCE)
    {
      return null;
    }
    return (Element)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSource(Element newSource, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newSource, DependenciesPackage.LINK__SOURCE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSource(Element newSource)
  {
    if (newSource != eInternalContainer() || eContainerFeatureID() != DependenciesPackage.LINK__SOURCE && newSource != null)
    {
      if (EcoreUtil.isAncestor(this, newSource))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newSource != null)
      {
        msgs = ((InternalEObject)newSource).eInverseAdd(this, DependenciesPackage.ELEMENT__OUTGOING_LINKS, Element.class, msgs);
      }
      msgs = basicSetSource(newSource, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.LINK__SOURCE, newSource, newSource));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Element getTarget()
  {
    return target;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTarget(Element newTarget, NotificationChain msgs)
  {
    Element oldTarget = target;
    target = newTarget;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DependenciesPackage.LINK__TARGET, oldTarget, newTarget);
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
  public void setTarget(Element newTarget)
  {
    if (newTarget != target)
    {
      NotificationChain msgs = null;
      if (target != null)
      {
        msgs = ((InternalEObject)target).eInverseRemove(this, DependenciesPackage.ELEMENT__INCOMING_LINKS, Element.class, msgs);
      }
      if (newTarget != null)
      {
        msgs = ((InternalEObject)newTarget).eInverseAdd(this, DependenciesPackage.ELEMENT__INCOMING_LINKS, Element.class, msgs);
      }
      msgs = basicSetTarget(newTarget, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.LINK__TARGET, newTarget, newTarget));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getReference()
  {
    if (reference != null && reference.eIsProxy())
    {
      InternalEObject oldReference = (InternalEObject)reference;
      reference = (EReference)eResolveProxy(oldReference);
      if (reference != oldReference)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, DependenciesPackage.LINK__REFERENCE, oldReference, reference));
        }
      }
    }
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference basicGetReference()
  {
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setReference(EReference newReference)
  {
    EReference oldReference = reference;
    reference = newReference;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DependenciesPackage.LINK__REFERENCE, oldReference, reference));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isBroken()
  {
    Element target = getTarget();
    return target == null || !target.isExists();
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
    case DependenciesPackage.LINK__SOURCE:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetSource((Element)otherEnd, msgs);
    case DependenciesPackage.LINK__TARGET:
      if (target != null)
      {
        msgs = ((InternalEObject)target).eInverseRemove(this, DependenciesPackage.ELEMENT__INCOMING_LINKS, Element.class, msgs);
      }
      return basicSetTarget((Element)otherEnd, msgs);
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
    case DependenciesPackage.LINK__SOURCE:
      return basicSetSource(null, msgs);
    case DependenciesPackage.LINK__TARGET:
      return basicSetTarget(null, msgs);
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
    case DependenciesPackage.LINK__SOURCE:
      return eInternalContainer().eInverseRemove(this, DependenciesPackage.ELEMENT__OUTGOING_LINKS, Element.class, msgs);
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
    case DependenciesPackage.LINK__URI:
      return getUri();
    case DependenciesPackage.LINK__SOURCE:
      return getSource();
    case DependenciesPackage.LINK__TARGET:
      return getTarget();
    case DependenciesPackage.LINK__REFERENCE:
      if (resolve)
      {
        return getReference();
      }
      return basicGetReference();
    case DependenciesPackage.LINK__BROKEN:
      return isBroken();
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
    case DependenciesPackage.LINK__URI:
      setUri((URI)newValue);
      return;
    case DependenciesPackage.LINK__SOURCE:
      setSource((Element)newValue);
      return;
    case DependenciesPackage.LINK__TARGET:
      setTarget((Element)newValue);
      return;
    case DependenciesPackage.LINK__REFERENCE:
      setReference((EReference)newValue);
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
    case DependenciesPackage.LINK__URI:
      setUri(URI_EDEFAULT);
      return;
    case DependenciesPackage.LINK__SOURCE:
      setSource((Element)null);
      return;
    case DependenciesPackage.LINK__TARGET:
      setTarget((Element)null);
      return;
    case DependenciesPackage.LINK__REFERENCE:
      setReference((EReference)null);
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
    case DependenciesPackage.LINK__URI:
      return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
    case DependenciesPackage.LINK__SOURCE:
      return getSource() != null;
    case DependenciesPackage.LINK__TARGET:
      return target != null;
    case DependenciesPackage.LINK__REFERENCE:
      return reference != null;
    case DependenciesPackage.LINK__BROKEN:
      return isBroken() != BROKEN_EDEFAULT;
    }
    return super.eIsSet(featureID);
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
    result.append(')');
    return result.toString();
  }

} // UnresolvedProxyImpl

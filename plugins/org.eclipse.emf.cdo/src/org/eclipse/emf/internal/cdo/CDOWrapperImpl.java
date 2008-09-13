/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Eike Stepper
 */
public abstract class CDOWrapperImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOWrapperImpl.class);

  protected CDOID id;

  protected CDOViewImpl view;

  protected InternalEObject instance;

  public CDOWrapperImpl()
  {
    super();
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOViewImpl cdoView()
  {
    return view;
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("id == null");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0} for {1}", id, instance);
    }

    this.id = id;
  }

  public void cdoInternalSetView(CDOView view)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting view: {0} for {1}", view, instance);
    }

    this.view = (CDOViewImpl)view;
  }

  public InternalEObject cdoInternalInstance()
  {
    return instance;
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    // TODO Implement method CDOWrapperImpl.cdoInternalDynamicFeature()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public EList<Adapter> eAdapters()
  {
    return instance.eAdapters();
  }

  public TreeIterator<EObject> eAllContents()
  {
    return instance.eAllContents();
  }

  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return instance.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications)
  {
    return instance.eBasicRemoveFromContainer(notifications);
  }

  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain notifications)
  {
    return instance.eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
  }

  public EClass eClass()
  {
    return instance.eClass();
  }

  public EObject eContainer()
  {
    return instance.eContainer();
  }

  public int eContainerFeatureID()
  {
    return instance.eContainerFeatureID();
  }

  public EStructuralFeature eContainingFeature()
  {
    return instance.eContainingFeature();
  }

  public EReference eContainmentFeature()
  {
    return instance.eContainmentFeature();
  }

  public EList<EObject> eContents()
  {
    return instance.eContents();
  }

  public EList<EObject> eCrossReferences()
  {
    return instance.eCrossReferences();
  }

  public boolean eDeliver()
  {
    return instance.eDeliver();
  }

  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return instance.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  public Resource.Internal eDirectResource()
  {
    return instance.eDirectResource();
  }

  public Object eGet(EStructuralFeature feature, boolean resolve, boolean coreType)
  {
    return instance.eGet(feature, resolve, coreType);
  }

  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return instance.eGet(feature, resolve);
  }

  public Object eGet(EStructuralFeature feature)
  {
    return instance.eGet(feature);
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    return instance.eGet(featureID, resolve, coreType);
  }

  public InternalEObject eInternalContainer()
  {
    return instance.eInternalContainer();
  }

  public Resource.Internal eInternalResource()
  {
    return instance.eInternalResource();
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return instance.eInverseAdd(otherEnd, featureID, baseClass, notifications);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return instance.eInverseRemove(otherEnd, featureID, baseClass, notifications);
  }

  public boolean eIsProxy()
  {
    return instance.eIsProxy();
  }

  public boolean eIsSet(EStructuralFeature feature)
  {
    return instance.eIsSet(feature);
  }

  public boolean eIsSet(int featureID)
  {
    return instance.eIsSet(featureID);
  }

  public boolean eNotificationRequired()
  {
    return instance.eNotificationRequired();
  }

  public void eNotify(Notification notification)
  {
    instance.eNotify(notification);
  }

  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    return instance.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  public URI eProxyURI()
  {
    return instance.eProxyURI();
  }

  public EObject eResolveProxy(InternalEObject proxy)
  {
    return instance.eResolveProxy(proxy);
  }

  public Resource eResource()
  {
    return instance.eResource();
  }

  public void eSet(EStructuralFeature feature, Object newValue)
  {
    instance.eSet(feature, newValue);
  }

  public void eSet(int featureID, Object newValue)
  {
    instance.eSet(featureID, newValue);
  }

  public void eSetClass(EClass class1)
  {
    instance.eSetClass(class1);
  }

  public void eSetDeliver(boolean deliver)
  {
    instance.eSetDeliver(deliver);
  }

  public void eSetProxyURI(URI uri)
  {
    instance.eSetProxyURI(uri);
  }

  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    return instance.eSetResource(resource, notifications);
  }

  public void eSetStore(EStore store)
  {
    instance.eSetStore(store);
  }

  public Setting eSetting(EStructuralFeature feature)
  {
    return instance.eSetting(feature);
  }

  public EStore eStore()
  {
    return instance.eStore();
  }

  public void eUnset(EStructuralFeature feature)
  {
    instance.eUnset(feature);
  }

  public void eUnset(int featureID)
  {
    instance.eUnset(featureID);
  }

  public String eURIFragmentSegment(EStructuralFeature feature, EObject object)
  {
    return instance.eURIFragmentSegment(feature, object);
  }

}

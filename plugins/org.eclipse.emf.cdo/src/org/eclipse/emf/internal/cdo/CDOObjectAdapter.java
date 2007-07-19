/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
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
import org.eclipse.emf.internal.cdo.bundle.OM;

/**
 * @author Eike Stepper
 */
public class CDOObjectAdapter extends AdapterImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOObjectAdapter.class);

  private CDOID id;

  private CDOState state;

  private CDOResourceImpl resource;

  private CDORevisionImpl revision;

  public CDOObjectAdapter()
  {
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public CDORevision cdoRevision()
  {
    return revision;
  }

  public CDOResource cdoResource()
  {
    return resource;
  }

  public CDOClass cdoClass()
  {
    return CDOObjectImpl.getCDOClass(this);
  }

  public CDOView cdoView()
  {
    return CDOObjectImpl.getCDOView(this);
  }

  public boolean cdoTransient()
  {
    return CDOObjectImpl.isCDOTransient(this);
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("id == null");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  public void cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      this.state = state;
    }
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (CDORevisionImpl)revision;
    revisionToEObject();
  }

  private void revisionToEObject()
  {
    InternalEObject eObject = getEObject();

    // TODO Implement method CDOObjectAdapter.revisionToEObject()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void cdoInternalSetView(CDOView view)
  {
    // Do nothing since getEObject() will never deliver a CDOResource
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resource: {0}", resource);
    }

    this.resource = (CDOResourceImpl)resource;
  }

  public void cdoInternalFinalizeRevision()
  {
    CDOObjectImpl.finalizeCDORevision(this, null, 0, null);

    // TODO Implement method CDOObjectAdapter.cdoInternalFinalizeRevision()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    // TODO Implement method CDOObjectAdapter.cdoInternalDynamicFeature()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public InternalEObject getEObject()
  {
    return (InternalEObject)target;
  }

  public EList<Adapter> eAdapters()
  {
    return getEObject().eAdapters();
  }

  public TreeIterator<EObject> eAllContents()
  {
    return getEObject().eAllContents();
  }

  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return getEObject().eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications)
  {
    return getEObject().eBasicRemoveFromContainer(notifications);
  }

  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain notifications)
  {
    return getEObject().eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
  }

  public EClass eClass()
  {
    return getEObject().eClass();
  }

  public EObject eContainer()
  {
    return getEObject().eContainer();
  }

  public int eContainerFeatureID()
  {
    return getEObject().eContainerFeatureID();
  }

  public EStructuralFeature eContainingFeature()
  {
    return getEObject().eContainingFeature();
  }

  public EReference eContainmentFeature()
  {
    return getEObject().eContainmentFeature();
  }

  public EList<EObject> eContents()
  {
    return getEObject().eContents();
  }

  public EList<EObject> eCrossReferences()
  {
    return getEObject().eCrossReferences();
  }

  public boolean eDeliver()
  {
    return getEObject().eDeliver();
  }

  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return getEObject().eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  public Resource.Internal eDirectResource()
  {
    return getEObject().eDirectResource();
  }

  public Object eGet(EStructuralFeature feature, boolean resolve, boolean coreType)
  {
    return getEObject().eGet(feature, resolve, coreType);
  }

  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return getEObject().eGet(feature, resolve);
  }

  public Object eGet(EStructuralFeature feature)
  {
    return getEObject().eGet(feature);
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    return getEObject().eGet(featureID, resolve, coreType);
  }

  public InternalEObject eInternalContainer()
  {
    return getEObject().eInternalContainer();
  }

  public Resource.Internal eInternalResource()
  {
    return getEObject().eInternalResource();
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return getEObject().eInverseAdd(otherEnd, featureID, baseClass, notifications);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return getEObject().eInverseRemove(otherEnd, featureID, baseClass, notifications);
  }

  public boolean eIsProxy()
  {
    return getEObject().eIsProxy();
  }

  public boolean eIsSet(EStructuralFeature feature)
  {
    return getEObject().eIsSet(feature);
  }

  public boolean eIsSet(int featureID)
  {
    return getEObject().eIsSet(featureID);
  }

  public boolean eNotificationRequired()
  {
    return getEObject().eNotificationRequired();
  }

  public void eNotify(Notification notification)
  {
    getEObject().eNotify(notification);
  }

  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    return getEObject().eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  public URI eProxyURI()
  {
    return getEObject().eProxyURI();
  }

  public EObject eResolveProxy(InternalEObject proxy)
  {
    return getEObject().eResolveProxy(proxy);
  }

  public Resource eResource()
  {
    return getEObject().eResource();
  }

  public void eSet(EStructuralFeature feature, Object newValue)
  {
    getEObject().eSet(feature, newValue);
  }

  public void eSet(int featureID, Object newValue)
  {
    getEObject().eSet(featureID, newValue);
  }

  public void eSetClass(EClass class1)
  {
    getEObject().eSetClass(class1);
  }

  public void eSetDeliver(boolean deliver)
  {
    getEObject().eSetDeliver(deliver);
  }

  public void eSetProxyURI(URI uri)
  {
    getEObject().eSetProxyURI(uri);
  }

  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    return getEObject().eSetResource(resource, notifications);
  }

  public void eSetStore(EStore store)
  {
    getEObject().eSetStore(store);
  }

  public Setting eSetting(EStructuralFeature feature)
  {
    return getEObject().eSetting(feature);
  }

  public EStore eStore()
  {
    return getEObject().eStore();
  }

  public void eUnset(EStructuralFeature feature)
  {
    getEObject().eUnset(feature);
  }

  public void eUnset(int featureID)
  {
    getEObject().eUnset(featureID);
  }

  public String eURIFragmentSegment(EStructuralFeature feature, EObject object)
  {
    return getEObject().eURIFragmentSegment(feature, object);
  }
}

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
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDOReferenceConverter;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
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

  @Override
  public InternalEObject getTarget()
  {
    return (InternalEObject)super.getTarget();
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type instanceof InternalEObject;
  }

  @Override
  public void notifyChanged(Notification msg)
  {
    // TODO Implement method CDOObjectAdapter.notifyChanged()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    if (newTarget instanceof InternalEObject)
    {
      super.setTarget(newTarget);
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + newTarget.getClass().getName());
    }
  }

  @Override
  public void unsetTarget(Notifier oldTarget)
  {
    if (oldTarget instanceof InternalEObject)
    {
      super.unsetTarget(oldTarget);
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + oldTarget.getClass().getName());
    }
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
    transferRevisionToTarget();
  }

  private void transferRevisionToTarget()
  {
    InternalEObject target = getTarget();
    CDOViewImpl view = (CDOViewImpl)cdoView();
    CDOPackageRegistryImpl packageRegistry = view.getSession().getPackageRegistry();
    CDOReferenceConverter converter = view.getReferenceConverter();

    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeatureImpl feature = features[i];
      // EStructuralFeature eFeature = ModelUtil.getEFeature(feature,
      // packageRegistry);

      CDOTypeImpl type = feature.getType();
      Object value = revision.getValue(feature);
      if (feature.isMany())
      {
      }
      else
      {
        if (feature.isReference())
        {
        }
        else
        {
        }
      }
    }
  }

  public void cdoInternalSetView(CDOView view)
  {
    // Do nothing since target will never be a CDOResource
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

  public EList<Adapter> eAdapters()
  {
    return getTarget().eAdapters();
  }

  public TreeIterator<EObject> eAllContents()
  {
    return getTarget().eAllContents();
  }

  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return getTarget().eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications)
  {
    return getTarget().eBasicRemoveFromContainer(notifications);
  }

  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain notifications)
  {
    return getTarget().eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
  }

  public EClass eClass()
  {
    return getTarget().eClass();
  }

  public EObject eContainer()
  {
    return getTarget().eContainer();
  }

  public int eContainerFeatureID()
  {
    return getTarget().eContainerFeatureID();
  }

  public EStructuralFeature eContainingFeature()
  {
    return getTarget().eContainingFeature();
  }

  public EReference eContainmentFeature()
  {
    return getTarget().eContainmentFeature();
  }

  public EList<EObject> eContents()
  {
    return getTarget().eContents();
  }

  public EList<EObject> eCrossReferences()
  {
    return getTarget().eCrossReferences();
  }

  public boolean eDeliver()
  {
    return getTarget().eDeliver();
  }

  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return getTarget().eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  public Resource.Internal eDirectResource()
  {
    return getTarget().eDirectResource();
  }

  public Object eGet(EStructuralFeature feature, boolean resolve, boolean coreType)
  {
    return getTarget().eGet(feature, resolve, coreType);
  }

  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return getTarget().eGet(feature, resolve);
  }

  public Object eGet(EStructuralFeature feature)
  {
    return getTarget().eGet(feature);
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    return getTarget().eGet(featureID, resolve, coreType);
  }

  public InternalEObject eInternalContainer()
  {
    return getTarget().eInternalContainer();
  }

  public Resource.Internal eInternalResource()
  {
    return getTarget().eInternalResource();
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return getTarget().eInverseAdd(otherEnd, featureID, baseClass, notifications);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return getTarget().eInverseRemove(otherEnd, featureID, baseClass, notifications);
  }

  public boolean eIsProxy()
  {
    return getTarget().eIsProxy();
  }

  public boolean eIsSet(EStructuralFeature feature)
  {
    return getTarget().eIsSet(feature);
  }

  public boolean eIsSet(int featureID)
  {
    return getTarget().eIsSet(featureID);
  }

  public boolean eNotificationRequired()
  {
    return getTarget().eNotificationRequired();
  }

  public void eNotify(Notification notification)
  {
    getTarget().eNotify(notification);
  }

  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    return getTarget().eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  public URI eProxyURI()
  {
    return getTarget().eProxyURI();
  }

  public EObject eResolveProxy(InternalEObject proxy)
  {
    return getTarget().eResolveProxy(proxy);
  }

  public Resource eResource()
  {
    return getTarget().eResource();
  }

  public void eSet(EStructuralFeature feature, Object newValue)
  {
    getTarget().eSet(feature, newValue);
  }

  public void eSet(int featureID, Object newValue)
  {
    getTarget().eSet(featureID, newValue);
  }

  public void eSetClass(EClass class1)
  {
    getTarget().eSetClass(class1);
  }

  public void eSetDeliver(boolean deliver)
  {
    getTarget().eSetDeliver(deliver);
  }

  public void eSetProxyURI(URI uri)
  {
    getTarget().eSetProxyURI(uri);
  }

  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    return getTarget().eSetResource(resource, notifications);
  }

  public void eSetStore(EStore store)
  {
    getTarget().eSetStore(store);
  }

  public Setting eSetting(EStructuralFeature feature)
  {
    return getTarget().eSetting(feature);
  }

  public EStore eStore()
  {
    return getTarget().eStore();
  }

  public void eUnset(EStructuralFeature feature)
  {
    getTarget().eUnset(feature);
  }

  public void eUnset(int featureID)
  {
    getTarget().eUnset(featureID);
  }

  public String eURIFragmentSegment(EStructuralFeature feature, EObject object)
  {
    return getTarget().eURIFragmentSegment(feature, object);
  }
}

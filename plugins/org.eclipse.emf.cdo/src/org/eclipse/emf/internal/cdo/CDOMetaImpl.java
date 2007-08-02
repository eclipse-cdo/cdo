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
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

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
public class CDOMetaImpl implements InternalCDOObject
{
  private CDOViewImpl view;

  private InternalEObject metaInstance;

  private CDOID id;

  public CDOMetaImpl(CDOViewImpl view, InternalEObject metaInstance, CDOID id)
  {
    this.view = view;
    this.metaInstance = metaInstance;
    this.id = id;
  }

  public CDOViewImpl getView()
  {
    return view;
  }

  public InternalEObject getMetaInstance()
  {
    return metaInstance;
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return CDOState.CLEAN;
  }

  public CDORevision cdoRevision()
  {
    throw new UnsupportedOperationException();
  }

  public CDOResource cdoResource()
  {
    throw new UnsupportedOperationException();
  }

  public CDOClass cdoClass()
  {
    throw new UnsupportedOperationException();
  }

  public CDOViewImpl cdoView()
  {
    return view;
  }

  public boolean cdoTransient()
  {
    return false;
  }

  public void cdoInternalSetID(CDOID id)
  {
    this.id = id;
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalSetView(CDOView view)
  {
    this.view = (CDOViewImpl)view;
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostLoad()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostAttach()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPreCommit()
  {
    throw new UnsupportedOperationException();
  }

  public InternalEObject cdoInternalInstance()
  {
    return metaInstance;
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    // TODO Implement method CDOMetaImpl.enclosing_method()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public String toString()
  {
    if (cdoID() == null)
    {
      return metaInstance.eClass().getName() + "?";
    }

    return metaInstance.eClass().getName() + "@" + cdoID();
  }

  public EList<Adapter> eAdapters()
  {
    return metaInstance.eAdapters();
  }

  public TreeIterator<EObject> eAllContents()
  {
    return metaInstance.eAllContents();
  }

  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return metaInstance.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications)
  {
    return metaInstance.eBasicRemoveFromContainer(notifications);
  }

  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain notifications)
  {
    return metaInstance.eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
  }

  public EClass eClass()
  {
    return metaInstance.eClass();
  }

  public EObject eContainer()
  {
    return metaInstance.eContainer();
  }

  public int eContainerFeatureID()
  {
    return metaInstance.eContainerFeatureID();
  }

  public EStructuralFeature eContainingFeature()
  {
    return metaInstance.eContainingFeature();
  }

  public EReference eContainmentFeature()
  {
    return metaInstance.eContainmentFeature();
  }

  public EList<EObject> eContents()
  {
    return metaInstance.eContents();
  }

  public EList<EObject> eCrossReferences()
  {
    return metaInstance.eCrossReferences();
  }

  public boolean eDeliver()
  {
    return metaInstance.eDeliver();
  }

  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return metaInstance.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  public Resource.Internal eDirectResource()
  {
    return metaInstance.eDirectResource();
  }

  public Object eGet(EStructuralFeature feature, boolean resolve, boolean coreType)
  {
    return metaInstance.eGet(feature, resolve, coreType);
  }

  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return metaInstance.eGet(feature, resolve);
  }

  public Object eGet(EStructuralFeature feature)
  {
    return metaInstance.eGet(feature);
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    return metaInstance.eGet(featureID, resolve, coreType);
  }

  public InternalEObject eInternalContainer()
  {
    return metaInstance.eInternalContainer();
  }

  public Resource.Internal eInternalResource()
  {
    return metaInstance.eInternalResource();
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return metaInstance.eInverseAdd(otherEnd, featureID, baseClass, notifications);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return metaInstance.eInverseRemove(otherEnd, featureID, baseClass, notifications);
  }

  public boolean eIsProxy()
  {
    return metaInstance.eIsProxy();
  }

  public boolean eIsSet(EStructuralFeature feature)
  {
    return metaInstance.eIsSet(feature);
  }

  public boolean eIsSet(int featureID)
  {
    return metaInstance.eIsSet(featureID);
  }

  public boolean eNotificationRequired()
  {
    return metaInstance.eNotificationRequired();
  }

  public void eNotify(Notification notification)
  {
    metaInstance.eNotify(notification);
  }

  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    return metaInstance.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  public URI eProxyURI()
  {
    return metaInstance.eProxyURI();
  }

  public EObject eResolveProxy(InternalEObject proxy)
  {
    return metaInstance.eResolveProxy(proxy);
  }

  public Resource eResource()
  {
    return metaInstance.eResource();
  }

  public void eSet(EStructuralFeature feature, Object newValue)
  {
    metaInstance.eSet(feature, newValue);
  }

  public void eSet(int featureID, Object newValue)
  {
    metaInstance.eSet(featureID, newValue);
  }

  public void eSetClass(EClass class1)
  {
    metaInstance.eSetClass(class1);
  }

  public void eSetDeliver(boolean deliver)
  {
    metaInstance.eSetDeliver(deliver);
  }

  public void eSetProxyURI(URI uri)
  {
    metaInstance.eSetProxyURI(uri);
  }

  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    return metaInstance.eSetResource(resource, notifications);
  }

  public void eSetStore(EStore store)
  {
    metaInstance.eSetStore(store);
  }

  public Setting eSetting(EStructuralFeature feature)
  {
    return metaInstance.eSetting(feature);
  }

  public EStore eStore()
  {
    return metaInstance.eStore();
  }

  public void eUnset(EStructuralFeature feature)
  {
    metaInstance.eUnset(feature);
  }

  public void eUnset(int featureID)
  {
    metaInstance.eUnset(featureID);
  }

  public String eURIFragmentSegment(EStructuralFeature feature, EObject object)
  {
    return metaInstance.eURIFragmentSegment(feature, object);
  }
}

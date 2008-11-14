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

import org.eclipse.emf.cdo.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.RWLockManager;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDOView extends CDOView, CDOIDProvider
{
  public InternalCDOSession getSession();

  public InternalCDOViewSet getViewSet();

  public void setViewSet(InternalCDOViewSet viewSet);

  public CDOFeatureAnalyzer getFeatureAnalyzer();

  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer);

  public CDOStore getStore();

  public InternalCDOTransaction toTransaction();

  public void attachResource(CDOResourceImpl resource);

  public void handleInvalidation(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedOIDs);

  public void handleChangeSubscription(Collection<CDORevisionDelta> deltas, Collection<CDOID> detachedObjects);

  public InternalCDOObject[] getObjectsArray();

  public void remapObject(CDOID oldID);

  public CDOID getResourceID(String path);

  public void registerProxyResource(CDOResourceImpl resource);

  public void registerObject(InternalCDOObject object);

  public void deregisterObject(InternalCDOObject object);

  public InternalCDOObject removeObject(CDOID id);

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand);

  public Object convertObjectToID(Object potentialObject);

  public Object convertObjectToID(Object potentialObject, boolean onlyPersistedID);

  public Object convertIDToObject(Object potentialID);

  public boolean isLocked(CDOObject object, RWLockManager.LockType lockType);

  public void subscribe(EObject eObject, Adapter adapter);

  public void unsubscribe(EObject eObject, Adapter adapter);

  public boolean hasSubscription(CDOID id);
}

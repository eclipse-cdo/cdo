/*
 * Copyright (c) 2009-2016, 2019-2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lob.CDOLobLoader;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;

import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOView extends CDOView, CDOIDProvider, CDOLobLoader, ILifecycle, IExecutorServiceProvider
{
  public void setViewID(int viewId);

  /**
   * @since 4.4
   */
  public void setProvider(CDOViewProvider provider);

  /**
   * @since 4.4
   */
  public String getRepositoryName();

  /**
   * @since 4.4
   */
  public void setRepositoryName(String repositoryName);

  @Override
  public InternalCDOSession getSession();

  public void setSession(InternalCDOSession session);

  @Override
  public InternalCDOViewSet getViewSet();

  public void setViewSet(InternalCDOViewSet viewSet);

  /**
   * Returns an unmodifiable map of the objects managed by this view.
   *
   * @since 4.0
   */
  public Map<CDOID, InternalCDOObject> getObjects();

  /**
   * @since 4.3
   */
  public List<InternalCDOObject> getObjectsList();

  /**
   * @since 4.0
   */
  public CDOStore getStore();

  public InternalCDOTransaction toTransaction();

  public void attachResource(CDOResourceImpl resource);

  /**
   * @since 4.5
   */
  public void resourceLoaded(CDOResourceImpl resource, boolean loaded);

  /**
   * @since 3.0
   */
  public void handleObjectStateChanged(InternalCDOObject object, CDOState oldState, CDOState newState);

  /**
   * @since 4.6
   */
  public void invalidate(ViewInvalidationData invalidationData);

  /**
   * @since 3.0
   */
  public void setLastUpdateTime(long lastUpdateTime);

  /**
   * @since 3.0
   */
  public void collectViewedRevisions(Map<CDOID, InternalCDORevision> revisions);

  public void remapObject(CDOID oldID);

  /**
   * @since 4.2
   */
  public void clearResourcePathCacheIfNecessary(CDORevisionDelta delta);

  public CDOID getResourceNodeID(String path);

  public void registerObject(InternalCDOObject object);

  public void deregisterObject(InternalCDOObject object);

  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand);

  /**
   * @since 3.0
   */
  public void prefetchRevisions(CDOID id, int depth);

  public Object convertObjectToID(Object potentialObject);

  public Object convertObjectToID(Object potentialObject, boolean onlyPersistedID);

  public Object convertIDToObject(Object potentialID);

  /**
   * @since 4.1
   */
  public boolean isObjectNew(CDOID id);

  public void handleAddAdapter(InternalCDOObject eObject, Adapter adapter);

  public void handleRemoveAdapter(InternalCDOObject eObject, Adapter adapter);

  public void subscribe(EObject eObject, Adapter adapter);

  public void unsubscribe(EObject eObject, Adapter adapter);

  public boolean hasSubscription(CDOID id);

  /**
   * @since 4.12
   */
  @Override
  public CDOLockOwner getLockOwner();

  /**
   * @since 3.0
   */
  public boolean isObjectLocked(CDOObject object, LockType lockType, boolean byOthers);

  /**
   * @since 4.1
   */
  public void handleLockNotification(InternalCDOView sender, CDOLockChangeInfo lockChangeInfo);

  /**
   * @since 4.2
   */
  public ViewAndState getViewAndState(CDOState state);

  /**
   * @since 4.5
   * @deprecated As of 4.29 use {@link #sync()}
   */
  @Deprecated
  public Object getViewMonitor();

  /**
   * @since 4.5
   * @deprecated As of 4.29 use {@link #sync()}
   */
  @Deprecated
  public void lockView();

  /**
   * @since 4.5
   * @deprecated As of 4.29 use {@link #sync()}
   */
  @Deprecated
  public void unlockView();

  /**
   * @since 4.15
   */
  public boolean isClosing();

  /**
   * @since 4.15
   */
  public void inverseClose();

  @Deprecated
  public CDOFeatureAnalyzer getFeatureAnalyzer();

  @Deprecated
  public void setFeatureAnalyzer(CDOFeatureAnalyzer featureAnalyzer);

  /**
   * @deprecated As of 4.2. use {@link #invalidate(CDOBranch, long, List, List, Map, boolean, boolean)}
   */
  @Deprecated
  public void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions, boolean async);

  /**
   * @since 4.2
   * @deprecated As of 4.6. use {@link #invalidate(ViewInvalidationData)}
   */
  @Deprecated
  public void invalidate(CDOBranch branch, long lastUpdateTime, List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects,
      Map<CDOID, InternalCDORevision> oldRevisions, boolean async, boolean clearResourcePathCache);

  /**
   * @deprecated No longer supported.
   */
  @Deprecated
  public void registerProxyResource(CDOResourceImpl resource);

  /**
   * @since 4.12
   * @deprecated As of 4.15 use {@link CDOLockStateCache#updateLockStates(CDOBranch, Collection, Collection, Consumer)}.
   */
  @Deprecated
  public void updateLockStates(CDOLockState[] newLockStates, boolean loadObjectsOnDemand, Consumer<CDOLockState> consumer);

  /**
   * Optimizes the storage of {@link CDOObject#cdoView()} and {@link CDOObject#cdoState()}. All objects of a view
   * share a small number of {@link CDOState} literals, so they are moved into a final AbstractCDOView.viewAndStates array.
   * For the {@link CDOState#TRANSIENT TRANSIENT} state, where there is no view associated with a {@link CDOObject}, this class
   * maintains a static {@link #VIEW_AND_STATES} array.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  public static final class ViewAndState
  {
    private static final CDOState[] STATE_VALUES = CDOState.values();

    private static final ViewAndState[] VIEW_AND_STATES = create(null);

    public static final ViewAndState TRANSIENT = VIEW_AND_STATES[CDOState.TRANSIENT.ordinal()];

    public final InternalCDOView view;

    public final CDOState state;

    public ViewAndState(InternalCDOView view, CDOState state)
    {
      this.view = view;
      this.state = state;
    }

    public ViewAndState getViewAndState(CDOState state)
    {
      if (view != null)
      {
        return view.getViewAndState(state);
      }

      return VIEW_AND_STATES[state.ordinal()];
    }

    @Override
    public String toString()
    {
      return "ViewAndState[view=" + view + ", state=" + state + "]";
    }

    public static ViewAndState[] create(InternalCDOView view)
    {
      ViewAndState[] viewAndStates = new ViewAndState[STATE_VALUES.length];
      for (CDOState state : STATE_VALUES)
      {
        viewAndStates[state.ordinal()] = new ViewAndState(view, state);
      }

      return viewAndStates;
    }
  }

  /**
   * A data structure that holds all input values of {@link InternalCDOView#invalidate(ViewInvalidationData) InternalCDOView#invalidate()}.
   *
   * @author Eike Stepper
   * @since 4.6
   */
  public static final class ViewInvalidationData
  {
    private CDOBranch branch;

    private long lastUpdateTime;

    private List<CDORevisionKey> allChangedObjects;

    private List<CDOIDAndVersion> allDetachedObjects;

    private Map<CDOID, InternalCDORevision> oldRevisions;

    private boolean async;

    private boolean clearResourcePathCache;

    private CDOLockChangeInfo lockChangeInfo;

    private byte securityImpact = CommitNotificationInfo.IMPACT_NONE;

    private Map<CDORevision, CDOPermission> oldPermissions;

    public ViewInvalidationData()
    {
    }

    public CDOBranch getBranch()
    {
      return branch;
    }

    public void setBranch(CDOBranch branch)
    {
      this.branch = branch;
    }

    public long getLastUpdateTime()
    {
      return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime)
    {
      this.lastUpdateTime = lastUpdateTime;
    }

    public List<CDORevisionKey> getAllChangedObjects()
    {
      return allChangedObjects;
    }

    public void setAllChangedObjects(List<CDORevisionKey> allChangedObjects)
    {
      this.allChangedObjects = allChangedObjects;
    }

    public List<CDOIDAndVersion> getAllDetachedObjects()
    {
      return allDetachedObjects;
    }

    public void setAllDetachedObjects(List<CDOIDAndVersion> allDetachedObjects)
    {
      this.allDetachedObjects = allDetachedObjects;
    }

    public Map<CDOID, InternalCDORevision> getOldRevisions()
    {
      return oldRevisions;
    }

    public void setOldRevisions(Map<CDOID, InternalCDORevision> oldRevisions)
    {
      this.oldRevisions = oldRevisions;
    }

    public boolean isAsync()
    {
      return async;
    }

    public void setAsync(boolean async)
    {
      this.async = async;
    }

    public boolean isClearResourcePathCache()
    {
      return clearResourcePathCache;
    }

    public void setClearResourcePathCache(boolean clearResourcePathCache)
    {
      this.clearResourcePathCache = clearResourcePathCache;
    }

    public CDOLockChangeInfo getLockChangeInfo()
    {
      return lockChangeInfo;
    }

    public void setLockChangeInfo(CDOLockChangeInfo lockChangeInfo)
    {
      this.lockChangeInfo = lockChangeInfo;
    }

    /**
     * @since 4.9
     */
    public byte getSecurityImpact()
    {
      return securityImpact;
    }

    /**
     * @since 4.9
     */
    public void setSecurityImpact(byte securityImpact)
    {
      this.securityImpact = securityImpact;
    }

    /**
     * @since 4.22
     */
    public Map<CDORevision, CDOPermission> getOldPermissions()
    {
      return oldPermissions;
    }

    /**
     * @since 4.22
     */
    public void setOldPermissions(Map<CDORevision, CDOPermission> oldPermissions)
    {
      this.oldPermissions = oldPermissions;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("ViewInvalidationData[branch=");
      builder.append(branch);
      builder.append(", lastUpdateTime=");
      builder.append(lastUpdateTime);
      builder.append(", allChangedObjects=");
      builder.append(allChangedObjects);
      builder.append(", allDetachedObjects=");
      builder.append(allDetachedObjects);
      builder.append(", oldRevisions=");
      builder.append(oldRevisions);
      builder.append(", async=");
      builder.append(async);
      builder.append(", clearResourcePathCache=");
      builder.append(clearResourcePathCache);
      builder.append(", lockChangeInfo=");
      builder.append(lockChangeInfo);
      builder.append(", securityImpact=");
      builder.append(securityImpact);
      builder.append(", oldPermissions=");
      builder.append(oldPermissions);
      builder.append("]");
      return builder.toString();
    }
  }
}

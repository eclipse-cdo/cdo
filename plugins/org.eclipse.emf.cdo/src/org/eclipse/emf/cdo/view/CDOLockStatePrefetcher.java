/*
 * Copyright (c) 2020, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionsLoadedEvent;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import org.eclipse.emf.spi.cdo.CDOLockStateCache;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

/**
 * Prefetches {@link CDOLockState lock states} for a given {@link #getView() view} when {@link CDORevision revisions} are loaded.
 *
 * @author Eike Stepper
 * @since 4.12
 */
public class CDOLockStatePrefetcher
{
  public static final Predicate<CDOID> DEFAULT_OBJECT_FILTER = id -> true;

  private final IListener revisionManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDORevisionsLoadedEvent)
      {
        CDORevisionsLoadedEvent e = (CDORevisionsLoadedEvent)event;

        if (asyncUpdate)
        {
          ExecutorService executorService = view.getExecutorService();
          executorService.submit(() -> updateLockStates(e));
        }
        else
        {
          updateLockStates(e);
        }
      }
    }
  };

  private final IListener viewListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle view)
    {
      dispose();
    }
  };

  private final InternalCDOView view;

  private final boolean asyncUpdate;

  private boolean updateOtherViews;

  private Predicate<CDOID> objectFilter = DEFAULT_OBJECT_FILTER;

  public CDOLockStatePrefetcher(CDOView view, boolean asyncUpdate)
  {
    this.view = (InternalCDOView)view;
    this.asyncUpdate = asyncUpdate;

    view.addListener(viewListener);
    view.getSession().getRevisionManager().addListener(revisionManagerListener);
  }

  public CDOLockStatePrefetcher(CDOView view)
  {
    this(view, true);
  }

  public final boolean isUpdateOtherViews()
  {
    return updateOtherViews;
  }

  public final void setUpdateOtherViews(boolean updateOtherViews)
  {
    this.updateOtherViews = updateOtherViews;
  }

  public final Predicate<CDOID> getObjectFilter()
  {
    return objectFilter;
  }

  public final void setObjectFilter(Predicate<CDOID> objectFilter)
  {
    this.objectFilter = objectFilter;
  }

  public final InternalCDOView getView()
  {
    return view;
  }

  public final boolean isAsyncUpdate()
  {
    return asyncUpdate;
  }

  public void dispose()
  {
    view.getSession().getRevisionManager().removeListener(revisionManagerListener);
    view.removeListener(viewListener);
  }

  protected final Object getLockTarget(CDOObject object)
  {
    return CDOViewImpl.getLockTarget(object);
  }

  protected final CDOLockState getLockState(CDOObject object)
  {
    CDOLockState[] lockStates = view.getLockStates(Collections.singleton(object.cdoID()), false);
    if (!ObjectUtil.isEmpty(lockStates))
    {
      return lockStates[0];
    }

    return null;
  }

  private void addMissingLockState(CDOObject object, List<CDOLockState> missingLockStates)
  {
    if (object != null && getLockState(object) == null)
    {
      Object lockedObject = getLockTarget(object); // CDOID or CDOIDAndBranch
      if (lockedObject != null)
      {
        missingLockStates.add(CDOLockUtil.createLockState(lockedObject));
      }
    }
  }

  private void updateLockStates(CDORevisionsLoadedEvent event)
  {
    view.sync().run(() -> {
      try
      {
        Set<CDOID> ids = new HashSet<>();
        Predicate<CDOID> filter = objectFilter;

        for (CDORevision revision : event.getPrimaryLoadedRevisions())
        {
          // Bug 466721 : Check null if it is a about a DetachedRevision
          if (revision != null)
          {
            CDOID id = revision.getID();
            if (id != null && filter.test(id))
            {
              // Don't ask to create an object for CDOResource as the caller of ResourceSet.getResource()
              // can have created it but not yet registered in the view. Don't ask others CDOResourceNode
              // either as it will create some load revisions request.
              boolean normalObject = !revision.isResourceNode();

              try
              {
                CDOObject object = view.getObject(id, normalObject);
                if (object != null)
                {
                  ids.add(id);
                }
              }
              catch (ObjectNotFoundException ex)
              {
                //$FALL-THROUGH$
              }
            }
          }
        }

        if (!ids.isEmpty())
        {
          // Direct call the session protocol.
          CDOSessionProtocol sessionProtocol = view.getSession().getSessionProtocol();
          List<CDOLockState> loadedLockStates = sessionProtocol.getLockStates2(view.getBranch().getID(), ids, event.getPrefetchDepth());

          updateLockStates(loadedLockStates, true);

          // Add missing lock states.
          List<CDOLockState> missingLockStates = new ArrayList<>();
          for (CDOID id : ids)
          {
            try
            {
              CDOObject object = view.getObject(id, false);
              if (object != null)
              {
                addMissingLockState(object, missingLockStates);
              }
            }
            catch (ObjectNotFoundException ex)
            {
              //$FALL-THROUGH$
            }
          }

          for (CDORevision revision : event.getAdditionalLoadedRevisions())
          {
            CDOID id = revision.getID();
            if (id != null && filter.test(id))
            {
              boolean normalObject = !revision.isResourceNode();

              try
              {
                CDOObject object = view.getObject(id, normalObject);
                if (object != null)
                {
                  addMissingLockState(object, missingLockStates);
                }
              }
              catch (ObjectNotFoundException ex)
              {
                //$FALL-THROUGH$
              }
            }
          }

          updateLockStates(missingLockStates, false);
        }
      }
      catch (Exception ex)
      {
        if (view.isActive())
        {
          OM.LOG.error(ex);
        }
      }
    });
  }

  private void updateLockStates(List<CDOLockState> lockStates, boolean loadOnDemand)
  {
    try
    {
      CDOBranch branch = view.getBranch();
      CDOLockStateCache lockStateCache = view.getSession().getLockStateCache();
      lockStateCache.addLockStates(branch, lockStates, null);
    }
    catch (Exception ex)
    {
      if (view.isActive())
      {
        OM.LOG.error(ex);
      }
    }
  }
}

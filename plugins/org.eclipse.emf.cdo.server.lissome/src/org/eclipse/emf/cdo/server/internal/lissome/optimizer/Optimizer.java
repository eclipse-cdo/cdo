/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.optimizer;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Eike Stepper
 */
public class Optimizer extends Lifecycle
{
  private final LissomeStore store;

  private final boolean async;

  private final LinkedList<OptimizerTask> queue = new LinkedList<OptimizerTask>();

  private Worker worker;

  private Cache cache;

  public Optimizer(LissomeStore store, boolean async)
  {
    this.store = store;
    this.async = async;
  }

  public LissomeStore getStore()
  {
    return store;
  }

  public boolean isAsync()
  {
    return async;
  }

  public OptimizerTask[] getTasks()
  {
    return getCache().getTasks();
  }

  public void addTask(OptimizerTask task)
  {
    synchronized (queue)
    {
      cache = null;
      queue.addLast(task);
      queue.notifyAll();
    }

    if (!async)
    {
      try
      {
        executeFirstTask();
      }
      catch (InterruptedException ignore)
      {
        // Can not happen
      }
    }
  }

  protected void executeFirstTask() throws InterruptedException
  {
    try
    {
      OptimizerTask task = getFirstTask();
      task.execute(this);
      removeFirstTask();
    }
    catch (InterruptedException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  protected OptimizerTask getFirstTask() throws InterruptedException
  {
    OptimizerTask task;
    synchronized (queue)
    {
      while (queue.isEmpty())
      {
        queue.wait(100);
      }

      task = queue.getFirst();
    }

    return task;
  }

  protected void removeFirstTask()
  {
    synchronized (queue)
    {
      cache = null;
      queue.removeFirst();
      queue.notifyAll();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (async)
    {
      worker = new TaskWorker();
      worker.activate();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (queue)
    {
      while (!queue.isEmpty())
      {
        queue.wait(100);
      }
    }

    if (async)
    {
      worker.deactivate();
      worker = null;
    }

    super.doDeactivate();
  }

  protected Cache getCache()
  {
    synchronized (queue)
    {
      if (cache == null)
      {
        boolean supportingBranches = store.getRepository().isSupportingBranches();
        cache = new Cache(queue, supportingBranches);
      }

      return cache;
    }
  }

  public boolean queryResources(IStoreAccessor.QueryResourcesContext context)
  {
    Cache cache = getCache();
    return cache.queryResources(context);
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    Cache cache = getCache();
    return cache.readRevision(id, branchPoint);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    // TODO: implement Optimizer.handleRevisions(eClass, branch, timeStamp, exactTime, handler)
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskWorker extends Worker
  {
    @Override
    protected String getThreadName()
    {
      return "OptimizerTaskWorker";
    }

    @Override
    protected void work(WorkContext context) throws Exception
    {
      executeFirstTask();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Cache
  {
    private final OptimizerTask[] tasks;

    private final boolean supportingBranches;

    private CDORevisionCache revisionCache;

    public Cache(LinkedList<OptimizerTask> queue, boolean supportingBranches)
    {
      this.supportingBranches = supportingBranches;
      tasks = queue.toArray(new OptimizerTask[queue.size()]);
    }

    public OptimizerTask[] getTasks()
    {
      return tasks;
    }

    public synchronized CDORevisionCache getRevisionCache()
    {
      if (revisionCache == null)
      {
        revisionCache = CDORevisionUtil.createRevisionCache(true, supportingBranches);

        CDORevisionCacheAdder adder = new CDORevisionCacheAdder()
        {
          @Override
          public void addRevision(CDORevision revision)
          {
            reviseOldRevision(revision);
            revisionCache.addRevision(revision);
          }

          private void reviseOldRevision(CDORevision revision)
          {
            int version = revision.getVersion();
            if (version > CDOBranchVersion.FIRST_VERSION)
            {
              CDOID id = revision.getID();
              CDOBranchVersion oldVersion = revision.getBranch().getVersion(version - 1);
              InternalCDORevision oldRevision = (InternalCDORevision)revisionCache.getRevisionByVersion(id, oldVersion);
              if (oldRevision != null)
              {
                oldRevision.setRevised(revision.getTimeStamp() - 1);
              }
            }
          }
        };

        for (OptimizerTask task : tasks)
        {
          if (task instanceof CommitTransactionTask)
          {
            CommitTransactionTask commitTask = (CommitTransactionTask)task;
            commitTask.cacheRevisions(adder);
          }
        }
      }

      return revisionCache;
    }

    public boolean queryResources(QueryResourcesContext context)
    {
      InternalCDORevisionCache revisionCache = (InternalCDORevisionCache)getRevisionCache();
      for (CDORevision revision : revisionCache.getRevisions(context))
      {
        if (!revision.isResourceNode())
        {
          continue;
        }

        CDOID folderID = (CDOID)revision.data().getContainerID();
        if (!ObjectUtil.equals(folderID, context.getFolderID()))
        {
          continue;
        }

        String name = context.getName();
        String revisionName = (String)revision.data().get(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, 0);
        boolean useEquals = context.exactMatch() || revisionName == null || name == null;
        boolean match = useEquals ? ObjectUtil.equals(revisionName, name) : revisionName.startsWith(name);
        if (!match)
        {
          continue;
        }

        if (!context.addResource(revision.getID()))
        {
          // No more results allowed
          return false;
        }
      }

      return true;
    }

    public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint)
    {
      CDORevisionCache revisionCache = getRevisionCache();
      return (InternalCDORevision)revisionCache.getRevision(id, branchPoint);
    }
  }
}

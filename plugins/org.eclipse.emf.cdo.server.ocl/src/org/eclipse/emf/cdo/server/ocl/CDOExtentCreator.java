/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionInterner;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An OCL {@link OCLExtentCreator extent creator} implementation for CDO.
 * <p>
 * The {@link #createExtent(EClass, AtomicBoolean) extent} of a {@link EClass class} X is the set of all {@link EObject objects} with <code>object.getEClass() == X</code>.
 *
 * @author Eike Stepper
 */
public class CDOExtentCreator implements OCLExtentCreator
{
  private CDOView view;

  private CDOChangeSetData changeSetData;

  private CDORevisionInterner revisionInterner;

  public CDOExtentCreator(CDOView view)
  {
    this.view = view;
  }

  public CDOView getView()
  {
    return view;
  }

  public CDOChangeSetData getChangeSetData()
  {
    return changeSetData;
  }

  public void setChangeSetData(CDOChangeSetData changeSetData)
  {
    this.changeSetData = changeSetData;
  }

  /**
   * @since 4.4
   */
  public CDORevisionInterner getRevisionInterner()
  {
    return revisionInterner;
  }

  /**
   * @since 4.4
   */
  public void setRevisionInterner(CDORevisionInterner revisionInterner)
  {
    this.revisionInterner = revisionInterner;
  }

  /**
   * @deprecated As of 4.4 use {@link #getRevisionInterner()}.
   */
  @Deprecated
  public org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder getRevisionCacheAdder()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated As of 4.4 use {@link #setRevisionInterner(CDORevisionInterner)}.
   */
  @Deprecated
  public void setRevisionCacheAdder(org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder revisionCacheAdder)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<EObject> createExtent(EClass eClass, AtomicBoolean canceled)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    CDOBranch branch = view.getBranch();
    long timeStamp = view.getTimeStamp();
    return createExtent(eClass, accessor, branch, timeStamp, canceled);
  }

  protected Set<EObject> createExtent(EClass eClass, IStoreAccessor accessor, CDOBranch branch, long timeStamp, final AtomicBoolean canceled)
  {
    final Set<EObject> extent = new HashSet<>();
    if (changeSetData != null)
    {
      List<CDOIDAndVersion> newObjects = changeSetData.getNewObjects();
      if (newObjects != null)
      {
        for (CDOIDAndVersion key : newObjects)
        {
          EObject object = getEObject(key.getID());
          if (object != null)
          {
            extent.add(object);
          }
        }
      }
    }

    CDORevisionHandler revisionHandler = new CDORevisionHandler.Filtered.Undetached(new CDORevisionHandler()
    {
      @Override
      public boolean handleRevision(CDORevision revision)
      {
        if (revisionInterner != null)
        {
          revision = revisionInterner.internRevision(revision);
        }

        CDOID id = revision.getID();
        if (!isDetached(id))
        {
          EObject object = getEObject(id);
          if (object != null)
          {
            extent.add(object);
          }
        }

        return !canceled.get();
      }
    });

    createExtent(eClass, accessor, branch, timeStamp, canceled, revisionHandler);
    return extent;
  }

  /**
   * @since 4.1
   */
  protected void createExtent(EClass eClass, IStoreAccessor accessor, CDOBranch branch, long timeStamp, final AtomicBoolean canceled,
      CDORevisionHandler revisionHandler)
  {
    handleRevisions(eClass, accessor, branch, timeStamp, revisionHandler);

    CDOPackageRegistry packageRegistry = accessor.getStore().getRepository().getPackageRegistry();
    List<EClass> subTypes = packageRegistry.getSubTypes().get(eClass);
    if (subTypes != null)
    {
      for (EClass subType : subTypes)
      {
        if (canceled.get())
        {
          break;
        }

        handleRevisions(subType, accessor, branch, timeStamp, revisionHandler);
      }
    }
  }

  /**
   * @since 4.2
   */
  protected void handleRevisions(EClass eClass, IStoreAccessor accessor, CDOBranch branch, long timeStamp, CDORevisionHandler revisionHandler)
  {
    if (!eClass.isAbstract() && !eClass.isInterface())
    {
      InternalRepository repository = (InternalRepository)accessor.getStore().getRepository();
      repository.handleRevisions(eClass, branch, false, timeStamp, false, revisionHandler);
    }
  }

  protected boolean isDetached(CDOID id)
  {
    if (changeSetData == null)
    {
      return false;
    }

    CDOChangeKind changeKind = changeSetData.getChangeKind(id);
    return changeKind == CDOChangeKind.DETACHED;
  }

  protected EObject getEObject(CDOID id) throws ObjectNotFoundException
  {
    InternalCDOObject object = (InternalCDOObject)view.getObject(id);
    if (object == null)
    {
      throw new ObjectNotFoundException(id);
    }

    return object.cdoInternalInstance();
  }

  /**
   * An {@link CDOExtentCreator extent creator} that creates extent sets which support a lazy populating iterator.
   *
   * @author Eike Stepper
   */
  public static class Lazy extends CDOExtentCreator
  {
    public Lazy(CDOView view)
    {
      super(view);
    }

    @Override
    protected Set<EObject> createExtent(EClass eClass, IStoreAccessor accessor, CDOBranch branch, long timeStamp, AtomicBoolean canceled)
    {
      return new Set<EObject>()
      {
        private final CountDownLatch emptyKnown = new CountDownLatch(1);

        private Iterator<EObject> emptyIterator;

        private Boolean empty;

        @Override
        public synchronized boolean isEmpty()
        {
          if (empty != null)
          {
            return empty;
          }

          emptyIterator = iterator();

          try
          {
            emptyKnown.await();
            return empty;
          }
          catch (InterruptedException ex)
          {
            throw new Error("Interrupted");
          }
        }

        @Override
        public synchronized Iterator<EObject> iterator()
        {
          if (emptyIterator != null)
          {
            Iterator<EObject> it = emptyIterator;
            emptyIterator = null;
            return it;
          }

          Object mutex = new Object();
          LinkedList<CDOID> ids = new LinkedList<>();
          boolean[] done = { false };

          class OCLExtentIterator implements Runnable
          {
            @Override
            public void run()
            {
              handleDirtyState();
              handlePersistentState();

              synchronized (mutex)
              {
                done[0] = true;
                mutex.notify();
              }

              if (empty == null)
              {
                empty = true;
                emptyKnown.countDown();
              }
            }

            private void handleDirtyState()
            {
              CDOChangeSetData changeSetData = getChangeSetData();
              if (changeSetData != null)
              {
                List<CDOIDAndVersion> newObjects = changeSetData.getNewObjects();
                if (newObjects != null)
                {
                  for (CDOIDAndVersion key : newObjects)
                  {
                    enqueue(key.getID());
                  }
                }
              }
            }

            private void handlePersistentState()
            {
              CDORevisionHandler revisionHandler = new CDORevisionHandler.Filtered.Undetached(new CDORevisionHandler()
              {
                @Override
                public boolean handleRevision(CDORevision revision)
                {
                  empty = false;
                  emptyKnown.countDown();

                  CDORevisionInterner revisionInterner = getRevisionInterner();
                  if (revisionInterner != null)
                  {
                    revision = revisionInterner.internRevision(revision);
                  }

                  CDOID id = revision.getID();
                  if (!isDetached(id))
                  {
                    enqueue(id);
                  }

                  return !canceled.get();
                }
              });

              createExtent(eClass, accessor, branch, timeStamp, canceled, revisionHandler);
            }

            private void enqueue(CDOID id)
            {
              synchronized (mutex)
              {
                ids.addLast(id);
                mutex.notify();
              }
            }
          }

          ISession serverSession = CDOServerUtil.getServerSession(getView());

          ExecutorService threadPool = ConcurrencyUtil.getExecutorService(getView());
          threadPool.submit(StoreThreadLocal.wrap(serverSession, new OCLExtentIterator()));

          return new Iterator<EObject>()
          {
            private CDOID next;

            @Override
            public boolean hasNext()
            {
              while (next == null)
              {
                if (canceled.get())
                {
                  return false;
                }

                synchronized (mutex)
                {
                  if (ids.isEmpty())
                  {
                    if (done[0])
                    {
                      return false;
                    }

                    try
                    {
                      mutex.wait(500L);
                    }
                    catch (InterruptedException ex)
                    {
                      throw new Error(ex);
                    }
                  }
                  else
                  {
                    next = ids.removeFirst();
                  }
                }
              }

              return true;
            }

            @Override
            public EObject next()
            {
              if (!hasNext())
              {
                throw new NoSuchElementException();
              }

              try
              {
                return getEObject(next);
              }
              finally
              {
                next = null;
              }
            }

            @Override
            public void remove()
            {
              throw new UnsupportedOperationException();
            }
          };
        }

        @Override
        public int size()
        {
          throw new Error("Not supported"); // RuntimeException gets swallowed up the stack!
        }

        @Override
        public boolean contains(Object o)
        {
          throw new Error("Not supported");
        }

        @Override
        public Object[] toArray()
        {
          throw new Error("Not supported");
        }

        @Override
        public <T> T[] toArray(T[] a)
        {
          throw new Error("Not supported");
        }

        @Override
        public boolean add(EObject o)
        {
          throw new Error("Not supported");
        }

        @Override
        public boolean remove(Object o)
        {
          throw new Error("Not supported");
        }

        @Override
        public boolean containsAll(Collection<?> c)
        {
          throw new Error("Not supported");
        }

        @Override
        public boolean addAll(Collection<? extends EObject> c)
        {
          throw new Error("Not supported");
        }

        @Override
        public boolean retainAll(Collection<?> c)
        {
          throw new Error("Not supported");
        }

        @Override
        public boolean removeAll(Collection<?> c)
        {
          throw new Error("Not supported");
        }

        @Override
        public void clear()
        {
          throw new Error("Not supported");
        }
      };
    }
  }
}

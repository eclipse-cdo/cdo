/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

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

  private CDORevisionCacheAdder revisionCacheAdder;

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

  public CDORevisionCacheAdder getRevisionCacheAdder()
  {
    return revisionCacheAdder;
  }

  public void setRevisionCacheAdder(CDORevisionCacheAdder revisionCacheAdder)
  {
    this.revisionCacheAdder = revisionCacheAdder;
  }

  public Set<EObject> createExtent(EClass eClass, AtomicBoolean canceled)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    CDOBranch branch = view.getBranch();
    long timeStamp = view.getTimeStamp();
    return createExtent(eClass, accessor, branch, timeStamp, canceled);
  }

  protected Set<EObject> createExtent(EClass eClass, IStoreAccessor accessor, CDOBranch branch, long timeStamp,
      final AtomicBoolean canceled)
  {
    final Set<EObject> extent = new HashSet<EObject>();
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

    accessor.handleRevisions(eClass, branch, timeStamp, false, new CDORevisionHandler.Filtered.Undetached(
        new CDORevisionHandler()
        {
          public boolean handleRevision(CDORevision revision)
          {
            if (revisionCacheAdder != null)
            {
              revisionCacheAdder.addRevision(revision);
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
        }));

    return extent;
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
    protected Set<EObject> createExtent(final EClass eClass, final IStoreAccessor accessor, final CDOBranch branch,
        final long timeStamp, final AtomicBoolean canceled)
    {
      return new Set<EObject>()
      {
        private Iterator<EObject> emptyIterator;

        private Boolean empty;

        private CountDownLatch emptyKnown = new CountDownLatch(1);

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

        public synchronized Iterator<EObject> iterator()
        {
          if (emptyIterator != null)
          {
            Iterator<EObject> it = emptyIterator;
            emptyIterator = null;
            return it;
          }

          final Object mutex = new Object();
          final LinkedList<CDOID> ids = new LinkedList<CDOID>();
          final boolean[] done = { false };

          Thread thread = new Thread("OCLExtentIterator")
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
              accessor.handleRevisions(eClass, branch, timeStamp, false, new CDORevisionHandler.Filtered.Undetached(
                  new CDORevisionHandler()
                  {
                    public boolean handleRevision(CDORevision revision)
                    {
                      empty = false;
                      emptyKnown.countDown();

                      CDORevisionCacheAdder revisionCacheAdder = getRevisionCacheAdder();
                      if (revisionCacheAdder != null)
                      {
                        revisionCacheAdder.addRevision(revision);
                      }

                      CDOID id = revision.getID();
                      if (!isDetached(id))
                      {
                        enqueue(id);
                      }

                      return !canceled.get();
                    }
                  }));
            }

            private void enqueue(CDOID id)
            {
              synchronized (mutex)
              {
                ids.addLast(id);
                mutex.notify();
              }
            }
          };

          thread.setDaemon(true);
          thread.start();

          return new Iterator<EObject>()
          {
            private CDOID next;

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
                      mutex.wait();
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

            public void remove()
            {
              throw new UnsupportedOperationException();
            }
          };
        }

        public int size()
        {
          throw new Error("Not supported"); // RuntimeException gets swallowed up the stack!
        }

        public boolean contains(Object o)
        {
          throw new Error("Not supported");
        }

        public Object[] toArray()
        {
          throw new Error("Not supported");
        }

        public <T> T[] toArray(T[] a)
        {
          throw new Error("Not supported");
        }

        public boolean add(EObject o)
        {
          throw new Error("Not supported");
        }

        public boolean remove(Object o)
        {
          throw new Error("Not supported");
        }

        public boolean containsAll(Collection<?> c)
        {
          throw new Error("Not supported");
        }

        public boolean addAll(Collection<? extends EObject> c)
        {
          throw new Error("Not supported");
        }

        public boolean retainAll(Collection<?> c)
        {
          throw new Error("Not supported");
        }

        public boolean removeAll(Collection<?> c)
        {
          throw new Error("Not supported");
        }

        public void clear()
        {
          throw new Error("Not supported");
        }
      };
    }
  }
}

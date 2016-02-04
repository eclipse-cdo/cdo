/*
 * Copyright (c) 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.server.IStoreAccessor.UnitSupport;
import org.eclipse.emf.cdo.server.IUnit;
import org.eclipse.emf.cdo.server.IUnitManager;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalUnitManager;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.container.Container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * @author Eike Stepper
 */
public class UnitManager extends Container<IUnit> implements InternalUnitManager
{
  private final InternalRepository repository;

  private final Map<CDOID, IUnit> units = CDOIDUtil.createMap();

  private final Map<CDOID, UnitInitializer> unitInitializers = CDOIDUtil.createMap();

  private final Set<ObjectAttacher> objectAttachers = new HashSet<ObjectAttacher>();

  private final ReentrantReadWriteLock managerLock = new ReentrantReadWriteLock();

  public UnitManager(InternalRepository repository)
  {
    this.repository = repository;
  }

  public final InternalRepository getRepository()
  {
    return repository;
  }

  public boolean isUnit(CDOID rootID)
  {
    checkActive();

    ReadLock readLock = managerLock.readLock();
    readLock.lock();

    try
    {
      // No need to synchronize on units because all other modifiers hold the manager write lock.
      return units.containsKey(rootID);
    }
    finally
    {
      readLock.unlock();
    }
  }

  public IUnit createUnit(CDOID rootID, IView view, CDORevisionHandler revisionHandler)
  {
    checkActive();

    WriteLock writeLock = managerLock.writeLock();
    UnitInitializer unitInitializer;
    boolean hook = false;

    ////////////////////////////////////
    // Phase 1: Register (short, locked)
    ////////////////////////////////////

    writeLock.lock();

    try
    {
      createUnitHook1();

      // No need to synchronize on units because all other access holds the manager lock.
      if (units.containsKey(rootID))
      {
        return null;
      }

      // No need to synchronize on unitInitializers because all other access holds the manager lock.
      unitInitializer = unitInitializers.get(rootID);
      if (unitInitializer != null)
      {
        hook = true;
      }
      else
      {
        checkNotNested(rootID, view, units.keySet());
        checkNotNested(rootID, view, unitInitializers.keySet());

        unitInitializer = createUnitInitializer(rootID, view, revisionHandler);

        // No need to synchronize on unitInitializers because all other access holds the manager lock.
        unitInitializers.put(rootID, unitInitializer);

        // Synchronize on objectAttachers because objectAttacherFinishedCommit() doesn't acquire the manager lock!
        synchronized (objectAttachers)
        {
          for (ObjectAttacher objectAttacher : objectAttachers)
          {
            List<CDOID> ids = objectAttacher.removeUnmappedRevisionsFor(unitInitializer);
            if (!ids.isEmpty())
            {
              unitInitializer.addObjectAttacher(objectAttacher, ids);
            }
          }
        }
      }
    }
    finally
    {
      writeLock.unlock();
    }

    if (hook)
    {
      return unitInitializer.hook(rootID, view, revisionHandler);
    }

    IUnit unit = null;

    try
    {
      /////////////////////////////////////////////////////
      // Phase 2: Initialize (potentially long, not locked)
      /////////////////////////////////////////////////////

      unit = unitInitializer.initialize();
    }
    finally
    {
      ///////////////////////////////////
      // Phase 3: Publish (short, locked)
      ///////////////////////////////////

      try
      {
        writeLock.lock();

        try
        {
          // No need to synchronize on unitInitializers because all other access holds the manager lock.
          unitInitializers.remove(rootID);

          if (unit != null)
          {
            // No need to synchronize on units because all other access holds the manager lock.
            units.put(rootID, unit);
          }
        }
        finally
        {
          writeLock.unlock();
        }
      }
      finally
      {
        unitInitializer.notifyHookedInitializers();
      }
    }

    fireElementAddedEvent(unit);
    return unit;
  }

  private void checkNotNested(CDOID rootID, IView view, Set<CDOID> unitIDs)
  {
    InternalCDORevision rootRevision = (InternalCDORevision)view.getRevision(rootID);
    CDOID unitID = getUnit(rootRevision, view, unitIDs);
    if (unitID != null)
    {
      throw new CDOException("Attempt to nest the new unit " + rootID + " in the existing unit " + unitID);
    }

    Set<CDOID> set = Collections.singleton(rootID);
    for (CDOID id : unitIDs)
    {
      InternalCDORevision revision = (InternalCDORevision)view.getRevision(id);
      if (getUnit(revision, view, set) != null)
      {
        throw new CDOException("Attempt to nest the existing unit " + id + " in the new unit " + rootID);
      }
    }
  }

  public IUnit getUnit(CDOID rootID)
  {
    checkActive();

    ReadLock readLock = managerLock.readLock();
    readLock.lock();

    try
    {
      // No need to synchronize on units because all other modifiers hold the manager write lock.
      return units.get(rootID);
    }
    finally
    {
      readLock.unlock();
    }
  }

  public IUnit[] getUnits()
  {
    checkActive();
    return getElements();
  }

  public IUnit[] getElements()
  {
    ReadLock readLock = managerLock.readLock();
    readLock.lock();

    try
    {
      // No need to synchronize on units because all other modifiers hold the manager write lock.
      return units.values().toArray(new IUnit[units.size()]);
    }
    finally
    {
      readLock.unlock();
    }
  }

  public InternalObjectAttacher attachObjects(InternalCommitContext commitContext)
  {
    checkActive();

    long timeStamp = commitContext.getTimeStamp();

    ObjectAttacher objectAttacher = null;
    Map<CDOID, CDOID> unitMappings = CDOIDUtil.createMap();

    ///////////////////////////////////////////////
    // Phase 1: Analyze new objects (short, locked)
    ///////////////////////////////////////////////

    ReadLock readLock = managerLock.readLock();
    readLock.lock();

    try
    {
      attachObjectsHook1();

      Set<CDOID> rootIDs = new HashSet<CDOID>();

      // No need to synchronize on units because all other modifiers hold the manager write lock.
      rootIDs.addAll(units.keySet());

      // No need to synchronize on unitInitializers because all other modifiers hold the manager write lock.
      rootIDs.addAll(unitInitializers.keySet());

      List<InternalCDORevision> unmappedRevisions = new ArrayList<InternalCDORevision>();
      boolean checkUnits = !rootIDs.isEmpty();

      for (InternalCDORevision revision : commitContext.getNewObjects())
      {
        if (checkUnits)
        {
          CDOID rootID = getUnit(revision, commitContext, rootIDs);
          if (rootID != null)
          {
            unitMappings.put(revision.getID(), rootID);
            continue;
          }
        }

        unmappedRevisions.add(revision);
      }

      if (!unmappedRevisions.isEmpty())
      {
        objectAttacher = createObjectAttacher(commitContext, unmappedRevisions);

        // Read lock holders must synchronize modifications of the private collections.
        synchronized (objectAttachers)
        {
          objectAttachers.add(objectAttacher);
        }
      }
    }
    finally
    {
      readLock.unlock();
    }

    //////////////////////////////////////////////////////////
    // Phase 2: Map objects to existing units (long, unlocked)
    //////////////////////////////////////////////////////////

    if (!unitMappings.isEmpty())
    {
      mapAttachedObjectsToUnits(commitContext, timeStamp, unitMappings);
    }

    return objectAttacher;
  }

  /**
   * Does not hold any manager lock when called.
   */
  public void objectAttacherFinishedCommit(ObjectAttacher objectAttacher)
  {
    checkActive();

    synchronized (objectAttachers)
    {
      objectAttachers.remove(objectAttacher);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    UnitSupport storeAccessor = (UnitSupport)repository.getStore().getReader(null);

    try
    {
      List<CDOID> roots = storeAccessor.readUnitRoots();
      for (CDOID root : roots)
      {
        IUnit unit = createUnit(root);

        // No need to synchronize on units because all other access call checkActive()
        units.put(root, unit);
      }
    }
    finally
    {
      storeAccessor.release();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    // No need to synchronize on units because all other access call checkActive()
    units.clear();

    super.doDeactivate();
  }

  protected Unit createUnit(CDOID root)
  {
    return new Unit(root);
  }

  protected UnitInitializer createUnitInitializer(CDOID rootID, IView view, CDORevisionHandler revisionHandler)
  {
    return new UnitInitializer(rootID, view, revisionHandler);
  }

  protected ObjectAttacher createObjectAttacher(InternalCommitContext commitContext,
      List<InternalCDORevision> unmappedRevisions)
  {
    return new ObjectAttacher(commitContext, unmappedRevisions);
  }

  protected void mapAttachedObjectsToUnits(InternalCommitContext commitContext, long timeStamp,
      Map<CDOID, CDOID> unitMappings)
  {
    UnitSupport storeAccessor = (UnitSupport)commitContext.getAccessor();
    storeAccessor.writeUnits(unitMappings, timeStamp);
  }

  protected void createUnitHook1()
  {
  }

  protected void attachObjectsHook1()
  {
  }

  private static CDOID getUnit(InternalCDORevision revision, CDORevisionProvider revisionProvider, Set<CDOID> rootIDs)
  {
    if (rootIDs.isEmpty())
    {
      return null;
    }

    CDOID id = revision.getID();
    if (rootIDs.contains(id))
    {
      return id;
    }

    CDORevision parentRevision = CDORevisionUtil.getParentRevision(revision, revisionProvider);
    if (parentRevision != null)
    {
      return getUnit((InternalCDORevision)parentRevision, revisionProvider, rootIDs);
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  protected class Unit implements IUnit
  {
    private final CDOID rootID;

    private final Set<IView> views = new HashSet<IView>();

    public Unit(CDOID rootID)
    {
      this.rootID = rootID;
    }

    public IUnitManager getManager()
    {
      return UnitManager.this;
    }

    public CDOID getRootID()
    {
      return rootID;
    }

    public boolean isOpen()
    {
      synchronized (views)
      {
        return !views.isEmpty();
      }
    }

    public void open(IView view, final CDORevisionHandler revisionHandler)
    {
      synchronized (views)
      {
        views.add(view);
      }

      UnitSupport storeAccessor = (UnitSupport)repository.getStore().getReader(null);

      try
      {
        storeAccessor.readUnit(view, rootID, revisionHandler);
      }
      finally
      {
        storeAccessor.release();
      }
    }

    public void close(IView view)
    {
      synchronized (views)
      {
        views.remove(view);
      }

      ((InternalView)view).closeUnit(rootID);
    }

    @Override
    public String toString()
    {
      return "Unit[" + rootID + "]";
    }

    /**
     * Does not hold any manager lock when called.
     */
    public void initialize(IView view, long timeStamp, CDORevisionHandler revisionHandler,
        Map<ObjectAttacher, List<CDOID>> objectAttachers)
    {
      UnitSupport storeAccessor = (UnitSupport)repository.getStore().getWriter(null);

      try
      {
        Set<CDOID> initializedIDs = new HashSet<CDOID>();
        Object initResult = storeAccessor.initUnit(view, rootID, revisionHandler, initializedIDs, timeStamp);

        List<CDOID> ids = new ArrayList<CDOID>();
        for (Entry<ObjectAttacher, List<CDOID>> entry : objectAttachers.entrySet())
        {
          ObjectAttacher objectAttacher = entry.getKey();
          if (objectAttacher.awaitFinishedCommit())
          {
            for (CDOID id : entry.getValue())
            {
              if (!initializedIDs.contains(id))
              {
                ids.add(id);
              }
            }
          }
        }

        storeAccessor.finishUnit(view, rootID, revisionHandler, timeStamp, initResult, ids);
      }
      finally
      {
        storeAccessor.release();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  protected class UnitInitializer implements CDORevisionHandler
  {
    private final long timeStamp = repository.getTimeStamp();

    private final Map<ObjectAttacher, List<CDOID>> concurrentObjectAttachers = new HashMap<ObjectAttacher, List<CDOID>>();

    private final CountDownLatch unitInitialized = new CountDownLatch(1);

    private final CDOID rootID;

    private final IView view;

    private final CDORevisionHandler revisionHandler;

    private final List<CDORevisionHandler> hookedRevisionHandlers = new CopyOnWriteArrayList<CDORevisionHandler>();

    private volatile boolean hasHookedRevisionHandlers;

    private Unit unit;

    public UnitInitializer(CDOID rootID, IView view, CDORevisionHandler revisionHandler)
    {
      this.rootID = rootID;
      this.view = view;
      this.revisionHandler = revisionHandler;
    }

    public CDOID getRootID()
    {
      return rootID;
    }

    /**
     * Does not hold any manager lock when called.
     */
    public IUnit initialize()
    {
      unit = new Unit(rootID);
      unit.initialize(view, timeStamp, revisionHandler, concurrentObjectAttachers);
      return unit;
    }

    /**
     * Does not hold any manager lock when called.
     */
    public IUnit hook(CDOID rootID, IView view, final CDORevisionHandler revisionHandler)
    {
      final Set<CDOID> ids = new HashSet<CDOID>();

      hookedRevisionHandlers.add(new CDORevisionHandler()
      {
        public boolean handleRevision(CDORevision revision)
        {
          ids.add(revision.getID());
          return revisionHandler.handleRevision(revision);
        }
      });

      // It's okay to do this unsynchronized. The worst thing that could happen is that the hooked revision handler is
      // missed a few times during UnitInitializer.handleRevision(), but that's okay because it probably missed many
      // revisions already and therefore performs an openUnit() subsequently, anyways. After all, hooked revision
      // handlers,
      // i.e., concurrent createUnit() calls for the same unit, are extremely rare.
      hasHookedRevisionHandlers = true;

      try
      {
        // Now wait for the main revision handler to finish.
        unitInitialized.await();
      }
      catch (InterruptedException ex)
      {
        return null;
      }

      // Now send the missed revisions.
      unit.open(view, new CDORevisionHandler()
      {
        public boolean handleRevision(CDORevision revision)
        {
          if (ids.contains(revision.getID()))
          {
            // This revision has already been sent. Skip to the next one.
            return true;
          }

          return revisionHandler.handleRevision(revision);
        }
      });

      return unit;
    }

    /**
     * Does not hold any manager lock when called.
     */
    public void notifyHookedInitializers()
    {
      unitInitialized.countDown();
    }

    public boolean handleRevision(CDORevision revision)
    {
      if (revisionHandler.handleRevision(revision))
      {
        if (hasHookedRevisionHandlers)
        {
          for (CDORevisionHandler hookedRevisionHandler : hookedRevisionHandlers)
          {
            hookedRevisionHandler.handleRevision(revision);
          }
        }

        return true;
      }

      return false;
    }

    /**
     * Holds the manager write lock when called.
     */
    public void addObjectAttacher(ObjectAttacher objectAttacher, List<CDOID> ids)
    {
      concurrentObjectAttachers.put(objectAttacher, ids);
    }
  }

  /**
   * @author Eike Stepper
   */
  protected class ObjectAttacher implements InternalObjectAttacher
  {
    private final InternalCommitContext commitContext;

    private final List<InternalCDORevision> unmappedRevisions;

    private final CountDownLatch commitFinished = new CountDownLatch(1);

    private boolean commitSucceeded;

    public ObjectAttacher(InternalCommitContext commitContext, List<InternalCDORevision> unmappedRevisions)
    {
      this.commitContext = commitContext;
      this.unmappedRevisions = unmappedRevisions;
    }

    /**
     * Does not hold any manager lock when called.
     */
    public void finishedCommit(boolean success)
    {
      objectAttacherFinishedCommit(this);

      commitSucceeded = success;
      commitFinished.countDown();
    }

    /**
     * Holds the manager write lock when called.
     */
    public List<CDOID> removeUnmappedRevisionsFor(UnitInitializer unitInitializer)
    {
      List<CDOID> ids = new ArrayList<CDOID>();

      Set<CDOID> rootIDs = Collections.singleton(unitInitializer.getRootID());
      for (Iterator<InternalCDORevision> it = unmappedRevisions.iterator(); it.hasNext();)
      {
        InternalCDORevision revision = it.next();
        if (getUnit(revision, commitContext, rootIDs) != null)
        {
          ids.add(revision.getID());
          it.remove();
        }
      }

      return ids;
    }

    public boolean awaitFinishedCommit()
    {
      try
      {
        commitFinished.await();
      }
      catch (InterruptedException ex)
      {
        return false;
      }

      return commitSucceeded;
    }
  }
}

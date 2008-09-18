/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/215688    
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Eike Stepper & Simon McDuff - http://bugs.eclipse.org/204890
 *    Simon McDuff - http://bugs.eclipse.org/246705
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDORevisionManager;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.protocol.VerifyRevisionRequest;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.fsm.FiniteStateMachine;
import org.eclipse.net4j.util.fsm.ITransition;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOStateMachine extends FiniteStateMachine<CDOState, CDOEvent, InternalCDOObject>
{
  // @Singleton
  public static final CDOStateMachine INSTANCE = new CDOStateMachine();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_STATEMACHINE, CDOStateMachine.class);

  private InternalCDOObject lastTracedObject;

  private CDOState lastTracedState;

  private CDOEvent lastTracedEvent;

  @SuppressWarnings("unchecked")
  private CDOStateMachine()
  {
    super(CDOState.class, CDOEvent.class);

    init(CDOState.TRANSIENT, CDOEvent.PREPARE, new PrepareTransition());
    init(CDOState.TRANSIENT, CDOEvent.ATTACH, FAIL);
    init(CDOState.TRANSIENT, CDOEvent.DETACH, IGNORE);
    init(CDOState.TRANSIENT, CDOEvent.READ, IGNORE);
    init(CDOState.TRANSIENT, CDOEvent.WRITE, IGNORE);
    init(CDOState.TRANSIENT, CDOEvent.INVALIDATE, FAIL);
    init(CDOState.TRANSIENT, CDOEvent.RELOAD, IGNORE);
    init(CDOState.TRANSIENT, CDOEvent.COMMIT, FAIL);
    init(CDOState.TRANSIENT, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.PREPARED, CDOEvent.PREPARE, FAIL);
    init(CDOState.PREPARED, CDOEvent.ATTACH, new AttachTransition());
    init(CDOState.PREPARED, CDOEvent.DETACH, FAIL);
    init(CDOState.PREPARED, CDOEvent.READ, IGNORE);
    init(CDOState.PREPARED, CDOEvent.WRITE, FAIL);
    init(CDOState.PREPARED, CDOEvent.INVALIDATE, FAIL);
    init(CDOState.PREPARED, CDOEvent.RELOAD, FAIL);
    init(CDOState.PREPARED, CDOEvent.COMMIT, FAIL);
    init(CDOState.PREPARED, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.NEW, CDOEvent.PREPARE, FAIL);
    init(CDOState.NEW, CDOEvent.ATTACH, FAIL);
    init(CDOState.NEW, CDOEvent.DETACH, new DetachTransition());
    init(CDOState.NEW, CDOEvent.READ, IGNORE);
    init(CDOState.NEW, CDOEvent.WRITE, new WriteNewTransition());
    init(CDOState.NEW, CDOEvent.INVALIDATE, FAIL);
    init(CDOState.NEW, CDOEvent.RELOAD, FAIL);
    init(CDOState.NEW, CDOEvent.COMMIT, new CommitTransition());
    init(CDOState.NEW, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.CLEAN, CDOEvent.PREPARE, FAIL);
    init(CDOState.CLEAN, CDOEvent.ATTACH, FAIL);
    init(CDOState.CLEAN, CDOEvent.DETACH, new DetachTransition());
    init(CDOState.CLEAN, CDOEvent.READ, IGNORE);
    init(CDOState.CLEAN, CDOEvent.WRITE, new WriteTransition());
    init(CDOState.CLEAN, CDOEvent.INVALIDATE, new InvalidateTransition());
    init(CDOState.CLEAN, CDOEvent.RELOAD, new ReloadTransition());
    init(CDOState.CLEAN, CDOEvent.COMMIT, FAIL);
    init(CDOState.CLEAN, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.DIRTY, CDOEvent.PREPARE, FAIL);
    init(CDOState.DIRTY, CDOEvent.ATTACH, FAIL);
    init(CDOState.DIRTY, CDOEvent.DETACH, new DetachTransition());
    init(CDOState.DIRTY, CDOEvent.READ, IGNORE);
    init(CDOState.DIRTY, CDOEvent.WRITE, new RewriteTransition());
    init(CDOState.DIRTY, CDOEvent.INVALIDATE, new ConflictTransition());
    init(CDOState.DIRTY, CDOEvent.RELOAD, new ReloadTransition());
    init(CDOState.DIRTY, CDOEvent.COMMIT, new CommitTransition());
    init(CDOState.DIRTY, CDOEvent.ROLLBACK, new RollbackTransition());

    init(CDOState.PROXY, CDOEvent.PREPARE, FAIL);
    init(CDOState.PROXY, CDOEvent.ATTACH, FAIL);
    init(CDOState.PROXY, CDOEvent.DETACH, new DetachTransition());
    init(CDOState.PROXY, CDOEvent.READ, new LoadTransition(false));
    init(CDOState.PROXY, CDOEvent.WRITE, new LoadTransition(true));
    init(CDOState.PROXY, CDOEvent.INVALIDATE, IGNORE);
    init(CDOState.PROXY, CDOEvent.RELOAD, new ReloadTransition());
    init(CDOState.PROXY, CDOEvent.COMMIT, FAIL);
    init(CDOState.PROXY, CDOEvent.ROLLBACK, FAIL);

    init(CDOState.CONFLICT, CDOEvent.PREPARE, FAIL);
    init(CDOState.CONFLICT, CDOEvent.ATTACH, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.DETACH, new DetachTransition());
    init(CDOState.CONFLICT, CDOEvent.READ, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.WRITE, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.INVALIDATE, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.RELOAD, FAIL);
    init(CDOState.CONFLICT, CDOEvent.COMMIT, IGNORE);
    init(CDOState.CONFLICT, CDOEvent.ROLLBACK, new RollbackTransition());
  }

  /**
   * object is already attached in EMF world. It contains all the information needed to know where it will be connected.
   * We used a mapOfContents only for performance reason.
   * 
   * @since 2.0
   */
  public void attach(InternalCDOObject object, CDOTransactionImpl transaction)
  {
    Map<InternalCDOObject, List<InternalCDOObject>> mapOfContents = new HashMap<InternalCDOObject, List<InternalCDOObject>>();
    attach1(object, new Pair<CDOTransactionImpl, Map<InternalCDOObject, List<InternalCDOObject>>>(transaction,
        mapOfContents));
    attach2(object, mapOfContents);
  }

  /**
   * Phase 1: TRANSIENT --> PREPARED
   */
  private void attach1(InternalCDOObject object,
      Pair<CDOTransactionImpl, Map<InternalCDOObject, List<InternalCDOObject>>> transactionAndMapOfContents)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("PREPARE: {0} --> {1}", object, transactionAndMapOfContents.getElement1());
    }

    process(object, CDOEvent.PREPARE, transactionAndMapOfContents);
  }

  /**
   * Phase 2: PREPARED --> NEW
   */
  private void attach2(InternalCDOObject object, Map<InternalCDOObject, List<InternalCDOObject>> mapOfContents)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("ATTACH: {0} --> {1}", object, mapOfContents);
    }

    process(object, CDOEvent.ATTACH, mapOfContents);
  }

  public void detach(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.DETACH);
    }

    process(object, CDOEvent.DETACH, null);
  }

  public void read(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.READ);
    }

    process(object, CDOEvent.READ, null);
  }

  public void write(InternalCDOObject object)
  {
    write(object, null);
  }

  public void write(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.WRITE);
    }

    process(object, CDOEvent.WRITE, featureDelta);
  }

  public void reload(InternalCDOObject... objects)
  {
    CDOView view = null;
    Map<CDOID, InternalCDOObject> ids = new HashMap<CDOID, InternalCDOObject>();
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    List<InternalCDORevision> revised = new ArrayList<InternalCDORevision>();
    for (InternalCDOObject object : objects)
    {
      CDOState state = object.cdoState();
      if (state != CDOState.TRANSIENT && state != CDOState.PREPARED && state != CDOState.NEW
          && state != CDOState.CONFLICT)
      {
        if (view == null)
        {
          view = object.cdoView();
        }

        InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
        if (revision.isCurrent())
        {
          revisions.add(revision);
        }
        else
        {
          revised.add(revision);
        }

        ids.put(object.cdoID(), object);
      }
    }

    if (view != null)
    {
      try
      {
        CDOSession session = view.getSession();
        IChannel channel = session.getChannel();
        IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
        VerifyRevisionRequest request = new VerifyRevisionRequest(channel, revisions);
        revisions = failOverStrategy.send(request);
      }
      catch (Exception ex)
      {
        throw new TransportException(ex);
      }

      revisions.addAll(revised);
      for (InternalCDORevision revision : revisions)
      {
        InternalCDOObject object = ids.get(revision.getID());
        if (TRACER.isEnabled())
        {
          trace(object, CDOEvent.RELOAD);
        }

        process(object, CDOEvent.RELOAD, null);
      }
    }
  }

  public void invalidate(InternalCDOObject object, long timeStamp)
  {
    invalidate(object, false, timeStamp);
  }

  /**
   * @since 2.0
   */
  public void invalidate(InternalCDOObject object, boolean detach, long timeStamp)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.INVALIDATE);
    }

    process(object, CDOEvent.INVALIDATE, new Pair<Boolean, Long>(detach, timeStamp));
  }

  public void commit(InternalCDOObject object, CommitTransactionResult result)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.COMMIT);
    }

    process(object, CDOEvent.COMMIT, result);
  }

  public void rollback(InternalCDOObject object, boolean remote)
  {
    if (TRACER.isEnabled())
    {
      trace(object, CDOEvent.ROLLBACK);
    }

    process(object, CDOEvent.ROLLBACK, remote);
  }

  @Override
  protected CDOState getState(InternalCDOObject object)
  {
    return object.cdoState();
  }

  @Override
  protected void setState(InternalCDOObject object, CDOState state)
  {
    object.cdoInternalSetState(state);
  }

  /**
   * Removes clutter from the trace log
   */
  private void trace(InternalCDOObject object, CDOEvent event)
  {
    CDOState state = object.cdoState();
    if (lastTracedObject != object || lastTracedState != state || lastTracedEvent != event)
    {
      TRACER.format("{0}: {1}", event, object.getClass().getName());
      lastTracedObject = object;
      lastTracedState = state;
      lastTracedEvent = event;
    }
  }

  @SuppressWarnings("unused")
  private void testAttach(InternalCDOObject object)
  {
    process(object, CDOEvent.ATTACH, null);
  }

  @SuppressWarnings("unused")
  private void testReload(InternalCDOObject object)
  {
    process(object, CDOEvent.RELOAD, null);
  }

  /**
   * Prepares a tree of transient objects to be subsequently {@link AttachTransition attached} to a CDOView.
   * <p>
   * Execution is recursive and includes:
   * <ol>
   * <li>Assignment of a new {@link CDOIDTemp}
   * <li>Assignment of a new {@link CDORevision}
   * <li>Bidirectional association with the {@link CDOView}
   * <li>Registration with the {@link CDOTransaction}
   * <li>Changing state to {@link CDOState#PREPARED PREPARED}
   * </ol>
   * 
   * @see AttachTransition
   * @author Eike Stepper
   */
  @SuppressWarnings("unchecked")
  private final class PrepareTransition
      implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, Pair<CDOTransactionImpl, Map<InternalCDOObject, List<InternalCDOObject>>>>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event,
        Pair<CDOTransactionImpl, Map<InternalCDOObject, List<InternalCDOObject>>> transactionAndMapOfContents)
    {
      CDOTransactionImpl transaction = transactionAndMapOfContents.getElement1();
      Map<InternalCDOObject, List<InternalCDOObject>> mapOfContents = transactionAndMapOfContents.getElement2();

      CDORevisionManager revisionManager = transaction.getSession().getRevisionManager();

      // Prepare object
      CDOID id = transaction.getNextTemporaryID();
      object.cdoInternalSetID(id);
      object.cdoInternalSetView(transaction);
      changeState(object, CDOState.PREPARED);

      // Create new revision
      CDOClass cdoClass = object.cdoClass();
      InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(revisionManager, cdoClass, id);
      revision.setVersion(-1);

      object.cdoInternalSetRevision(revision);

      // Register object
      transaction.registerObject(object);
      transaction.registerNew(object);

      // Build a list to access the same object in AttachTransition.
      // This is an optimization. Accessing object from eStore/getObject takes more time than directly.
      List<InternalCDOObject> contents = new ArrayList<InternalCDOObject>();

      // Prepare content tree
      for (Iterator<InternalCDOObject> it = FSMUtil.getProperContents(object); it.hasNext();)
      {
        InternalCDOObject content = it.next();
        contents.add(content);
        INSTANCE.process(content, CDOEvent.PREPARE,
            new Pair<CDOTransactionImpl, Map<InternalCDOObject, List<InternalCDOObject>>>(transaction, mapOfContents));
      }
      mapOfContents.put(object, contents.size() == 0 ? Collections.EMPTY_LIST : contents);
    }
  }

  /**
   * Attaches a tree of {@link PrepareTransition prepared} objects to a CDOView.
   * <p>
   * Execution is recursive and includes:
   * <ol>
   * <li>Calling {@link InternalCDOObject#cdoInternalPostAttach()},<br>
   * which includes for {@link CDOObjectImpl}:
   * <ol>
   * <li>Population of the CDORevision with the current values in
   * {@link EStoreEObjectImpl#eSetting(org.eclipse.emf.ecore.EStructuralFeature) eSettings}
   * <li>Unsetting {@link EStoreEObjectImpl#eSetting(org.eclipse.emf.ecore.EStructuralFeature) eSettings}
   * </ol>
   * <li>Changing state to {@link CDOState#NEW NEW}
   * </ol>
   * 
   * @see PrepareTransition
   * @author Eike Stepper
   */
  private final class AttachTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, Map<InternalCDOObject, List<InternalCDOObject>>>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event,
        Map<InternalCDOObject, List<InternalCDOObject>> mapOfContents)
    {
      object.cdoInternalPostAttach();
      changeState(object, CDOState.NEW);

      // Prepare content tree
      List<InternalCDOObject> contents = mapOfContents.get(object);
      for (InternalCDOObject content : contents)
      {
        // TODO Just call execute()?!
        INSTANCE.process(content, CDOEvent.ATTACH, mapOfContents);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DetachTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object NULL)
    {
      CDOTransactionImpl transaction = (CDOTransactionImpl)object.cdoView();

      transaction.detachObject(object);

      object.cdoInternalPostDetach();
      object.cdoInternalSetState(CDOState.TRANSIENT);

      boolean isResource = object instanceof Resource;
      // Prepare content tree
      for (Iterator<EObject> it = object.eContents().iterator(); it.hasNext();)
      {
        InternalEObject eObject = (InternalEObject)it.next();
        boolean isDirectlyConnected = isResource && eObject.eDirectResource() == object;
        if (isDirectlyConnected || eObject.eDirectResource() == null)
        {
          InternalCDOObject content = FSMUtil.adapt(eObject, transaction);
          if (content != null)
          {
            INSTANCE.process(content, CDOEvent.DETACH, object);
          }
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CommitTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, CommitTransactionResult>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, CommitTransactionResult data)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      Map<CDOIDTemp, CDOID> idMappings = data.getIDMappings();

      // Adjust object
      CDOID id = object.cdoID();
      CDOID newID = idMappings.get(id);
      if (newID != null)
      {
        object.cdoInternalSetID(newID);
        view.remapObject(id);
        id = newID;
      }

      // Adjust revision
      InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
      revision.setID(id);
      revision.setUntransactional();
      revision.setCreated(data.getTimeStamp());
      revision.adjustReferences(idMappings);

      CDORevisionManagerImpl revisionManager = view.getSession().getRevisionManager();
      revisionManager.addCachedRevision(revision);

      changeState(object, CDOState.CLEAN);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class RollbackTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Boolean>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Boolean remote)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();

      // Adjust object
      CDOID id = object.cdoID();
      CDORevision transactionalRevision = object.cdoRevision();
      int version = transactionalRevision.getVersion();

      CDORevisionManagerImpl revisionManager = view.getSession().getRevisionManager();
      InternalCDORevision previousRevision = revisionManager.getRevisionByVersion(id, 0, version - 1);
      object.cdoInternalSetRevision(previousRevision);

      changeState(object, remote ? CDOState.PROXY : CDOState.CLEAN);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class WriteTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object featureDelta)
    {
      // Copy revision
      InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.copy(object.cdoRevision());
      revision.setTransactional();
      object.cdoInternalSetRevision(revision);

      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.registerDirty(object, (CDOFeatureDelta)featureDelta);
      changeState(object, CDOState.DIRTY);
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class WriteNewTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object featureDelta)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.registerFeatureDelta(object, (CDOFeatureDelta)featureDelta);
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class RewriteTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object featureDelta)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.registerFeatureDelta(object, (CDOFeatureDelta)featureDelta);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ReloadTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object NULL)
    {
      changeState(object, CDOState.PROXY);
    }
  }

  /**
   * @author Eike Stepper
   */
  private class InvalidateTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Pair<Boolean, Long>>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Pair<Boolean, Long> detachAndTimeStamp)
    {
      if (detachAndTimeStamp.getElement1())
      {
        object.cdoInternalPostDetach();
        object.cdoInternalSetState(CDOState.TRANSIENT);
      }
      else
      {
        reviseObject(object, detachAndTimeStamp.getElement2());
        changeState(object, CDOState.PROXY);
      }
    }

    protected void reviseObject(InternalCDOObject object, Long timeStamp)
    {
      InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
      revision.setRevised(timeStamp - 1);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ConflictTransition extends InvalidateTransition
  {
    @Override
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Pair<Boolean, Long> detachAndTimeStamp)
    {
      reviseObject(object, detachAndTimeStamp.getElement2());
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.setConflict(object);
      changeState(object, CDOState.CONFLICT);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class LoadTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    private boolean forWrite;

    public LoadTransition(boolean forWrite)
    {
      this.forWrite = forWrite;
    }

    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object delta)
    {
      CDOID id = object.cdoID();
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      InternalCDORevision revision = view.getRevision(id, true);
      object.cdoInternalSetRevision(revision);
      changeState(object, CDOState.CLEAN);
      object.cdoInternalPostLoad();

      if (forWrite)
      {
        INSTANCE.write(object, (CDOFeatureDelta)delta);
      }
    }
  }
}

/**
 * @author Eike Stepper
 */
enum CDOEvent
{
  PREPARE, ATTACH, DETACH, READ, WRITE, INVALIDATE, RELOAD, COMMIT, ROLLBACK
}

package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.TransportException;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.IFailOverStrategy;
import org.eclipse.net4j.util.fsm.FiniteStateMachine;
import org.eclipse.net4j.util.fsm.ITransition;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.protocol.ResourceIDRequest;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOStateMachine extends FiniteStateMachine<CDOState, CDOEvent, InternalCDOObject>
{
  // @Singleton
  public static final CDOStateMachine INSTANCE = new CDOStateMachine();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOStateMachine.class);

  private InternalCDOObject lastTracedObject;

  private CDOState lastTracedState;

  private CDOEvent lastTracedEvent;

  private CDOStateMachine()
  {
    super(CDOState.class, CDOEvent.class);

    transit(CDOState.TRANSIENT, CDOEvent.ATTACH, new AttachTransition());
    transit(CDOState.TRANSIENT, CDOEvent.DETACH, FAIL);
    transit(CDOState.TRANSIENT, CDOEvent.READ, IGNORE);
    transit(CDOState.TRANSIENT, CDOEvent.WRITE, IGNORE);
    transit(CDOState.TRANSIENT, CDOEvent.COMMIT, FAIL);
    transit(CDOState.TRANSIENT, CDOEvent.ROLLBACK, FAIL);
    transit(CDOState.TRANSIENT, CDOEvent.INVALIDATE, FAIL);
    transit(CDOState.TRANSIENT, CDOEvent.FINALIZE_ATTACH, FAIL);

    transit(CDOState.PREPARED_ATTACH, CDOEvent.ATTACH, FAIL);
    transit(CDOState.PREPARED_ATTACH, CDOEvent.DETACH, FAIL);
    transit(CDOState.PREPARED_ATTACH, CDOEvent.READ, IGNORE);
    transit(CDOState.PREPARED_ATTACH, CDOEvent.WRITE, FAIL);
    transit(CDOState.PREPARED_ATTACH, CDOEvent.COMMIT, FAIL);
    transit(CDOState.PREPARED_ATTACH, CDOEvent.ROLLBACK, FAIL);
    transit(CDOState.PREPARED_ATTACH, CDOEvent.INVALIDATE, FAIL);
    transit(CDOState.PREPARED_ATTACH, CDOEvent.FINALIZE_ATTACH, new FinalizeAttachTransition());

    transit(CDOState.NEW, CDOEvent.ATTACH, FAIL);
    transit(CDOState.NEW, CDOEvent.DETACH, FAIL);
    transit(CDOState.NEW, CDOEvent.READ, IGNORE);
    transit(CDOState.NEW, CDOEvent.WRITE, IGNORE);
    transit(CDOState.NEW, CDOEvent.COMMIT, new CommitTransition());
    transit(CDOState.NEW, CDOEvent.ROLLBACK, FAIL);
    transit(CDOState.NEW, CDOEvent.INVALIDATE, FAIL);
    transit(CDOState.NEW, CDOEvent.FINALIZE_ATTACH, FAIL);

    transit(CDOState.CLEAN, CDOEvent.ATTACH, FAIL);
    transit(CDOState.CLEAN, CDOEvent.DETACH, FAIL);
    transit(CDOState.CLEAN, CDOEvent.READ, IGNORE);
    transit(CDOState.CLEAN, CDOEvent.WRITE, new WriteTransition());
    transit(CDOState.CLEAN, CDOEvent.COMMIT, FAIL);
    transit(CDOState.CLEAN, CDOEvent.ROLLBACK, FAIL);
    transit(CDOState.CLEAN, CDOEvent.INVALIDATE, new InvalidateTransition());
    transit(CDOState.CLEAN, CDOEvent.FINALIZE_ATTACH, FAIL);

    transit(CDOState.DIRTY, CDOEvent.ATTACH, FAIL);
    transit(CDOState.DIRTY, CDOEvent.DETACH, FAIL);
    transit(CDOState.DIRTY, CDOEvent.READ, IGNORE);
    transit(CDOState.DIRTY, CDOEvent.WRITE, IGNORE);
    transit(CDOState.DIRTY, CDOEvent.COMMIT, new CommitTransition());
    transit(CDOState.DIRTY, CDOEvent.ROLLBACK, FAIL);
    transit(CDOState.DIRTY, CDOEvent.INVALIDATE, FAIL);
    transit(CDOState.DIRTY, CDOEvent.FINALIZE_ATTACH, FAIL);

    transit(CDOState.PROXY, CDOEvent.ATTACH, new LoadResourceTransition());
    transit(CDOState.PROXY, CDOEvent.DETACH, new DetachTransition());
    transit(CDOState.PROXY, CDOEvent.READ, new LoadTransition(false));
    transit(CDOState.PROXY, CDOEvent.WRITE, new LoadTransition(true));
    transit(CDOState.PROXY, CDOEvent.COMMIT, FAIL);
    transit(CDOState.PROXY, CDOEvent.ROLLBACK, FAIL);
    transit(CDOState.PROXY, CDOEvent.INVALIDATE, IGNORE);
    transit(CDOState.PROXY, CDOEvent.FINALIZE_ATTACH, IGNORE);

    transit(CDOState.CONFLICT, CDOEvent.ATTACH, FAIL);
    transit(CDOState.CONFLICT, CDOEvent.DETACH, FAIL);
    transit(CDOState.CONFLICT, CDOEvent.READ, FAIL);
    transit(CDOState.CONFLICT, CDOEvent.WRITE, FAIL);
    transit(CDOState.CONFLICT, CDOEvent.COMMIT, FAIL);
    transit(CDOState.CONFLICT, CDOEvent.ROLLBACK, FAIL);
    transit(CDOState.CONFLICT, CDOEvent.INVALIDATE, FAIL);
    transit(CDOState.CONFLICT, CDOEvent.FINALIZE_ATTACH, FAIL);
  }

  public void attach(InternalCDOObject object, CDOResource resource, CDOViewImpl view)
  {
    ResourceAndView data = new ResourceAndView();
    data.resource = resource;
    data.view = view;

    // TRANSIENT --> PREPARED_ATTACH
    if (TRACER.isEnabled()) TRACER.format("ATTACH: {0} --> {1}", object, view);
    process(object, CDOEvent.ATTACH, data);

    // PREPARED_ATTACH --> NEW
    if (TRACER.isEnabled()) TRACER.format("FINALIZE_ATTACH: {0} --> {1}", object, view);
    process(object, CDOEvent.FINALIZE_ATTACH, data);
  }

  public void detach(InternalCDOObject object, CDOResource resource, CDOViewImpl view)
  {
    // TODO Implement method CDOStateMachine.detach()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void read(InternalCDOObject object)
  {
    if (TRACER.isEnabled()) trace(object, CDOEvent.READ);
    process(object, CDOEvent.READ, null);
  }

  public void write(InternalCDOObject object)
  {
    if (TRACER.isEnabled()) trace(object, CDOEvent.WRITE);
    process(object, CDOEvent.WRITE, null);
  }

  public void invalidate(InternalCDOObject object, long timeStamp)
  {
    if (TRACER.isEnabled()) trace(object, CDOEvent.INVALIDATE);
    process(object, CDOEvent.INVALIDATE, timeStamp);
  }

  public void commit(InternalCDOObject object, CommitTransactionResult result)
  {
    if (TRACER.isEnabled()) trace(object, CDOEvent.COMMIT);
    process(object, CDOEvent.COMMIT, result);
  }

  public void rollback(InternalCDOObject object)
  {
    if (TRACER.isEnabled()) trace(object, CDOEvent.ROLLBACK);
    process(object, CDOEvent.ROLLBACK, null);
  }

  @Override
  protected CDOState getState(InternalCDOObject object)
  {
    return object.cdoState();
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

  /**
   * @author Eike Stepper
   */
  private static final class ResourceAndView
  {
    public CDOResource resource;

    public CDOViewImpl view;

    @Override
    public String toString()
    {
      return MessageFormat.format("ResourceAndView({0}, {1})", resource, view);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AttachTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, ResourceAndView>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, ResourceAndView data)
    {
      CDOTransactionImpl transaction = data.view.toTransaction();

      // Prepare object
      CDOID id = transaction.getNextTemporaryID();
      object.cdoInternalSetID(id);
      object.cdoInternalSetResource(data.resource);
      object.cdoInternalSetView(data.view);
      object.cdoInternalSetState(CDOState.PREPARED_ATTACH);

      // Create new revision
      CDORevisionImpl revision = new CDORevisionImpl((CDOClassImpl)object.cdoClass(), id);
      revision.setVersion(1);
      revision.setResourceID(data.resource.cdoID());
      object.cdoInternalSetRevision(revision);

      // Register object
      data.view.registerObject(object);
      transaction.registerNew(object);

      // Attach content tree
      for (Iterator<InternalCDOObject> it = FSMUtil.iterator(object.eContents(), transaction); it.hasNext();)
      {
        InternalCDOObject content = it.next();
        INSTANCE.process(content, CDOEvent.ATTACH, data);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FinalizeAttachTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object data)
    {
      CDOTransactionImpl transaction = (CDOTransactionImpl)object.cdoView();
      object.cdoInternalPostAttach();
      object.cdoInternalSetState(CDOState.NEW);

      // Finalize content tree
      for (Iterator<?> it = FSMUtil.iterator(object.eContents(), transaction); it.hasNext();)
      {
        InternalCDOObject content = (InternalCDOObject)it.next();
        INSTANCE.process(content, CDOEvent.FINALIZE_ATTACH, data);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DetachTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object data)
    {
      // TODO Implement method DetachTransition.execute()
      throw new UnsupportedOperationException("Not yet implemented");
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CommitTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, CommitTransactionResult>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, CommitTransactionResult data)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      Map<CDOID, CDOID> idMappings = data.getIDMappings();

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
      CDORevisionImpl revision = (CDORevisionImpl)object.cdoRevision();
      revision.setID(id);
      revision.setCreated(data.getTimeStamp());
      revision.adjustReferences(idMappings);
      view.getSession().getRevisionManager().addRevision(revision);

      object.cdoInternalSetState(CDOState.CLEAN);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class WriteTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object data)
    {
      // Copy revision
      CDORevisionImpl revision = new CDORevisionImpl((CDORevisionImpl)object.cdoRevision());
      object.cdoInternalSetRevision(revision);
      revision.increaseVersion();

      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = view.toTransaction();
      transaction.registerDirty(object);

      object.cdoInternalSetState(CDOState.DIRTY);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class InvalidateTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Long>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Long timeStamp)
    {
      ((CDORevisionImpl)object.cdoRevision()).setRevised(timeStamp - 1);
      object.cdoInternalSetState(CDOState.PROXY);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LoadTransition implements ITransition<CDOState, CDOEvent, InternalCDOObject, Object>
  {
    private boolean forWrite;

    public LoadTransition(boolean forWrite)
    {
      this.forWrite = forWrite;
    }

    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, Object data)
    {
      CDOID id = object.cdoID();
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDORevisionImpl revision = view.getRevision(id);
      object.cdoInternalSetRevision(revision);
      object.cdoInternalSetState(CDOState.CLEAN);
      object.cdoInternalPostLoad();

      if (forWrite)
      {
        INSTANCE.write(object);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LoadResourceTransition implements
      ITransition<CDOState, CDOEvent, InternalCDOObject, ResourceAndView>
  {
    public void execute(InternalCDOObject object, CDOState state, CDOEvent event, ResourceAndView data)
    {
      CDOID id = requestID(data.resource, data.view);
      if (id == CDOID.NULL)
      {
        throw new ServerException("Resource not available: " + data.resource.getPath());
      }

      // Prepare object
      object.cdoInternalSetID(id);
      object.cdoInternalSetResource(data.resource);
      object.cdoInternalSetView(data.view);

      // Register object
      data.view.registerObject(object);
    }

    private CDOID requestID(CDOResource resource, CDOViewImpl view)
    {
      try
      {
        String path = CDOUtil.extractResourcePath(resource.getURI());
        CDOSessionImpl session = view.getSession();

        IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
        ResourceIDRequest request = new ResourceIDRequest(session.getChannel(), path);
        return failOverStrategy.send(request);
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new TransportException(ex);
      }
    }
  }
}

/**
 * @author Eike Stepper
 */
enum CDOEvent
{
  ATTACH, DETACH, READ, WRITE, COMMIT, ROLLBACK, INVALIDATE, FINALIZE_ATTACH
}

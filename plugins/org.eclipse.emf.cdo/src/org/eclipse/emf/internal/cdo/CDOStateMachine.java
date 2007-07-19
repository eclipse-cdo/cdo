package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.TransportException;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;
import org.eclipse.emf.internal.cdo.protocol.ResourceIDRequest;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOStateMachine
{
  // @Singleton
  public static final CDOStateMachine INSTANCE = new CDOStateMachine();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOStateMachine.class);

  private static Transition IGNORE;

  private static Transition FAIL;

  private Transition[][] transitions;

  private CDOStateMachine()
  {
    IGNORE = new IgnoreTransition();
    FAIL = new FailTransition();

    // Dimension the matrix
    int states = CDOState.values().length;
    int events = Event.values().length;
    transitions = new Transition[states][events];

    // Fill the matrix
    initTransitions();

    // Test the matrix for completeness
    for (CDOState state : CDOState.values())
    {
      for (Event event : Event.values())
      {
        if (transitions[state.ordinal()][event.ordinal()] == null)
        {
          throw new IllegalStateException("transitions[" + state + "][" + event + "] == null");
        }
      }
    }
  }

  public void attach(InternalCDOObject object, CDOResource resource, CDOViewImpl view)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("ATTACH: {0} --> {1}", object, view);
    }

    // TRANSIENT --> PREPARED_ATTACH
    INSTANCE.processEvent(object, Event.ATTACH, resource, view);

    if (TRACER.isEnabled())
    {
      TRACER.format("FINALIZE_ATTACH: {0} --> {1}", object, view);
    }

    // PREPARED_ATTACH --> NEW
    INSTANCE.processEvent(object, Event.FINALIZE_ATTACH, resource, view);
  }

  public void detach(InternalCDOObject object, CDOResource resource, CDOViewImpl view)
  {
    // TODO Implement method CDOStateMachine.detach()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void read(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("READ: {0}", object);
    }

    INSTANCE.processEvent(object, Event.READ, null, null);
  }

  public void write(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("WRITE: {0}", object);
    }

    INSTANCE.processEvent(object, Event.WRITE, null, null);
  }

  public void invalidate(InternalCDOObject object, long timeStamp)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("INVALIDATE: {0}", object);
    }

    INSTANCE.processEvent(object, Event.INVALIDATE, timeStamp, null);
  }

  public void commit(InternalCDOObject object, CommitTransactionResult result)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("COMMIT: {0}", object);
    }

    INSTANCE.processEvent(object, Event.COMMIT, result, null);
  }

  public void rollback(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("ROLLBACK: {0}", object);
    }

    INSTANCE.processEvent(object, Event.ROLLBACK, null, null);
  }

  private void initTransitions()
  {
    setTransition(CDOState.TRANSIENT, Event.ATTACH, new AttachTransition());
    setTransition(CDOState.TRANSIENT, Event.DETACH, FAIL);
    setTransition(CDOState.TRANSIENT, Event.READ, IGNORE);
    setTransition(CDOState.TRANSIENT, Event.WRITE, IGNORE);
    setTransition(CDOState.TRANSIENT, Event.COMMIT, FAIL);
    setTransition(CDOState.TRANSIENT, Event.ROLLBACK, FAIL);
    setTransition(CDOState.TRANSIENT, Event.INVALIDATE, FAIL);
    setTransition(CDOState.TRANSIENT, Event.FINALIZE_ATTACH, FAIL);

    setTransition(CDOState.PREPARED_ATTACH, Event.ATTACH, FAIL);
    setTransition(CDOState.PREPARED_ATTACH, Event.DETACH, FAIL);
    setTransition(CDOState.PREPARED_ATTACH, Event.READ, FAIL);
    setTransition(CDOState.PREPARED_ATTACH, Event.WRITE, FAIL);
    setTransition(CDOState.PREPARED_ATTACH, Event.COMMIT, FAIL);
    setTransition(CDOState.PREPARED_ATTACH, Event.ROLLBACK, FAIL);
    setTransition(CDOState.PREPARED_ATTACH, Event.INVALIDATE, FAIL);
    setTransition(CDOState.PREPARED_ATTACH, Event.FINALIZE_ATTACH, new FinalizeAttachTransition());

    setTransition(CDOState.NEW, Event.ATTACH, FAIL);
    setTransition(CDOState.NEW, Event.DETACH, FAIL);
    setTransition(CDOState.NEW, Event.READ, IGNORE);
    setTransition(CDOState.NEW, Event.WRITE, IGNORE);
    setTransition(CDOState.NEW, Event.COMMIT, new CommitTransition());
    setTransition(CDOState.NEW, Event.ROLLBACK, FAIL);
    setTransition(CDOState.NEW, Event.INVALIDATE, FAIL);
    setTransition(CDOState.NEW, Event.FINALIZE_ATTACH, FAIL);

    setTransition(CDOState.CLEAN, Event.ATTACH, FAIL);
    setTransition(CDOState.CLEAN, Event.DETACH, FAIL);
    setTransition(CDOState.CLEAN, Event.READ, IGNORE);
    setTransition(CDOState.CLEAN, Event.WRITE, new WriteTransition());
    setTransition(CDOState.CLEAN, Event.COMMIT, FAIL);
    setTransition(CDOState.CLEAN, Event.ROLLBACK, FAIL);
    setTransition(CDOState.CLEAN, Event.INVALIDATE, new InvalidateTransition());
    setTransition(CDOState.CLEAN, Event.FINALIZE_ATTACH, FAIL);

    setTransition(CDOState.DIRTY, Event.ATTACH, FAIL);
    setTransition(CDOState.DIRTY, Event.DETACH, FAIL);
    setTransition(CDOState.DIRTY, Event.READ, IGNORE);
    setTransition(CDOState.DIRTY, Event.WRITE, IGNORE);
    setTransition(CDOState.DIRTY, Event.COMMIT, new CommitTransition());
    setTransition(CDOState.DIRTY, Event.ROLLBACK, FAIL);
    setTransition(CDOState.DIRTY, Event.INVALIDATE, FAIL);
    setTransition(CDOState.DIRTY, Event.FINALIZE_ATTACH, FAIL);

    setTransition(CDOState.PROXY, Event.ATTACH, new LoadResourceTransition());
    setTransition(CDOState.PROXY, Event.DETACH, new DetachTransition());
    setTransition(CDOState.PROXY, Event.READ, new LoadTransition(false));
    setTransition(CDOState.PROXY, Event.WRITE, new LoadTransition(true));
    setTransition(CDOState.PROXY, Event.COMMIT, FAIL);
    setTransition(CDOState.PROXY, Event.ROLLBACK, FAIL);
    setTransition(CDOState.PROXY, Event.INVALIDATE, IGNORE);
    setTransition(CDOState.PROXY, Event.FINALIZE_ATTACH, IGNORE);

    setTransition(CDOState.CONFLICT, Event.ATTACH, FAIL);
    setTransition(CDOState.CONFLICT, Event.DETACH, FAIL);
    setTransition(CDOState.CONFLICT, Event.READ, FAIL);
    setTransition(CDOState.CONFLICT, Event.WRITE, FAIL);
    setTransition(CDOState.CONFLICT, Event.COMMIT, FAIL);
    setTransition(CDOState.CONFLICT, Event.ROLLBACK, FAIL);
    setTransition(CDOState.CONFLICT, Event.INVALIDATE, FAIL);
    setTransition(CDOState.CONFLICT, Event.FINALIZE_ATTACH, FAIL);
  }

  private void setTransition(CDOState state, Event event, Transition transition)
  {
    if (transition == null)
    {
      throw new IllegalArgumentException("transition == null");
    }

    int stateIndex = state.ordinal();
    int eventIndex = event.ordinal();
    transitions[stateIndex][eventIndex] = transition;
  }

  private void processEvent(InternalCDOObject object, Event event, Object data1, Object data2)
  {
    CDOState state = object.cdoState();
    int stateIndex = state.ordinal();
    int eventIndex = event.ordinal();

    Transition transition = transitions[stateIndex][eventIndex];
    transition.execute(object, event, data1, data2);
  }

  private static CDOTransactionImpl getTransaction(CDOViewImpl view)
  {
    CDOTransactionImpl transaction = view.getTransaction();
    if (transaction == null)
    {
      throw new IllegalStateException("transaction == null");
    }

    return transaction;
  }

  /**
   * @author Eike Stepper
   */
  private enum Event
  {
    ATTACH, DETACH, READ, WRITE, COMMIT, ROLLBACK, INVALIDATE, FINALIZE_ATTACH
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class Transition
  {
    public void execute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format(traceMessage(), event, object.cdoState(), object);
      }

      doExecute(object, event, data1, data2);
    }

    protected String traceMessage()
    {
      return "Processing event {0} in state {1} for {2}";
    }

    protected abstract void doExecute(InternalCDOObject object, Event event, Object data1, Object data2);
  }

  /**
   * @author Eike Stepper
   */
  private static final class IgnoreTransition extends Transition
  {
    @Override
    protected String traceMessage()
    {
      return "Ignoring event {0} in state {1} for {2}";
    }

    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FailTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      throw new IllegalStateException("Event " + event + " not allowed in state " + object.cdoState() + " for "
          + object);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AttachTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      CDOResourceImpl resource = (CDOResourceImpl)data1;
      CDOViewImpl view = (CDOViewImpl)data2;
      CDOTransactionImpl transaction = getTransaction(view);

      // Prepare object
      CDOID id = CDOIDImpl.create(transaction.getNextTemporaryID());
      object.setID(id);
      object.setResource(resource);
      object.setAdapter(view);
      object.setState(CDOState.PREPARED_ATTACH);

      // Create new revision
      CDORevisionImpl revision = new CDORevisionImpl((CDOClassImpl)object.cdoClass(), id);
      revision.setVersion(1);
      revision.setResourceID(resource.cdoID());
      object.setRevision(revision);

      // Register object
      view.registerObject(object);
      transaction.registerNew(object);

      // Prepare content tree
      for (EObject content : object.eContents())
      {
        if (content instanceof InternalCDOObject)
        {
          INSTANCE.processEvent((InternalCDOObject)content, Event.ATTACH, resource, view);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FinalizeAttachTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      object.finalizeRevision();
      object.setState(CDOState.NEW);

      // Prepare content tree
      for (EObject content : object.eContents())
      {
        if (content instanceof InternalCDOObject)
        {
          INSTANCE.processEvent((InternalCDOObject)content, Event.FINALIZE_ATTACH, null, null);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DetachTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      // TODO Implement method DetachTransition.execute()
      throw new UnsupportedOperationException("Not yet implemented");
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CommitTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CommitTransactionResult result = (CommitTransactionResult)data1;
      Map<CDOID, CDOID> idMappings = result.getIdMappings();

      // Adjust object
      CDOID id = object.cdoID();
      CDOID newID = idMappings.get(id);
      if (newID != null)
      {
        object.setID(newID);
        view.remapObject(id);
        id = newID;
      }

      // Adjust revision
      CDORevisionImpl revision = (CDORevisionImpl)object.cdoRevision();
      revision.setID(id);
      revision.setCreated(result.getTimeStamp());
      revision.adjustReferences(idMappings);
      view.getSession().getRevisionManager().addRevision(revision);

      object.setState(CDOState.CLEAN);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class WriteTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      CDORevisionImpl revision = object.copyRevision();
      revision.increaseVersion();

      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDOTransactionImpl transaction = getTransaction(view);
      transaction.registerDirty(object);

      object.setState(CDOState.DIRTY);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class InvalidateTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      long timeStamp = (Long)data1;
      ((CDORevisionImpl)object.cdoRevision()).setRevised(timeStamp - 1);
      object.setState(CDOState.PROXY);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LoadTransition extends Transition
  {
    private boolean forWrite;

    public LoadTransition(boolean forWrite)
    {
      this.forWrite = forWrite;
    }

    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      CDOID id = object.cdoID();
      CDOViewImpl view = (CDOViewImpl)object.cdoView();
      CDORevisionImpl revision = view.getRevision(id);
      object.setRevision(revision);
      object.setState(CDOState.CLEAN);

      if (forWrite)
      {
        INSTANCE.write(object);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LoadResourceTransition extends Transition
  {
    @Override
    protected void doExecute(InternalCDOObject object, Event event, Object data1, Object data2)
    {
      CDOResourceImpl resource = (CDOResourceImpl)data1;
      CDOViewImpl view = (CDOViewImpl)data2;
      CDOID id = requestID(resource, view);
      if (id == CDOID.NULL)
      {
        throw new ServerException("Resource not available: " + resource.getPath());
      }

      // Prepare object
      object.setID(id);
      // object.setRevision(revision);
      object.setResource(resource);
      object.setAdapter(view);

      // Register object
      view.registerObject(object);
    }

    private CDOID requestID(CDOResourceImpl resource, CDOViewImpl view)
    {
      String path = CDOUtil.extractPath(resource.getURI());
      ResourceIDRequest signal = new ResourceIDRequest(view.getSession().getChannel(), path);

      try
      {
        return signal.send();
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
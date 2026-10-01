/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Orders lock changes for one server session and assigns gapless counts to
 * changes that are visible to that session.
 *
 * @author Eike Stepper
 */
public final class LockChangeDispatcher<C, P>
{
  private final Queue<Ticket<C, P>> queue = new ArrayDeque<>();

  private long lockModCount;

  private boolean draining;

  private Ticket<C, P> active;

  private boolean closed;

  /**
   * Creates a dispatcher with no changes reserved and with its first visible
   * sequence number set to one.
   */
  public LockChangeDispatcher()
  {
  }

  /**
   * Reserves a position in this session's sequence. The position remains
   * pending until its ticket is completed or cancelled.
   *
   * @param context
   *          immutable context captured at the change's linearization point
   * @return the ticket that must later be completed or cancelled
   */
  public synchronized Ticket<C, P> reserve(C context)
  {
    Ticket<C, P> ticket = new Ticket<>(this, context);
    if (closed)
    {
      ticket.state = TicketState.CANCELLED;
      ticket.done = true;
      return ticket;
    }

    queue.add(ticket);
    return ticket;
  }

  private void ready(Ticket<C, P> ticket, P payload, BiFunction<C, P, Projection<P>> projector)
  {
    synchronized (this)
    {
      if (!ticket.done && ticket.state == TicketState.PENDING)
      {
        ticket.payload = payload;
        ticket.projector = projector;
        ticket.state = TicketState.READY;
      }
    }

    drain();
  }

  void cancel(Ticket<C, P> ticket)
  {
    synchronized (this)
    {
      if (!ticket.done && ticket.state == TicketState.PENDING)
      {
        ticket.state = TicketState.CANCELLED;
      }
    }

    drain();
  }

  private void drain()
  {
    synchronized (this)
    {
      if (draining)
      {
        return;
      }

      draining = true;
    }

    try
    {
      for (;;)
      {
        Ticket<C, P> ticket;
        synchronized (this)
        {
          ticket = queue.peek();
          if (ticket == null || ticket.state == TicketState.PENDING)
          {
            return;
          }

          queue.remove();
          active = ticket;
          if (ticket.state == TicketState.CANCELLED || closed)
          {
            ticket.done = true;
            active = null;
            ticket.releaseInputs();
            ticket.signalDone();
            notifyAll();
            continue;
          }
        }

        Projection<P> projection;
        try
        {
          projection = ticket.projector.apply(ticket.context, ticket.payload);
        }
        catch (RuntimeException | Error ex)
        {
          synchronized (this)
          {
            ticket.failure = ex;
            ticket.done = true;
            active = null;
            ticket.releaseInputs();
            ticket.signalDone();
            notifyAll();
          }

          throw ex;
        }

        synchronized (this)
        {
          if (!closed && projection != null && projection.visible)
          {
            ticket.lockModCount = ++lockModCount;
            ticket.projection = projection;
          }

          ticket.done = true;
          active = null;
          ticket.releaseInputs();
          ticket.signalDone();
          notifyAll();
        }
      }
    }
    finally
    {
      boolean retry;
      synchronized (this)
      {
        draining = false;
        Ticket<C, P> first = queue.peek();
        retry = first != null && first.state != TicketState.PENDING;
      }

      if (retry)
      {
        drain();
      }
    }
  }

  /**
   * Returns the most recently assigned visible sequence number.
   *
   * @return zero before the first relevant change, otherwise its sequence number
   */
  public synchronized long getLockModCount()
  {
    return lockModCount;
  }

  public synchronized void setInitialLockModCount(long lockModCount)
  {
    if (!queue.isEmpty() || active != null || draining || this.lockModCount != 0L)
    {
      throw new IllegalStateException("Initial lock modification count can only be set before reservations"); //$NON-NLS-1$
    }

    this.lockModCount = lockModCount;
  }

  /**
   * Returns whether all reservations have reached a stable result.
   */
  public synchronized boolean isQuiescent()
  {
    return queue.isEmpty() && active == null;
  }

  /**
   * Waits until every reservation reaches a stable result.
   */
  public synchronized void awaitQuiescence()
  {
    while (!isQuiescent())
    {
      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while awaiting lock change quiescence", ex); //$NON-NLS-1$
      }
    }
  }

  /**
   * Cancels all outstanding positions and prevents future reservations from
   * becoming visible.
   */
  public void close()
  {
    synchronized (this)
    {
      closed = true;
      for (Ticket<C, P> ticket : queue)
      {
        if (ticket.state == TicketState.PENDING)
        {
          ticket.state = TicketState.CANCELLED;
        }
      }
    }

    drain();
  }

  /**
   * The reservation lifecycle before and after queue processing.
   */
  public enum TicketState
  {
    PENDING, READY, CANCELLED;
  }

  /**
   * The result of projecting a completed change onto one session.
   */
  public static final class Projection<P>
  {
    final boolean visible;

    final P value;

    final Set<?> filteredIDs;

    /**
     * Creates a full or skipped projection.
     *
     * @param visible
     *          whether the change is relevant to the session
     * @param value
     *          the projected payload, if any
     */
    public Projection(boolean visible, P value)
    {
      this(visible, value, null);
    }

    /**
     * Creates a projection that is optionally restricted to a set of IDs.
     *
     * @param visible
     *          whether the change is relevant to the session
     * @param value
     *          the completed payload
     * @param filteredIDs
     *          IDs retained by a filtered projection, or {@code null} for a full projection
     */
    public Projection(boolean visible, P value, Set<?> filteredIDs)
    {
      this.visible = visible;
      this.value = value;
      this.filteredIDs = filteredIDs == null ? null : Collections.unmodifiableSet(new HashSet<>(filteredIDs));
    }

    /**
     * Returns whether the projection is relevant to the session.
     */
    public boolean isVisible()
    {
      return visible;
    }

    /**
     * Returns the projected payload.
     */
    public P getValue()
    {
      return value;
    }

    /**
     * Returns IDs retained by a filtered projection, or {@code null} for a full
     * projection.
     */
    public Set<?> getFilteredIDs()
    {
      return filteredIDs;
    }
  }

  /**
   * A reserved position that can be made ready or cancelled exactly once.
   */
  public static final class Ticket<C, P>
  {
    private final LockChangeDispatcher<C, P> dispatcher;

    private C context;

    private volatile TicketState state = TicketState.PENDING;

    private volatile boolean done;

    private P payload;

    private volatile Projection<P> projection;

    private volatile long lockModCount;

    private volatile Throwable failure;

    private BiFunction<C, P, Projection<P>> projector;

    private Ticket(LockChangeDispatcher<C, P> dispatcher, C context)
    {
      this.dispatcher = dispatcher;
      this.context = context;
    }

    /**
     * Completes this position with its payload. Later ready tickets remain
     * queued behind any earlier pending ticket.
     *
     * @param payload
     *          complete immutable change payload
     * @param projector
     *          function that determines whether and how the change is visible
     */
    public void ready(P payload, BiFunction<C, P, Projection<P>> projector)
    {
      dispatcher.ready(this, payload, projector);
    }

    /**
     * Cancels this position without consuming a sequence number.
     */
    public void cancel()
    {
      dispatcher.cancel(this);
    }

    /**
     * Returns whether this ticket has been processed or discarded.
     */
    public boolean isDone()
    {
      return done;
    }

    /**
     * Returns the ticket's reservation lifecycle state.
     */
    public TicketState getState()
    {
      return state;
    }

    /**
     * Returns the sequence number assigned when this ticket was processed, or
     * zero when it was cancelled or skipped.
     */
    public long getLockModCount()
    {
      return lockModCount;
    }

    /**
     * Returns the completed projection after this ticket is done.
     */
    public Projection<P> getProjection()
    {
      return projection;
    }

    /**
     * Returns a projection failure, if one occurred.
     */
    public Throwable getFailure()
    {
      return failure;
    }

    /**
     * Waits for this ticket to reach a terminal result and returns its stable
     * sequence number and projection.
     * <p>
     * The wait uses a ticket-local monitor so callers do not hold the
     * dispatcher's monitor while waiting.
     * </p>
     *
     * @return the completed result; cancelled or skipped tickets have count zero
     */
    public TicketResult<P> awaitResult()
    {
      synchronized (this)
      {
        while (!done)
        {
          try
          {
            wait();
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while awaiting lock change result", ex); //$NON-NLS-1$
          }
        }
      }

      if (failure != null)
      {
        throw new IllegalStateException("Lock change projection failed", failure); //$NON-NLS-1$
      }

      return new TicketResult<>(lockModCount, projection);
    }

    private void signalDone()
    {
      synchronized (this)
      {
        notifyAll();
      }
    }

    private void releaseInputs()
    {
      context = null;
      payload = null;
      projector = null;
    }
  }

  /**
   * Immutable result of one ticket after projection and sequencing.
   */
  public static final class TicketResult<P>
  {
    private final long lockModCount;

    private final Projection<P> projection;

    private TicketResult(long lockModCount, Projection<P> projection)
    {
      this.lockModCount = lockModCount;
      this.projection = projection;
    }

    public long getLockModCount()
    {
      return lockModCount;
    }

    public Projection<P> getProjection()
    {
      return projection;
    }
  }
}

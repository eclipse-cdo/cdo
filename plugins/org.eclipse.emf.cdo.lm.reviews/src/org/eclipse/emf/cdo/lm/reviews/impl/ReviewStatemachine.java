/*
 * Copyright (c) 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.ReviewEvent;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.net4j.util.fsm.FiniteStateMachine;
import org.eclipse.net4j.util.fsm.ITransition;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class ReviewStatemachine<REVIEW extends Review> extends FiniteStateMachine<ReviewStatus, ReviewEvent, REVIEW>
{
  public static final String PROP_SUBMITTING = "org.eclipse.emf.cdo.lm.submitting";

  private static final int COMMIT_RETRIES = 10;

  private final boolean client;

  private final boolean dropReviews;

  @SuppressWarnings("unchecked")
  private ReviewStatemachine(boolean client, boolean dropReviews)
  {
    super(ReviewStatus.class, ReviewEvent.class);
    this.client = client;
    this.dropReviews = dropReviews;

    INIT(ReviewStatus.NEW, ReviewEvent.CommitInSource, new CommitInSourceTransition());
    INIT(ReviewStatus.NEW, ReviewEvent.CommitInTarget, new CommitInTargetTransition());
    INIT(ReviewStatus.NEW, ReviewEvent.MergeFromSource, IGNORE);
    INIT(ReviewStatus.NEW, ReviewEvent.RebaseToTarget, IGNORE);
    INIT(ReviewStatus.NEW, ReviewEvent.Submit, new SubmitTransition());
    INIT(ReviewStatus.NEW, ReviewEvent.Abandon, new AbandonTransition());
    INIT(ReviewStatus.NEW, ReviewEvent.Restore, FAIL);
    INIT(ReviewStatus.NEW, ReviewEvent.Finish, FAIL);
    INIT(ReviewStatus.NEW, ReviewEvent.Delete, new DeleteTranstion());

    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.CommitInSource, IGNORE);
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.CommitInTarget, new CommitInTargetTransition());
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.MergeFromSource, new MergeFromSourceTransition());
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.RebaseToTarget, IGNORE);
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.Submit, new SubmitTransition());
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.Abandon, new AbandonTransition());
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.Restore, FAIL);
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.Finish, FAIL);
    INIT(ReviewStatus.SOURCE_OUTDATED, ReviewEvent.Delete, new DeleteTranstion());

    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.CommitInSource, dropReviews ? IGNORE : new CommitInSourceTransition());
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.CommitInTarget, IGNORE);
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.MergeFromSource, IGNORE);
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.RebaseToTarget, new RebaseToTargetTransition());
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.Submit, FAIL);
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.Abandon, new AbandonTransition());
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.Restore, FAIL);
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.Finish, FAIL);
    INIT(ReviewStatus.TARGET_OUTDATED, ReviewEvent.Delete, new DeleteTranstion());

    INIT(ReviewStatus.OUTDATED, ReviewEvent.CommitInSource, IGNORE);
    INIT(ReviewStatus.OUTDATED, ReviewEvent.CommitInTarget, IGNORE);
    INIT(ReviewStatus.OUTDATED, ReviewEvent.MergeFromSource, new MergeFromSourceTransition());
    INIT(ReviewStatus.OUTDATED, ReviewEvent.RebaseToTarget, new RebaseToTargetTransition());
    INIT(ReviewStatus.OUTDATED, ReviewEvent.Submit, FAIL);
    INIT(ReviewStatus.OUTDATED, ReviewEvent.Abandon, new AbandonTransition());
    INIT(ReviewStatus.OUTDATED, ReviewEvent.Restore, FAIL);
    INIT(ReviewStatus.OUTDATED, ReviewEvent.Finish, FAIL);
    INIT(ReviewStatus.OUTDATED, ReviewEvent.Delete, new DeleteTranstion());

    INIT(ReviewStatus.SUBMITTED, ReviewEvent.CommitInSource, IGNORE);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.CommitInTarget, IGNORE);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.MergeFromSource, FAIL);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.RebaseToTarget, FAIL);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.Submit, FAIL);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.Abandon, FAIL);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.Restore, FAIL);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.Finish, FAIL);
    INIT(ReviewStatus.SUBMITTED, ReviewEvent.Delete, FAIL);

    INIT(ReviewStatus.ABANDONED, ReviewEvent.CommitInSource, IGNORE);
    INIT(ReviewStatus.ABANDONED, ReviewEvent.CommitInTarget, IGNORE);
    INIT(ReviewStatus.ABANDONED, ReviewEvent.MergeFromSource, FAIL);
    INIT(ReviewStatus.ABANDONED, ReviewEvent.RebaseToTarget, FAIL);
    INIT(ReviewStatus.ABANDONED, ReviewEvent.Submit, FAIL);
    INIT(ReviewStatus.ABANDONED, ReviewEvent.Abandon, FAIL);
    INIT(ReviewStatus.ABANDONED, ReviewEvent.Restore, new RestoreTransition());
    INIT(ReviewStatus.ABANDONED, ReviewEvent.Finish, FAIL);
    INIT(ReviewStatus.ABANDONED, ReviewEvent.Delete, new DeleteTranstion());

    INIT(ReviewStatus.RESTORING, ReviewEvent.CommitInSource, IGNORE);
    INIT(ReviewStatus.RESTORING, ReviewEvent.CommitInTarget, IGNORE);
    INIT(ReviewStatus.RESTORING, ReviewEvent.MergeFromSource, FAIL);
    INIT(ReviewStatus.RESTORING, ReviewEvent.RebaseToTarget, FAIL);
    INIT(ReviewStatus.RESTORING, ReviewEvent.Submit, FAIL);
    INIT(ReviewStatus.RESTORING, ReviewEvent.Abandon, FAIL);
    INIT(ReviewStatus.RESTORING, ReviewEvent.Restore, FAIL);
    INIT(ReviewStatus.RESTORING, ReviewEvent.Finish, new RestoreFinishTransition());
    INIT(ReviewStatus.RESTORING, ReviewEvent.Delete, FAIL);

    INIT(ReviewStatus.DELETED, ReviewEvent.CommitInSource, IGNORE);
    INIT(ReviewStatus.DELETED, ReviewEvent.CommitInTarget, IGNORE);
    INIT(ReviewStatus.DELETED, ReviewEvent.MergeFromSource, FAIL);
    INIT(ReviewStatus.DELETED, ReviewEvent.RebaseToTarget, FAIL);
    INIT(ReviewStatus.DELETED, ReviewEvent.Submit, FAIL);
    INIT(ReviewStatus.DELETED, ReviewEvent.Abandon, FAIL);
    INIT(ReviewStatus.DELETED, ReviewEvent.Restore, FAIL);
    INIT(ReviewStatus.DELETED, ReviewEvent.Finish, FAIL);
    INIT(ReviewStatus.DELETED, ReviewEvent.Delete, FAIL);
  }

  @Override
  protected final ReviewStatus getState(REVIEW review)
  {
    return review.getStatus();
  }

  @Override
  protected final void setState(REVIEW review, ReviewStatus status)
  {
    review.setStatus(status);
  }

  protected abstract void handleCommitInSource(REVIEW review);

  protected abstract void handleCommitInTarget(REVIEW review);

  protected abstract void handleMergeFromSource(REVIEW review, MergeFromSourceResult result);

  protected abstract void handleRebaseToTarget(REVIEW review, RebaseToTargetResult result);

  protected abstract void handleSubmit(REVIEW review, FixedBaseline submitResult);

  protected abstract void handleAbandon(REVIEW review);

  protected abstract void handleRestore(REVIEW review);

  protected abstract ReviewStatus handleRestoreFinish(REVIEW review);

  protected abstract void handleDelete(REVIEW review);

  protected final void setCommitComment(CDOObject object, String comment)
  {
    CDOTransaction transaction = (CDOTransaction)object.cdoView();
    transaction.setCommitComment(comment);
  }

  @SuppressWarnings("unchecked")
  private void INIT(ReviewStatus status, ReviewEvent event, ITransition<ReviewStatus, ReviewEvent, REVIEW, Object> transition)
  {
    if (event.isClient() != client)
    {
      transition = FAIL;
    }
    else if (dropReviews && !event.isDrop())
    {
      transition = IGNORE;
    }
    else if (!dropReviews && !event.isDelivery())
    {
      transition = IGNORE;
    }

    init(status, event, transition);
  }

  public static <REVIEW extends Review> CDOCommitInfo modify(REVIEW review, Consumer<REVIEW> modifier)
  {
    CDOSession session = review.cdoView().getSession();

    ConcurrentAccessException[] exception = { null };
    CDOCommitInfo[] result = { null };

    for (int i = 0; i <= COMMIT_RETRIES; i++)
    {
      CDOTransaction transaction = session.openTransaction();
      exception[0] = null;

      try
      {
        transaction.syncExec(() -> {
          REVIEW transactionalReview = transaction.getObject(review);
          modifier.accept(transactionalReview);

          if (!transaction.isDirty())
          {
            return;
          }

          try
          {
            result[0] = transaction.commit();
            exception[0] = null;
          }
          catch (ConcurrentAccessException ex)
          {
            exception[0] = ex;
          }
          catch (CommitException ex)
          {
            throw ex.wrap();
          }
        });

        if (exception[0] == null)
        {
          return result[0];
        }
      }
      finally
      {
        transaction.close();
      }
    }

    if (exception[0] != null)
    {
      throw exception[0].wrap();
    }

    return result[0];
  }

  /**
   * @author Eike Stepper
   */
  public enum ReviewEvent
  {
    CommitInSource(false, true, true, false), //

    CommitInTarget(false, true, true, false), //

    MergeFromSource(true, false, true, true), //

    RebaseToTarget(true, false, true, true), //

    Submit(true, false, true, true), //

    Abandon(true, false, true, true), //

    Restore(true, false, true, true), //

    Finish(false, true, true, true), //

    Delete(true, false, true, true);

    private final boolean client;

    private final boolean server;

    private final boolean delivery;

    private final boolean drop;

    private ReviewEvent(boolean client, boolean server, boolean delivery, boolean drop)
    {
      this.client = client;
      this.server = server;
      this.delivery = delivery;
      this.drop = drop;
    }

    public boolean isClient()
    {
      return client;
    }

    public boolean isServer()
    {
      return server;
    }

    public boolean isDelivery()
    {
      return delivery;
    }

    public boolean isDrop()
    {
      return drop;
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class AbstractTransition implements ITransition<ReviewStatus, ReviewEvent, REVIEW, Object>
  {
    @Override
    public final void execute(REVIEW review, ReviewStatus status, ReviewEvent event, Object data)
    {
      modify(review, transactionalReview -> {
        ReviewStatus newStatus = execute(transactionalReview, status, data);
        if (newStatus != status)
        {
          changeState(transactionalReview, newStatus);
        }
      });
    }

    protected abstract ReviewStatus execute(REVIEW review, ReviewStatus status, Object data);
  }

  /**
   * @author Eike Stepper
   */
  private class CommitInSourceTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleCommitInSource(review);
      return status == ReviewStatus.NEW ? ReviewStatus.SOURCE_OUTDATED : ReviewStatus.OUTDATED;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class CommitInTargetTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleCommitInTarget(review);
      return status == ReviewStatus.NEW ? ReviewStatus.TARGET_OUTDATED : ReviewStatus.OUTDATED;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class MergeFromSourceTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleMergeFromSource(review, (MergeFromSourceResult)data);
      return status == ReviewStatus.OUTDATED ? ReviewStatus.TARGET_OUTDATED : ReviewStatus.NEW;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class RebaseToTargetTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleRebaseToTarget(review, (RebaseToTargetResult)data);
      return status == ReviewStatus.OUTDATED ? ReviewStatus.SOURCE_OUTDATED : ReviewStatus.NEW;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class SubmitTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleSubmit(review, (FixedBaseline)data);
      return ReviewStatus.SUBMITTED;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class AbandonTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleAbandon(review);
      return ReviewStatus.ABANDONED;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class RestoreTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleRestore(review);
      return ReviewStatus.RESTORING;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class RestoreFinishTransition extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      return handleRestoreFinish(review);
    }
  }

  /**
   * @author Eike Stepper
   */
  private class DeleteTranstion extends AbstractTransition
  {
    @Override
    public ReviewStatus execute(REVIEW review, ReviewStatus status, Object data)
    {
      handleDelete(review);
      return ReviewStatus.DELETED;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class MergeFromSourceResult
  {
    public long sourceCommit = CDOBranchPoint.INVALID_DATE;

    public long targetCommit = CDOBranchPoint.INVALID_DATE;

    public MergeFromSourceResult()
    {
    }

    public boolean isSuccess()
    {
      return targetCommit != CDOBranchPoint.INVALID_DATE;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RebaseToTargetResult
  {
    public CDOBranchRef rebaseBranch;

    public long targetCommit = CDOBranchPoint.INVALID_DATE;

    public boolean success;

    public RebaseToTargetResult()
    {
    }

    public boolean isSuccess()
    {
      return success;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Client<REVIEW extends Review> extends ReviewStatemachine<REVIEW>
  {
    public Client(boolean dropReviews)
    {
      super(true, dropReviews);
    }

    @Override
    protected final void handleCommitInSource(REVIEW review)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected final void handleCommitInTarget(REVIEW review)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected final ReviewStatus handleRestoreFinish(REVIEW review)
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Server<REVIEW extends Review> extends ReviewStatemachine<REVIEW>
  {
    public Server(boolean dropReviews)
    {
      super(false, dropReviews);
    }

    @Override
    protected void handleMergeFromSource(REVIEW review, MergeFromSourceResult result)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected void handleRebaseToTarget(REVIEW review, RebaseToTargetResult result)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected final void handleSubmit(REVIEW review, FixedBaseline submitResult)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected final void handleAbandon(REVIEW review)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected final void handleRestore(REVIEW review)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    protected final void handleDelete(REVIEW review)
    {
      throw new UnsupportedOperationException();
    }
  }
}

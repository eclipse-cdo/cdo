/*
 * Copyright (c) 2012, 2013, 2016, 2017, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;

import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

/**
 * A utility class that, when associated with a {@link CDOTransaction transaction}, automatically updates the
 * {@link CDOTransaction#setCommitComment(String) commit comment} according to local model modifications.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOTransactionCommentator implements Closeable
{
  /**
   * @since 4.6
   */
  public static final String MERGE_PREFIX = "Merge from ";

  private final IListener transactionListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      close();
    }
  };

  private final CDOTransactionHandler2 transactionHandler = new CDOTransactionHandler2()
  {
    private String lastComment;

    @Override
    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      lastComment = createComment(commitContext);
      if (lastComment != null)
      {
        String commitComment = transaction.getCommitComment();
        if (commitComment != null)
        {
          transaction.setCommitComment(commitComment + " (" + lastComment + ")");
        }
        else
        {
          transaction.setCommitComment(lastComment);
        }
      }
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      cleanUp();
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
      cleanUp();
    }

    private void cleanUp()
    {
      if (lastComment != null)
      {
        String commitComment = transaction.getCommitComment();
        if (commitComment != null)
        {
          if (commitComment.endsWith(lastComment))
          {
            commitComment = commitComment.substring(0, commitComment.length() - lastComment.length());
            transaction.setCommitComment(commitComment.trim());
          }
        }
      }

      lastComment = null;
    }
  };

  private CDOTransaction transaction;

  private boolean showMerges;

  public CDOTransactionCommentator(CDOTransaction transaction)
  {
    this(transaction, false);
  }

  /**
   * @since 4.6
   */
  public CDOTransactionCommentator(final CDOTransaction transaction, boolean showMerges)
  {
    this.transaction = transaction;
    this.showMerges = showMerges;

    transaction.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        transaction.addListener(transactionListener);
        transaction.addTransactionHandler(transactionHandler);
      }
    });
  }

  public final CDOTransaction getTransaction()
  {
    return transaction;
  }

  /**
   * @since 4.6
   */
  public final boolean isShowMerges()
  {
    return showMerges;
  }

  /**
   * @since 4.6
   */
  public final void setShowMerges(final boolean showMerges)
  {
    transaction.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        CDOTransactionCommentator.this.showMerges = showMerges;
      }
    });
  }

  @Override
  public final boolean isClosed()
  {
    return transaction == null;
  }

  @Override
  public void close()
  {
    transaction.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        transaction.removeTransactionHandler(transactionHandler);
        transaction.removeListener(transactionListener);
      }
    });

    transaction = null;
  }

  protected String createComment(CDOCommitContext commitContext)
  {
    StringBuilder builder = new StringBuilder();
    appendSummary(builder, commitContext);
    return builder.toString();
  }

  /**
   * @since 4.6
   */
  public static boolean appendMerge(StringBuilder builder, CDOBranchPoint mergeSource)
  {
    if (mergeSource != null)
    {
      return appendBranchPoint(builder, mergeSource);
    }

    return false;
  }

  /**
   * @since 4.15
   */
  public static boolean appendBranchPoint(StringBuilder builder, CDOBranchPoint branchPoint)
  {
    if (branchPoint != null)
    {
      builder.append(branchPoint.getBranch().getPathName());
      builder.append(", ");
      builder.append(CDOCommonUtil.formatTimeStamp(branchPoint.getTimeStamp()));
      return true;
    }

    return false;
  }

  /**
   * @since 4.2
   */
  public static void appendSummary(StringBuilder builder, CDOCommitContext commitContext)
  {
    appendSummary(builder, commitContext, false);
  }

  /**
   * @since 4.6
   */
  public static boolean appendSummary(StringBuilder builder, CDOCommitContext commitContext, boolean showMerges)
  {
    boolean needComma = false;

    if (showMerges)
    {
      CDOBranchPoint mergeSource = commitContext.getCommitMergeSource();
      needComma |= appendMerge(builder, mergeSource);
    }

    needComma |= appendSummary(builder, needComma, commitContext.getNewObjects().size(), "addition");
    needComma |= appendSummary(builder, needComma, commitContext.getDirtyObjects().size(), "change");
    needComma |= appendSummary(builder, needComma, commitContext.getDetachedObjects().size(), "removal");

    return needComma;
  }

  private static boolean appendSummary(StringBuilder builder, boolean needComma, int count, String label)
  {
    if (count > 0)
    {
      if (needComma)
      {
        builder.append(", ");
      }

      builder.append(count);
      builder.append(" ");
      builder.append(label);
      if (count > 1)
      {
        builder.append("s");
      }

      return true;
    }

    return false;
  }
}

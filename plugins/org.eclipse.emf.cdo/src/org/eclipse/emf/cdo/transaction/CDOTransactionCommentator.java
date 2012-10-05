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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOTransactionCommentator implements Closeable
{
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

    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      lastComment = createComment(commitContext);
      if (lastComment != null)
      {
        String commitComment = transaction.getCommitComment();
        if (commitComment != null)
        {
          transaction.setCommitComment(commitComment = " " + lastComment);
        }
        else
        {
          transaction.setCommitComment(lastComment);
        }
      }
    }

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      cleanUp();
    }

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

  public CDOTransactionCommentator(CDOTransaction transaction)
  {
    this.transaction = transaction;
    transaction.addListener(transactionListener);
    transaction.addTransactionHandler(transactionHandler);
  }

  public final CDOTransaction getTransaction()
  {
    return transaction;
  }

  public final boolean isClosed()
  {
    return transaction == null;
  }

  public void close()
  {
    transaction.removeTransactionHandler(transactionHandler);
    transaction.removeListener(transactionListener);
    transaction = null;
  }

  protected String createComment(CDOCommitContext commitContext)
  {
    StringBuilder builder = new StringBuilder("<");
    appendSummary(builder, commitContext);
    builder.append(">");
    return builder.toString();
  }

  /**
   * @since 4.2
   */
  public static void appendSummary(StringBuilder builder, CDOCommitContext commitContext)
  {
    boolean needComma = false;
    needComma |= appendSummary(builder, needComma, commitContext.getNewObjects().size(), "addition");
    needComma |= appendSummary(builder, needComma, commitContext.getDirtyObjects().size(), "change");
    needComma |= appendSummary(builder, needComma, commitContext.getDetachedObjects().size(), "removal");
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

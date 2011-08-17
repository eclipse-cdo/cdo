/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;

/**
 * A call-back interface that indicates the ability to <i>handle</i> {@link CDORevision revisions} that are passed from
 * other entities.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDORevisionHandler
{
  /**
   * Handles a revision.
   * 
   * @return <code>true</code> to indicate that the caller may pass more revisions, <code>false</code> otherwise.
   * @since 4.0
   */
  public boolean handleRevision(CDORevision revision);

  /**
   * @author Eike Stepper
   * @since 4.1
   */
  public static class Filtered implements CDORevisionHandler
  {
    private final CDORevisionHandler delegate;

    public Filtered(CDORevisionHandler delegate)
    {
      this.delegate = delegate;
    }

    public final boolean handleRevision(CDORevision revision)
    {
      if (filter(revision))
      {
        return true;
      }

      return delegate.handleRevision(revision);
    }

    protected boolean filter(CDORevision revision)
    {
      return false;
    }

    /**
     * @author Eike Stepper
     */
    public static final class Undetached extends Filtered
    {
      public Undetached(CDORevisionHandler delegate)
      {
        super(delegate);
      }

      @Override
      protected boolean filter(CDORevision revision)
      {
        return revision instanceof DetachedCDORevision;
      }
    }
  }
}

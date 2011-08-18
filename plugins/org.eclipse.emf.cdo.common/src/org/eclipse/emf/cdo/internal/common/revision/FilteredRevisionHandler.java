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
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;

/**
 * Corresponds to {@link CDORevisionHandler}.Filtered in trunk!
 * 
 * @author Eike Stepper
 */
public class FilteredRevisionHandler implements CDORevisionHandler
{
  private final CDORevisionHandler delegate;

  public FilteredRevisionHandler(CDORevisionHandler delegate)
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
  public static final class Undetached extends FilteredRevisionHandler
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

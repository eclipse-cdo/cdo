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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOMerger
{
  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws ConflictException;

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  public static class ConflictException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    private CDOMerger merger;

    private CDOChangeSetData result;

    public ConflictException(String message, CDOMerger merger, CDOChangeSetData result)
    {
      super(message);
      this.merger = merger;
      this.result = result;
    }

    public ConflictException(String message, Throwable cause, CDOMerger merger, CDOChangeSetData result)
    {
      super(message, cause);
      this.merger = merger;
      this.result = result;
    }

    public CDOMerger getMerger()
    {
      return merger;
    }

    public CDOChangeSetData getResult()
    {
      return result;
    }
  }
}

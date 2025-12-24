/*
 * Copyright (c) 2010-2012, 2014, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.util.CDOException;

/**
 * Calculates the changes between a source {@link CDOChangeSet change set} and a target {@link CDOChangeSet change set}
 * and returns the result relative to their common ancestor.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOMerger
{
  /**
   * Calculates the changes between a source {@link CDOChangeSet change set} and a target {@link CDOChangeSet change
   * set} and returns the result relative to their common ancestor.
   */
  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws ConflictException;

  /**
   * Thrown from a {@link CDOMerger merger} in case of conflicting changes in the a source and target
   * {@link CDOChangeSet change sets}.
   *
   * @author Eike Stepper
   * @since 4.0
   */
  public static class ConflictException extends CDOException
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

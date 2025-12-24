/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.util.CDOException;

/**
 * Supports the cleanup of LOBs (Large OBjects) that are no longer referenced by any revisions.
 *
 * @author Eike Stepper
 * @since 4.25
 */
public interface ILobCleanup
{
  public int cleanupLobs(boolean dryRun) throws LobCleanupNotSupportedException;

  /**
   * Indicates that LOB cleanup is not supported.
   *
   * @author Eike Stepper
   */
  public static final class LobCleanupNotSupportedException extends CDOException
  {
    private static final long serialVersionUID = 1L;

    public LobCleanupNotSupportedException()
    {
      super("LOB cleanup is not supported");
    }

    public LobCleanupNotSupportedException(Throwable cause)
    {
      super("LOB cleanup is not supported", cause);
    }
  }
}

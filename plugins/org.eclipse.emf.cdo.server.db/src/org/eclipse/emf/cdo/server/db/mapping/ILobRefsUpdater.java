/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.util.CDOException;

import java.sql.Connection;

/**
 * @author Eike Stepper
 * @since 4.14
 */
public interface ILobRefsUpdater
{
  public void updateLobRefs(Connection connection) throws LobRefsUpdateNotSupportedException;

  /**
   * @author Eike Stepper
   */
  public static final class LobRefsUpdateNotSupportedException extends CDOException
  {
    private static final long serialVersionUID = 1L;

    public LobRefsUpdateNotSupportedException()
    {
      super("LOB references update is not supported");
    }
  }
}

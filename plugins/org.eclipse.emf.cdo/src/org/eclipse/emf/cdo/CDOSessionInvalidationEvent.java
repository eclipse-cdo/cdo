/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface CDOSessionInvalidationEvent extends CDOSessionEvent
{
  public static final long LOCAL_ROLLBACK = 0L;

  public CDOView getView();

  /**
   * Returns the time stamp of the server transaction if this event was sent as a result of a successfully committed
   * transaction or <code>LOCAL_ROLLBACK</code> if this event was sent due to a local rollback.
   */
  public long getTimeStamp();

  public Set<CDOIDAndVersion> getDirtyOIDs();
}

/*
 * Copyright (c) 2010-2012, 2015, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public interface IDawnListener extends IListener
{
  /**
   * allows to react an view invalidations
   */
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event);

  /**
   * Allows to react on conflicts
   */
  public void handleTransactionConflictEvent(@SuppressWarnings("deprecation") org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent event);

  /**
   * Allows the user to react on lock change notifications:
   *
   * @since 2.0
   */
  public void handleLocksChangedEvent(CDOViewLocksChangedEvent event);

  /**
   * This method typically will be called for unprocessed events.
   *
   * @since 2.0
   */
  public void handleEvent(IEvent event);
}

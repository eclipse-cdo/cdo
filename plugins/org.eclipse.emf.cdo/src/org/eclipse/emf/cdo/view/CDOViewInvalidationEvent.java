/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.event.IListener;

import java.util.Set;

/**
 * An event that is emitted to registered {@link IListener listeners} of a {@link CDOView} if
 * {@link CDOSession#setPassiveUpdateEnabled(boolean) passive update} is enabled for the session.
 * 
 * @author Simon McDuff
 * @see CDOInvalidationNotification
 * @see CDOAdapterPolicy
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOViewInvalidationEvent extends CDOViewEvent
{
  /**
   * Returns the time stamp of the server transaction if this event was sent as a result of a successfully committed
   * transaction or <code>LOCAL_ROLLBACK</code> if this event was sent due to a local rollback.
   */
  public long getTimeStamp();

  /**
   * Returns a set of the {@link CDOObject CDOObjects} of the modified objects.
   */
  public Set<? extends CDOObject> getDirtyObjects();

  /**
   * Returns a set of the {@link CDOObject CDOObjects} of the removed objects.
   * 
   * @since 2.0
   */
  public Set<? extends CDOObject> getDetachedObjects();
}

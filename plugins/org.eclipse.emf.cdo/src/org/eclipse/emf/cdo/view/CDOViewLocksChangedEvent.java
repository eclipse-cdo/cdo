/*
 * Copyright (c) 2011, 2012, 2014, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.session.CDOSession.Options;

import org.eclipse.emf.ecore.EObject;

/**
 * A {@link CDOViewEvent view event} fired when {@link CDOLockChangeInfo lock changes} are being received from a remote
 * repository.
 * {@link Options#setLockNotificationMode(org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode) Lock
 * notifications} must be enabled for this event to be fired.
 *
 * @author Caspar De Groot
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewLocksChangedEvent extends CDOViewEvent, CDOLockChangeInfo
{
  /**
   * Returns the view that caused the lock changes if this view is local, or <code>null</code> if the view was remote.
   */
  public CDOView getSender();

  /**
   * Get {@link EObject objects} affected by lock changes.
   *
   * @since 4.4
   */
  public EObject[] getAffectedObjects();
}

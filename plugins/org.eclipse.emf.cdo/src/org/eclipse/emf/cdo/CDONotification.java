/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.common.notify.Notification;

/**
 * @author Simon McDuff
 *         <p>
 *         This class behave like the usual Notification except for the following:
 *         <p>
 *         <li>It doesn't provide old value, only new index or new value.</li>
 *         <li>{@link Notification#REMOVE_MANY} means clear was call.</li>
 *         <li>{@link Notification#Add_MANY} isn't used.</li>
 *         <p>
 * @since 2.0
 */
public interface CDONotification extends Notification
{
  /**
   * Notification often have a list of notification (NotificationChain). It informs the adapter if another another
   * notification will be sent.
   * <p>
   * You don`t want to refresh the UI for every Notifications. The same object could receives many notifications at the
   * same time. To know when to perform your update you could use that value. It will be the last notification when it
   * will return false.
   */
  boolean hasNext();

  /**
   * Return the {@link CDORevisionDelta} associate to this Notification.
   */
  public CDORevisionDelta getRevisionDelta();

}

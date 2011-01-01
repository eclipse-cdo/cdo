/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.common.notify.Notification;

/**
 * TODO Simon: JavaDoc
 * 
 * @since 2.0
 * @author Simon McDuff
 */
public interface CDONotification extends Notification
{
  /**
   * TODO Simon: JavaDoc
   */
  public static final int EVENT_TYPE_CDO_START = Notification.EVENT_TYPE_COUNT + 100;

  /**
   * TODO Simon: JavaDoc
   */
  public static final int DETACH_OBJECT = EVENT_TYPE_CDO_START + 1;

  /**
   * TODO Simon: JavaDoc
   */
  public static final int INVALIDATE = EVENT_TYPE_CDO_START + 2;
}

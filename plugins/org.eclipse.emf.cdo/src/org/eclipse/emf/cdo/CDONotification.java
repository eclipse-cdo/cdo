/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.common.notify.Notification;

/**
 * A base interface for all CDO specific notifications.
 *
 * @since 2.0
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDONotification extends Notification
{
  public static final int EVENT_TYPE_CDO_START = Notification.EVENT_TYPE_COUNT + 100;

  public static final int DETACH_OBJECT = EVENT_TYPE_CDO_START + 1;

  public static final int INVALIDATE = EVENT_TYPE_CDO_START + 2;
}

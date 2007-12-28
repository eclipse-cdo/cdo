/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.common.notify.Notification;

/**
 * @author Simon McDuff
 */
public interface CDOInvalidationNotification extends Notification
{
  public static final int INVALIDATE = EVENT_TYPE_COUNT + 1;
}

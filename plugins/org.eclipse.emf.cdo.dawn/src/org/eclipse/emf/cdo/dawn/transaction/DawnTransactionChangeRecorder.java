/*
 * Copyright (c) 2010-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.transaction;

import org.eclipse.emf.cdo.CDODeltaNotification;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionChangeRecorder;

/**
 * @author Martin Fluegge
 */
public class DawnTransactionChangeRecorder extends TransactionChangeRecorder
{
  /**
   * @since 2.0
   */
  public DawnTransactionChangeRecorder(InternalTransactionalEditingDomain domain, ResourceSet rset)
  {
    super(domain, rset);
  }

  /**
   * @since 1.0
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    if (!(notification.getOldValue() instanceof ResourceSet && notification.getNewValue() == null && notification.getEventType() == Notification.SET))
    {
      if (!(notification instanceof CDODeltaNotification))
      {
        super.notifyChanged(notification);
      }
    }
  }
}

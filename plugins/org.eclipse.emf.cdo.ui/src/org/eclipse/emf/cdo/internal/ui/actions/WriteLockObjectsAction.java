/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

/**
 * @author Simon McDuff
 * @deprecated As of 4.6 no longer supported.
 */
@Deprecated
public class WriteLockObjectsAction extends AbstractLockObjectsAction
{
  @Deprecated
  public static final String ID = "writelock-objects"; //$NON-NLS-1$

  @Deprecated
  public WriteLockObjectsAction()
  {
    super(Messages.getString("WriteLockObjectsAction.1")); //$NON-NLS-1$
    setId(ID);
  }

  @Deprecated
  @Override
  protected CDOLock getLock(InternalCDOObject object)
  {
    return object.cdoWriteLock();
  }
}

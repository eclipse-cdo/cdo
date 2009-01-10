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
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOLock;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

/**
 * @author Simon McDuff
 */
public class WriteLockObjectsAction extends AbstractLockObjectsAction
{
  public static final String ID = "writelock-objects";

  public WriteLockObjectsAction()
  {
    super("WriteLock");
    setId(ID);
  }

  @Override
  protected CDOLock getLock(InternalCDOObject object)
  {
    return object.cdoWriteLock();
  }
}

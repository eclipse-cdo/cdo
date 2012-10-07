/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Eike Stepper
 */
public class CDOExternalObject extends CDOObjectWrapperBase
{
  public CDOExternalObject(InternalEObject instance, InternalCDOView view)
  {
    this.instance = instance;
    this.view = view;
    id = view.provideCDOID(instance);
  }

  public CDOState cdoState()
  {
    return CDOState.CLEAN;
  }

  public CDORevision cdoRevision()
  {
    return null;
  }

  public CDOLock cdoReadLock()
  {
    return null;
  }

  public CDOLock cdoWriteLock()
  {
    return null;
  }

  public CDOLock cdoWriteOption()
  {
    return null;
  }

  public CDOLockState cdoLockState()
  {
    return null;
  }

  public void cdoReload()
  {
    // Do nothing
  }

  public CDOObjectHistory cdoHistory()
  {
    return null;
  }
}

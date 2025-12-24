/*
 * Copyright (c) 2012, 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Eike Stepper
 */
public class CDOExternalObject extends CDOObjectWrapperBase
{
  private InternalCDOView view;

  private CDOID id;

  public CDOExternalObject(InternalEObject instance, InternalCDOView view)
  {
    this.instance = instance;
    this.view = view;
    id = view.provideCDOID(instance);
  }

  @Override
  public CDOID cdoID()
  {
    return id;
  }

  @Override
  public CDOView cdoView()
  {
    return view;
  }

  @Override
  public void cdoPrefetch(int depth)
  {
  }

  @Override
  public CDOState cdoState()
  {
    return CDOState.CLEAN;
  }

  @Override
  public CDORevision cdoRevision()
  {
    return null;
  }

  @Override
  public CDORevision cdoRevision(boolean loadOnDemand)
  {
    return null;
  }

  @Override
  public CDOPermission cdoPermission()
  {
    return null;
  }

  @Override
  public CDOLock cdoReadLock()
  {
    return null;
  }

  @Override
  public CDOLock cdoWriteLock()
  {
    return null;
  }

  @Override
  public CDOLock cdoWriteOption()
  {
    return null;
  }

  @Override
  public CDOLockState cdoLockState()
  {
    return null;
  }

  @Override
  @Deprecated
  public void cdoReload()
  {
    // Do nothing
  }

  @Override
  public CDOObjectHistory cdoHistory()
  {
    return null;
  }
}

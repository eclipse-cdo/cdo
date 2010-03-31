/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - bug 247226: Transparently support legacy models
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOMetaWrapper extends CDOObjectWrapper
{
  public CDOMetaWrapper(InternalCDOView view, InternalEObject instance, CDOID id)
  {
    this.view = view;
    this.instance = instance;
    this.id = id;
  }

  public CDOState cdoState()
  {
    return CDOState.CLEAN;
  }

  public InternalCDORevision cdoRevision()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOResourceImpl cdoResource()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOResourceImpl cdoDirectResource()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public EClass eClass()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoReload()
  {
    throw new UnsupportedOperationException();
  }

  public boolean cdoTransient()
  {
    return false;
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPreLoad()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostLoad()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalCleanup()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostAttach()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostDetach(boolean remote)
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostInvalidate()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 3.0
   */
  public void cdoInternalPostRollback()
  {
    // Do nothing
  }

  public void cdoInternalPreCommit()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    if (cdoID() == null)
    {
      return instance.eClass().getName() + "?"; //$NON-NLS-1$
    }

    return instance.eClass().getName() + "@" + cdoID(); //$NON-NLS-1$
  }
}

/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.InternalEObject;

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

  public CDOClass cdoClass()
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

  public void cdoInternalPostDetach()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPreCommit()
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostInvalid()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    if (cdoID() == null)
    {
      return instance.eClass().getName() + "?";
    }

    return instance.eClass().getName() + "@" + cdoID();
  }
}

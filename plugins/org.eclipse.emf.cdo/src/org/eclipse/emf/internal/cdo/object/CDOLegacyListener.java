/*
 * Copyright (c) 2011, 2012, 2014 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Eike Stepper
 * @since 2.0
 */
/*
 * IMPORTANT: Compile errors in this class might indicate an old version of EMF. Legacy support is only enabled for EMF
 * with fixed bug #247130. These compile errors do not affect native models!
 */
public final class CDOLegacyListener extends CDOLegacyWrapper
// TODO LEGACY
// implements InternalEObject.EReadListener, InternalEObject.EWriteListener
{
  private boolean handlingCallback;

  public CDOLegacyListener(InternalEObject instance)
  {
    super(instance);
  }

  public void eFireRead(int featureID)
  {
    // Do nothing
  }

  public void eFireWrite(int featureID)
  {
    // Do nothing
  }

  // TODO LEGACY
  // /**
  // * @since 2.0
  // */
  // /*
  // * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
  // * EMF with fixed bug #247130. These compile errors do not affect native models!
  // */
  // public EList<InternalEObject.EReadListener> eReadListeners()
  // {
  // return instance.eReadListeners();
  // }

  // TODO LEGACY
  // /**
  // * @since 2.0
  // */
  // /*
  // * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
  // * EMF with fixed bug #247130. These compile errors do not affect native models!
  // */
  // public EList<InternalEObject.EWriteListener> eWriteListeners()
  // {
  // return instance.eWriteListeners();
  // }

  public synchronized void handleRead(InternalEObject object, int featureID)
  {
    if (!handlingCallback)
    {
      try
      {
        handlingCallback = true;
        CDOStateMachine.INSTANCE.read(this);

        // TODO Optimize this when the list position index is added to the new callbacks
        resolveAllProxies();
      }
      finally
      {
        handlingCallback = false;
      }
    }
  }

  public synchronized void handleWrite(InternalEObject object, int featureID)
  {
    if (!handlingCallback)
    {
      try
      {
        handlingCallback = true;
        CDOStateMachine.INSTANCE.write(this, null);

        // TODO Optimize this when the list position index is added to the new callbacks
        resolveAllProxies();
      }
      finally
      {
        handlingCallback = false;
      }
    }
  }
}

/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.CDOCallback;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * @author Eike Stepper
 */
public class CDOCallbackImpl extends CDOLegacyImpl implements CDOCallback
{
  public CDOCallbackImpl(InternalEObject instance)
  {
    this.instance = instance;
  }

  public void beforeRead(EObjectImpl instance)
  {
    CDOStateMachine.INSTANCE.read(this);
  }

  public void beforeWrite(EObjectImpl instance)
  {
    CDOStateMachine.INSTANCE.write(this);
  }

  @Override
  protected Object convertPotentialID(CDOViewImpl view, Object potentialID)
  {
    // XXX
    return view.convertIDToObject(potentialID);
  }
}

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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class DynamicCDOObjectImpl extends CDOObjectImpl implements EStructuralFeature.Internal.DynamicValueHolder
{
  private EClass eClass;

  public DynamicCDOObjectImpl(EClass eClass)
  {
    this.eClass = eClass;
  }

  @Override
  public EClass eClass()
  {
    return eClass;
  }

  @Override
  public void eSetClass(EClass eClass)
  {
    this.eClass = eClass;
  }

  @Override
  protected EClass eDynamicClass()
  {
    return eClass;
  }
}

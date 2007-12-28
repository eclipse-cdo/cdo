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

import org.eclipse.emf.cdo.util.CDOFactory;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;

/**
 * @author Eike Stepper
 */
public class CDOFactoryImpl extends EFactoryImpl implements CDOFactory
{
  public CDOFactoryImpl(EPackage ePackage)
  {
    this.ePackage = ePackage;
  }

  // @Override
  // public EObject create(EClass eClass)
  // {
  // return new DynamicCDOObjectImpl(eClass);
  // }

  @Override
  protected EObject basicCreate(EClass eClass)
  {
    return new DynamicCDOObjectImpl(eClass);
  }
}

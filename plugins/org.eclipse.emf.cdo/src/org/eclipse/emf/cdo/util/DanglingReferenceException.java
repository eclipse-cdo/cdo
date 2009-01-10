/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.util.CDOException;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class DanglingReferenceException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private EObject target;

  public DanglingReferenceException(EObject object)
  {
    super("The object '" + object + "(" + object.getClass().getName() + ")' is not contained in a resource.");
    target = object;
  }

  public EObject getTarget()
  {
    return target;
  }
}

/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.util.CDOException;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.ecore.EObject;

import java.text.MessageFormat;

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
    super(MessageFormat.format(Messages.getString("DanglingReferenceException.0"), object, object.getClass().getName())); //$NON-NLS-1$
    target = object;
  }

  public EObject getTarget()
  {
    return target;
  }
}

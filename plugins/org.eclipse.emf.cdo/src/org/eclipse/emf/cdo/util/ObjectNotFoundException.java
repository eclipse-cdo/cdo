/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOException;

import org.eclipse.emf.internal.cdo.messages.Messages;

import java.text.MessageFormat;

/**
 * Exception occurs when an id doesn't exist on the server.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class ObjectNotFoundException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private final CDOID id;

  public ObjectNotFoundException(CDOID id)
  {
    super(MessageFormat.format(Messages.getString("ObjectNotFoundException.0"), id, id.isTemporary())); //$NON-NLS-1$
    this.id = id;
  }

  /**
   * @since 3.0
   */
  public CDOID getID()
  {
    return id;
  }
}

/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOException;

/**
 * Exception occurs when an object isn't valid anymore. It was valid when we create it, but not anymore. The cause could
 * be that another {@link CDOTransaction} removed it.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class InvalidObjectException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public InvalidObjectException(CDOID id)
  {
    super("Object " + id + " isn't valid anymore.");
  }
}

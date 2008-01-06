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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.db.IObjectTypeCache;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class ObjectTypeCache extends Lifecycle implements IObjectTypeCache
{
  public ObjectTypeCache()
  {
  }

  public CDOClassRef getObjectType(Connection connection, CDOID id)
  {
    return null;
  }

  public void putObjectType(Connection connection, CDOID id, CDOClassRef type)
  {
  }
}

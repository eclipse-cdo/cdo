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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.IDBTable;

import java.util.Collections;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class NoMapping implements IMapping
{
  public static final IMapping INSTANCE = new NoMapping();

  private NoMapping()
  {
  }

  public IMappingStrategy getMappingStrategy()
  {
    return null;
  }

  public CDOClass getCDOClass()
  {
    return null;
  }

  public Set<IDBTable> getAffectedTables()
  {
    return Collections.emptySet();
  }

  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
  }
}

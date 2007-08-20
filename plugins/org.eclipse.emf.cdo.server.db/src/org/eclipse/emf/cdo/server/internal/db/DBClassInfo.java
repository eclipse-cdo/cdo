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

import org.eclipse.net4j.db.IDBTable;

/**
 * @author Eike Stepper
 */
public final class DBClassInfo extends DBInfo
{
  private IDBTable table;

  public DBClassInfo(int id)
  {
    super(id);
  }

  public IDBTable getTable()
  {
    return table;
  }

  public void setTable(IDBTable table)
  {
    if (this.table != table)
    {
      if (this.table != null)
      {
        throw new IllegalStateException("Table is already set");
      }

      this.table = table;
    }
  }
}

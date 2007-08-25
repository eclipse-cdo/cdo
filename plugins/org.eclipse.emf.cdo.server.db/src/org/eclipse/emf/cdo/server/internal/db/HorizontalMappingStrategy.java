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

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMapping;

import java.sql.ResultSet;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends MappingStrategy
{
  public HorizontalMappingStrategy()
  {
  }

  public String getType()
  {
    return "horizontal";
  }

  public boolean hasEfficientTypeLookup()
  {
    return false;
  }

  public Iterator<CDOID> readObjectIDs(IDBStoreAccessor storeAccessor, boolean withTypes)
  {
    return new ObjectIDIterator(this, storeAccessor, withTypes)
    {
      @Override
      protected ResultSet getNextResultSet()
      {
        return null;
      }
    };
  }

  public CDOClassRef readObjectType(IDBStoreAccessor storeAccessor, CDOID id)
  {
    return null;
  }

  @Override
  protected IMapping createMapping(CDOClass cdoClass)
  {
    if (cdoClass.isAbstract())
    {
      return null;
    }

    return new HorizontalMapping(this, cdoClass);
  }
}

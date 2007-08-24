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

import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IMapping;

import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;

/**
 * @author Eike Stepper
 */
public class VerticalMappingStrategy extends MappingStrategy
{
  private IDBTable rootTable;

  public VerticalMappingStrategy()
  {
    throw new UnsupportedOperationException();
  }

  public String getType()
  {
    return "vertical";
  }

  public IDBTable getRootTable()
  {
    if (rootTable == null)
    {
      IDBSchema schema = getStore().getSchema();
      rootTable = schema.addTable("cdo_revisions");
      initTable(rootTable, true);
    }

    return rootTable;
  }

  @Override
  protected IMapping createMapping(CDOClass cdoClass)
  {
    return new VerticalMapping(this, cdoClass);
  }
}

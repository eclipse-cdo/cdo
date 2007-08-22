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
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.db.IMapping;

import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class VerticalMappingStrategy extends StandardMappingStrategy
{
  public VerticalMappingStrategy()
  {
    throw new UnsupportedOperationException();
  }

  public String getType()
  {
    return "vertical";
  }

  @Override
  protected IMapping createMapping(CDOClass cdoClass)
  {
    return new VerticalMapping(this, cdoClass);
  }

  @Override
  protected IDBSchema createSchema()
  {
    IDBSchema schema = super.createSchema();
    IDBTable table = schema.addTable("CDO_REVISIONS");
    initTable(table, true);
    return schema;
  }

  @Override
  protected IDBField mapFeature(CDOClass cdoClass, CDOFeature cdoFeature, Set<IDBTable> affectedTables)
  {
    if (cdoFeature.getContainingClass() != cdoClass)
    {
      return null;
    }

    // TODO Implement method enclosing_type.enclosing_method()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}

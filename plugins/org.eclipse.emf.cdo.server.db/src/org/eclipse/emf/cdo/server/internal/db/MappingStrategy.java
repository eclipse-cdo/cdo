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
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBTable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy implements IMappingStrategy
{
  private IDBStore store;

  private Map<String, String> properties;

  public MappingStrategy()
  {
  }

  public IDBStore getStore()
  {
    return store;
  }

  public void setStore(IDBStore store)
  {
    this.store = store;
  }

  public Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap();
    }

    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public IMapping getMapping(CDOClass cdoClass)
  {
    IMapping mapping = ClassServerInfo.getMapping(cdoClass);
    if (mapping == NoMapping.INSTANCE)
    {
      return null;
    }

    if (mapping == null)
    {
      mapping = createMapping(cdoClass);
      ClassServerInfo.setMapping(cdoClass, mapping == null ? NoMapping.INSTANCE : mapping);
    }

    return mapping;
  }

  protected abstract IMapping createMapping(CDOClass cdoClass);

  @Override
  public String toString()
  {
    return getType();
  }

  protected void initTable(IDBTable table, boolean full)
  {
    table.addField("cdo_id", DBType.BIGINT);
    if (full)
    {
      table.addField("cdo_class", DBType.INTEGER);
      table.addField("cdo_version", DBType.INTEGER);
      table.addField("cdo_created", DBType.TIMESTAMP);
      table.addField("cdo_revised", DBType.TIMESTAMP);
      table.addField("cdo_resource", DBType.BIGINT);
      table.addField("cdo_container", DBType.BIGINT);
      table.addField("cdo_feature", DBType.INTEGER);
    }
  }
}
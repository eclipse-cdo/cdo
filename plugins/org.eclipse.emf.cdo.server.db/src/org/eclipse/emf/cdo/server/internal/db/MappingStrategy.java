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

  private Precedence precedence;

  private ToMany toMany;

  private ToOne toOne;

  private Map<Object, IDBTable> referenceTables = new HashMap();

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

  public Precedence getPrecedence()
  {
    if (precedence == null)
    {
      String value = getProperties().get("mappingPrecedence");
      precedence = value == null ? Precedence.STRATEGY : Precedence.valueOf(value);
    }

    return precedence;
  }

  public ToMany getToMany()
  {
    if (toMany == null)
    {
      String value = getProperties().get("toManyReferenceMapping");
      toMany = value == null ? ToMany.PER_REFERENCE : ToMany.valueOf(value);
    }

    return toMany;
  }

  public ToOne getToOne()
  {
    if (toOne == null)
    {
      String value = getProperties().get("toOneReferenceMapping");
      toOne = value == null ? ToOne.LIKE_ATTRIBUTES : ToOne.valueOf(value);
    }

    return toOne;
  }

  public Map<Object, IDBTable> getReferenceTables()
  {
    return referenceTables;
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

  @Override
  public String toString()
  {
    return getType();
  }

  protected abstract IMapping createMapping(CDOClass cdoClass);
}
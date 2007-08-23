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
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.IDBTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Mapping implements IMapping
{
  private IMappingStrategy mappingStrategy;

  private CDOClass cdoClass;

  private Set<IDBTable> affectedTables = new HashSet();

  public Mapping(IMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    this.mappingStrategy = mappingStrategy;
    this.cdoClass = cdoClass;
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public CDOClass getCDOClass()
  {
    return cdoClass;
  }

  public Set<IDBTable> getAffectedTables()
  {
    return affectedTables;
  }

  public List<IAttributeMapping> createAttributeMappings(CDOFeature[] features)
  {
    return null;
  }

  public List<IReferenceMapping> createReferenceMappings(CDOFeature[] features)
  {
    return null;
  }
}

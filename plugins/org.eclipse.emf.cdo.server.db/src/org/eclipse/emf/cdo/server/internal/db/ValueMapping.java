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
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class ValueMapping extends Mapping
{
  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  public ValueMapping(MappingStrategy mappingStrategy, CDOClass cdoClass, CDOFeature[] features)
  {
    super(mappingStrategy, cdoClass);
    mappingStrategy.initTable(getTable(), hasFullRevisionInfo());
    attributeMappings = createAttributeMappings(features);
    referenceMappings = createReferenceMappings(features);
  }

  public List<IAttributeMapping> createAttributeMappings(CDOFeature[] features)
  {
    return null;
  }

  public List<IReferenceMapping> createReferenceMappings(CDOFeature[] features)
  {
    return null;
  }

  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    if (attributeMappings != null)
    {
      writeAttributes(storeAccessor, revision);
    }

    if (referenceMappings != null)
    {
      writeReferences(storeAccessor, revision);
    }
  }

  protected void writeAttributes(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(getTable());
    builder.append(" VALUES (");
    appendRevisionInfo(builder, revision, hasFullRevisionInfo());
    builder.append(", ");

    for (IAttributeMapping attributeMapping : attributeMappings)
    {
      builder.append(", ");
      attributeMapping.appendValue(builder, revision);
    }

    builder.append(")");
    executeSQL(storeAccessor, builder.toString());
  }

  protected void writeReferences(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.writeReference(this, storeAccessor, revision);
    }
  }

  protected abstract boolean hasFullRevisionInfo();
}

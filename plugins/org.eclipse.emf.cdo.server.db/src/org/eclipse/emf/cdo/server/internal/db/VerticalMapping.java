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
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBTable;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerticalMapping extends IDInfoMapping
{
  private List<IMapping> superMappings;

  private IDBTable table;

  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  public VerticalMapping(IMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    super(mappingStrategy, cdoClass);
    for (CDOClass superType : cdoClass.getSuperTypes())
    {
      IMapping superMapping = mappingStrategy.getMapping(superType);
      if (superMapping != null)
      {
        if (superMappings == null)
        {
          superMappings = new ArrayList(0);
        }

        superMappings.add(superMapping);
      }
    }

    CDOFeature[] features = cdoClass.getFeatures();
    attributeMappings = createAttributeMappings(features);
    referenceMappings = createReferenceMappings(features);
  }

  public List<IMapping> getSuperMappings()
  {
    return superMappings;
  }

  @Override
  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    super.writeRevision(storeAccessor, revision);
    if (attributeMappings != null)
    {
      writeAttributes(storeAccessor, revision);
    }

    if (referenceMappings != null)
    {
      writeReferences(storeAccessor, revision);
    }

    if (superMappings != null)
    {
      for (IMapping superMapping : superMappings)
      {
        superMapping.writeRevision(storeAccessor, revision);
      }
    }
  }

  protected void writeAttributes(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    try
    {
      StringBuilder builder = new StringBuilder();
      builder.append("INSERT INTO ");
      builder.append(table);
      builder.append(" VALUES (");
      builder.append(revision.getID().getValue());
      for (IAttributeMapping attributeMapping : attributeMappings)
      {
        builder.append(", ");
        attributeMapping.appendValue(builder, revision);
      }

      builder.append(")");
      String sql = builder.toString();

      Statement statement = storeAccessor.getStatement();
      int count = statement.executeUpdate(sql);
      if (count != 1)
      {
        throw new DBException("Wrong update count: " + count);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected void writeReferences(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.writeReference(this, storeAccessor, revision);
    }
  }
}

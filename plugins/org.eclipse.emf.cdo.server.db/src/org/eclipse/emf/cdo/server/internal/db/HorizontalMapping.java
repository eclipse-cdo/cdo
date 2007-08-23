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
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBTable;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class HorizontalMapping extends FullInfoMapping
{
  private IDBTable table;

  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  public HorizontalMapping(IMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    super(mappingStrategy, cdoClass);
    CDOFeature[] features = cdoClass.getAllFeatures();
    attributeMappings = mappingStrategy.getAttributeMappings(features);
    referenceMappings = mappingStrategy.getReferenceMappings(features);
  }

  @Override
  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    super.writeRevision(storeAccessor, revision);
    writeAttributes(storeAccessor, revision);
    writeReferences(storeAccessor, revision);
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
      builder.append(", ");
      builder.append(ServerInfo.getDBID(revision.getCDOClass()));
      builder.append(", ");
      builder.append(revision.getVersion());
      builder.append(", ");
      builder.append(new Date(revision.getCreated()));
      builder.append(", ");
      builder.append(new Date(revision.getRevised()));
      builder.append(", ");
      builder.append(revision.getResourceID().getValue());
      builder.append(", ");
      builder.append(revision.getContainerID().getValue());
      builder.append(", ");
      builder.append(revision.getContainingFeatureID());
      builder.append(", ");

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

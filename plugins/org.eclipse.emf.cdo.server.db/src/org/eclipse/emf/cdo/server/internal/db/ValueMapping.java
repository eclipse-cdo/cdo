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
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class ValueMapping extends Mapping
{
  private static final long NO_TIMESTAMP = 0L;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ValueMapping.class);

  private List<IAttributeMapping> attributeMappings;

  private List<IReferenceMapping> referenceMappings;

  public ValueMapping(MappingStrategy mappingStrategy, CDOClass cdoClass, CDOFeature[] features)
  {
    super(mappingStrategy, cdoClass);
    initTable(getTable(), hasFullRevisionInfo());
    attributeMappings = createAttributeMappings(features);
    referenceMappings = createReferenceMappings(features);
  }

  public List<IAttributeMapping> getAttributeMappings()
  {
    return attributeMappings;
  }

  public List<IReferenceMapping> getReferenceMappings()
  {
    return referenceMappings;
  }

  public IReferenceMapping getReferenceMapping(CDOFeature feature)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      if (referenceMapping.getFeature() == feature)
      {
        return referenceMapping;
      }
    }

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

  public void readRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    readRevisionByTime(storeAccessor, revision, NO_TIMESTAMP);
  }

  public void readRevisionByTime(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, long timeStamp)
  {
    if (attributeMappings != null)
    {
      readAttributes(storeAccessor, revision, timeStamp);
    }

    if (referenceMappings != null)
    {
      readReferences(storeAccessor, revision);
    }
  }

  public void readRevisionByVersion(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, int version)
  {
    // TODO Implement method ValueMapping.readRevisionByVersion()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  protected void writeAttributes(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(getTable());
    builder.append(" VALUES (");
    appendRevisionInfos(builder, revision, hasFullRevisionInfo());

    for (IAttributeMapping attributeMapping : attributeMappings)
    {
      builder.append(", ");
      attributeMapping.appendValue(builder, revision);
    }

    builder.append(")");
    sqlUpdate(storeAccessor, builder.toString());
  }

  protected void writeReferences(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.writeReference(storeAccessor, revision);
    }
  }

  protected void readAttributes(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, long timeStamp)
  {
    IDBField[] fields = getTable().getFields();
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    for (int i = 1; i < 8; i++)
    {
      if (i > 1)
      {
        builder.append(", ");
      }

      builder.append(fields[i].getName());
    }

    builder.append(" FROM ");
    builder.append(getTable());
    builder.append(" WHERE ");
    builder.append(fields[0].getName());
    builder.append("=");
    builder.append(revision.getID().getValue());
    builder.append(" AND ");
    builder.append(fields[0].getName());
    builder.append("=0");

    for (IAttributeMapping attributeMapping : attributeMappings)
    {
      builder.append(", ");
      attributeMapping.appendValue(builder, revision);
    }

    builder.append(")");
    String sql = builder.toString();

    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

  }

  protected void readReferences(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    for (IReferenceMapping referenceMapping : referenceMappings)
    {
      referenceMapping.readReference(storeAccessor, revision);
    }
  }

  protected List<IAttributeMapping> createAttributeMappings(CDOFeature[] features)
  {
    List<IAttributeMapping> attributeMappings = new ArrayList<IAttributeMapping>();
    for (CDOFeature feature : features)
    {
      if (feature.isReference())
      {
        if (!feature.isMany())
        {
          attributeMappings.add(createToOneReferenceMapping(feature));
        }
      }
      else
      {
        attributeMappings.add(createAttributeMapping(feature));
      }
    }

    return attributeMappings.isEmpty() ? null : attributeMappings;
  }

  protected List<IReferenceMapping> createReferenceMappings(CDOFeature[] features)
  {
    List<IReferenceMapping> referenceMappings = new ArrayList<IReferenceMapping>();
    for (CDOFeature feature : features)
    {
      if (feature.isReference() && feature.isMany())
      {
        referenceMappings.add(createReferenceMapping(feature));
      }
    }

    return referenceMappings.isEmpty() ? null : referenceMappings;
  }

  protected AttributeMapping createAttributeMapping(CDOFeature feature)
  {
    return new AttributeMapping(this, feature);
  }

  protected ToOneReferenceMapping createToOneReferenceMapping(CDOFeature feature)
  {
    return new ToOneReferenceMapping(this, feature);
  }

  protected ReferenceMapping createReferenceMapping(CDOFeature feature)
  {
    return new ReferenceMapping(this, feature, ToMany.PER_REFERENCE);
  }

  protected abstract boolean hasFullRevisionInfo();
}

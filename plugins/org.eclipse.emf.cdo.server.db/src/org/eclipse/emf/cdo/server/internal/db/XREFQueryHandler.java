/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 *    Kai Schlamp - Bug 284812: [DB] Query non CDO object fails
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.AbstractMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractHorizontalClassMapping;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
@Deprecated
public class XREFQueryHandler implements IQueryHandler
{
  public static final String QUERY_LANGUAGE = "XREF"; //$NON-NLS-1$

  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    String language = info.getQueryLanguage();
    if (!QUERY_LANGUAGE.equals(language))
    {
      throw new IllegalArgumentException("Unsupported query language: " + language);
    }

    CDOID targetID = (CDOID)info.getParameters().get("targetID");
    if (targetID == null)
    {
      throw new IllegalArgumentException("targetID is null");
    }
    if (targetID.isNull())
    {
      throw new IllegalArgumentException("targetID.isNull() == true");
    }
    if (targetID.isTemporary())
    {
      throw new IllegalArgumentException("targetID.isTemporary() == true");
    }

    Object tempObject = info.getParameters().get("provideFeatureName");
    /*
     * Sometimes its interesting to know through which EStructuralFeature the referencee is being referenced. This flag
     * makes it configurable.
     */
    boolean provideFeatureName = tempObject == null ? false : (Boolean)tempObject;

    IRepository repository = context.getView().getSession().getManager().getRepository();
    IDBStore store = (IDBStore)repository.getStore();

    // Provided CDOID
    CDOID metaID = getMetaId(targetID, context);
    if (metaID == null || metaID.isNull())
    {
      return;
    }

    EModelElement element = store.getMetaDataManager().getMetaInstance(CDOIDUtil.getLong(metaID));

    EClass targetClass = (EClass)element;

    AbstractMappingStrategy mappingStrategy = (AbstractMappingStrategy)store.getMappingStrategy();
    for (IClassMapping classMapping : mappingStrategy.getClassMappings().values())
    {
      IDBTable valueTable = classMapping.getDBTables().iterator().next();
      AbstractHorizontalClassMapping ahcm = (AbstractHorizontalClassMapping)classMapping;

      List<EReference> pointingReferences = new ArrayList<EReference>();

      for (ITypeMapping valueMapping : ahcm.getValueMappings())
      {
        EStructuralFeature feature = valueMapping.getFeature();
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (canPointTo(reference, targetClass))
          {
            pointingReferences.add(reference);
          }
        }
      }
      if (pointingReferences.size() > 0)
      {
        if (provideFeatureName)
        {
          xrefValueTableProvideFeature(valueTable, pointingReferences, targetID, info, context, provideFeatureName);
        }
        else
        {
          xrefValueTable(valueTable, pointingReferences, targetID, info, context, provideFeatureName);
        }
      }

      for (IListMapping listMapping : ahcm.getListMappings())
      {
        IDBTable listTable = listMapping.getDBTables().iterator().next();
        EStructuralFeature feature = listMapping.getFeature();
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (canPointTo(reference, targetClass))
          {
            xrefListTable(listTable, reference, targetID, info, context, provideFeatureName);
          }
        }
      }
    }
  }

  private CDOID getMetaId(CDOID targetID, IQueryContext context)
  {
    DBStore store = (DBStore)context.getView().getRepository().getStore();
    Connection connection = store.getConnection();

    String query = "SELECT cdo_class FROM CDO_OBJECTS WHERE cdo_id = " + CDOIDUtil.getLong(targetID);
    try
    {
      PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        return CDOIDUtil.createMeta(resultSet.getLong(1));
      }

    }
    catch (SQLException ex)
    {
      OM.LOG.error(ex);
    }
    return null;
  }

  private void xrefListTable(IDBTable listTable, EReference reference, CDOID targetID, CDOQueryInfo info,
      IQueryContext context, boolean provideFeatureName)
  {
    DBStore store = (DBStore)context.getView().getRepository().getStore();
    Connection connection = store.getConnection();

    String query = "SELECT cdo_source FROM " + listTable.getFullName() + " WHERE cdo_value = "
        + CDOIDUtil.getLong(targetID);
    try
    {
      PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      fetchResults(statement, info, context, reference, provideFeatureName);
    }
    catch (SQLException ex)
    {
      OM.LOG.error(ex);
    }
  }

  private void xrefValueTableProvideFeature(IDBTable valueTable, List<EReference> references, CDOID targetID,
      CDOQueryInfo info, IQueryContext context, boolean provideFeatureName)
  {
    DBStore store = (DBStore)context.getView().getRepository().getStore();
    Connection connection = store.getConnection();
    for (int i = 0; i < references.size(); i++)
    {
      StringBuilder queryBuilder = new StringBuilder();
      queryBuilder.append("SELECT cdo_id FROM ");
      queryBuilder.append(valueTable.getFullName());
      queryBuilder.append(" WHERE ");
      String featureName = store.getMappingStrategy().getFieldName(references.get(i));
      queryBuilder.append(" (");
      queryBuilder.append(featureName);
      queryBuilder.append(" = ");
      queryBuilder.append(CDOIDUtil.getLong(targetID));
      queryBuilder.append(") ");
      try
      {
        PreparedStatement statement = connection.prepareStatement(queryBuilder.toString(),
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        fetchResults(statement, info, context, references.get(i), provideFeatureName);
      }
      catch (SQLException ex)
      {
        OM.LOG.error(ex);
      }
    }

  }

  private void xrefValueTable(IDBTable valueTable, List<EReference> references, CDOID targetID, CDOQueryInfo info,
      IQueryContext context, boolean provideFeatureName)
  {
    DBStore store = (DBStore)context.getView().getRepository().getStore();
    Connection connection = store.getConnection();

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT cdo_id FROM ");
    queryBuilder.append(valueTable.getFullName());
    queryBuilder.append(" WHERE ");
    for (int i = 0; i < references.size(); i++)
    {
      if (i != 0)
      {
        queryBuilder.append(" OR ");
      }

      String featureName = store.getMappingStrategy().getFieldName(references.get(i));
      queryBuilder.append(featureName);
      queryBuilder.append("=");
      queryBuilder.append(CDOIDUtil.getLong(targetID));
    }

    try
    {
      PreparedStatement statement = connection.prepareStatement(queryBuilder.toString(),
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      fetchResults(statement, info, context, null, provideFeatureName);
    }
    catch (SQLException ex)
    {
      OM.LOG.error(ex);
    }

  }

  private boolean canPointTo(EReference reference, EClass targetClass)
  {
    EClass eType = (EClass)reference.getEType();
    return targetClass.equals(eType) || targetClass.getEAllSuperTypes().contains(eType);
  }

  private void fetchResults(PreparedStatement statement, CDOQueryInfo info, IQueryContext context,
      EReference reference, boolean provideFeatureName) throws SQLException
  {
    ResultSet resultSet = statement.executeQuery();

    int maxResults = info.getMaxResults();
    int counter = 0;
    while (resultSet.next())
    {
      counter++;
      if (maxResults != CDOQueryInfo.UNLIMITED_RESULTS && counter >= maxResults)
      {
        break;
      }

      CDOID cdoid = CDOIDUtil.createLong(resultSet.getLong(1));
      context.addResult(cdoid);
    }
    if (counter > 0)
    {
      if (provideFeatureName && reference != null)
      {
        context.addResult(reference.getName());
      }
    }

    DBUtil.close(statement);
  }
}

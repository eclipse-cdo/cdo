package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ReferenceMapping extends FeatureMapping implements IReferenceMapping
{
  public static final String FIELD_NAME_FEATURE = "cdo_feature";

  public static final String FIELD_NAME_SOURCE = "cdo_source";

  public static final String FIELD_NAME_VERSION = "cdo_version";

  public static final String FIELD_NAME_IDX = "cdo_idx";

  public static final String FIELD_NAME_TARGET = "cdo_target";

  private IDBTable table;

  private ToMany toMany;

  private boolean withFeature;

  private String constantPrefix;

  public ReferenceMapping(ValueMapping valueMapping, CDOFeature feature, ToMany toMany)
  {
    super(valueMapping, feature);
    this.toMany = toMany;
    mapReference(valueMapping.getCDOClass(), feature);

    // Build the constant SQL prefix
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");
    if (withFeature)
    {
      builder.append(FeatureServerInfo.getDBID(feature));
      builder.append(", ");
    }

    constantPrefix = builder.toString();
  }

  public IDBTable getTable()
  {
    return table;
  }

  public void writeReference(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    int idx = 0;
    long source = revision.getID().getValue();
    int version = revision.getVersion();
    for (Object element : revision.getList(getFeature()))
    {
      long target = ((CDOID)element).getValue();
      StringBuilder builder = new StringBuilder(constantPrefix);
      builder.append(source);
      builder.append(", ");
      builder.append(version);
      builder.append(", ");
      builder.append(idx++);
      builder.append(", ");
      builder.append(target);
      builder.append(")");
      getValueMapping().sqlUpdate(storeAccessor, builder.toString());
    }
  }

  public void readReference(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    // TODO Implement method ReferenceMapping.readReference()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    IDBStoreAccessor storeAccessor = chunkReader.getStoreAccessor();
    CDOID source = chunkReader.getRevision().getID();
    int version = chunkReader.getRevision().getVersion();
    CDOFeature feature = chunkReader.getFeature();

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(FIELD_NAME_TARGET);
    builder.append(" FROM ");
    builder.append(table);
    builder.append(" WHERE ");
    if (withFeature)
    {
      builder.append(FIELD_NAME_FEATURE);
      builder.append("=");
      builder.append(FeatureServerInfo.getDBID(feature));
    }

    builder.append(" AND ");
    builder.append(FIELD_NAME_SOURCE);
    builder.append("=");
    builder.append(source.getValue());
    builder.append(" AND ");
    builder.append(FIELD_NAME_VERSION);
    builder.append("=");
    builder.append(version);
    builder.append(where);
    builder.append(" ORDER BY ");
    builder.append(FIELD_NAME_IDX);

    String sql = builder.toString();
    ResultSet resultSet = null;

    try
    {
      Chunk chunk = null;
      int chunkSize = 0;
      int chunkIndex = 0;
      int indexInChunk = 0;

      resultSet = storeAccessor.getStatement().executeQuery(sql);
      while (resultSet.next())
      {
        long target = resultSet.getLong(1);
        if (chunk == null)
        {
          chunk = chunks.get(chunkIndex++);
          chunkSize = chunk.size();
        }

        chunk.addID(indexInChunk++, CDOIDImpl.create(target));
        if (indexInChunk == chunkSize)
        {
          chunk = null;
          indexInChunk = 0;
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  protected void mapReference(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    switch (toMany)
    {
    case PER_REFERENCE:
      withFeature = false;
      table = mapReferenceTable(cdoFeature, cdoClass.getName() + "_" + cdoFeature.getName() + "_refs");
      break;

    case PER_CLASS:
      withFeature = true;
      table = mapReferenceTable(cdoClass, cdoClass.getName() + "_refs");
      break;

    case PER_PACKAGE:
      withFeature = true;
      CDOPackage cdoPackage = cdoClass.getContainingPackage();
      table = mapReferenceTable(cdoPackage, cdoPackage.getName() + "_refs");
      break;

    case PER_REPOSITORY:
      withFeature = true;
      IRepository repository = getValueMapping().getMappingStrategy().getStore().getRepository();
      table = mapReferenceTable(repository, repository.getName() + "_refs");
      break;

    default:
      throw new IllegalArgumentException("Invalid mapping: " + toMany);
    }
  }

  protected IDBTable mapReferenceTable(Object key, String tableName)
  {
    Map<Object, IDBTable> referenceTables = getValueMapping().getMappingStrategy().getReferenceTables();
    IDBTable table = referenceTables.get(key);
    if (table == null)
    {
      table = addReferenceTable(tableName);
      referenceTables.put(key, table);
    }

    return table;
  }

  protected IDBTable addReferenceTable(String tableName)
  {
    IDBTable table = getValueMapping().addTable(tableName);
    if (withFeature)
    {
      table.addField(FIELD_NAME_FEATURE, DBType.INTEGER);
    }

    table.addField(FIELD_NAME_SOURCE, DBType.BIGINT);
    table.addField(FIELD_NAME_VERSION, DBType.INTEGER);
    table.addField(FIELD_NAME_IDX, DBType.INTEGER);
    table.addField(FIELD_NAME_TARGET, DBType.BIGINT);
    return table;
  }

}
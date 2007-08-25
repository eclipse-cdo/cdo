package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBTable;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ReferenceMapping extends FeatureMapping implements IReferenceMapping
{
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
      table.addField("cdo_feature", DBType.INTEGER);
    }

    table.addField("cdo_source", DBType.BIGINT);
    table.addField("cdo_version", DBType.INTEGER);
    table.addField("cdo_idx", DBType.INTEGER);
    table.addField("cdo_target", DBType.BIGINT);
    return table;
  }

}
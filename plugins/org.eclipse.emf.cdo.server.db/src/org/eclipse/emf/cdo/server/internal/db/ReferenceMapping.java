package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
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

  public ReferenceMapping(ValueMapping valueMapping, CDOFeature feature)
  {
    super(valueMapping, feature);
    table = mapReference(valueMapping.getCDOClass(), feature, ToMany.PER_REFERENCE);
  }

  public IDBTable getTable()
  {
    return table;
  }

  public void writeReference(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
  }

  protected IDBTable mapReference(CDOClass cdoClass, CDOFeature cdoFeature, ToMany mapping)
  {
    switch (mapping)
    {
    case PER_REFERENCE:
      return mapReferenceTable(cdoFeature, cdoClass.getName() + "_" + cdoFeature.getName() + "_refs", false);
    case PER_CLASS:
      return mapReferenceTable(cdoClass, cdoClass.getName() + "_refs", true);
    case PER_PACKAGE:
      CDOPackage cdoPackage = cdoClass.getContainingPackage();
      return mapReferenceTable(cdoPackage, cdoPackage.getName() + "_refs", true);
    case PER_REPOSITORY:
      IRepository repository = getValueMapping().getMappingStrategy().getStore().getRepository();
      return mapReferenceTable(repository, repository.getName() + "_refs", true);
    default:
      throw new IllegalArgumentException("Invalid mapping: " + mapping);
    }
  }

  protected IDBTable mapReferenceTable(Object key, String tableName, boolean withFeature)
  {
    Map<Object, IDBTable> referenceTables = getValueMapping().getMappingStrategy().getReferenceTables();
    IDBTable table = referenceTables.get(key);
    if (table == null)
    {
      table = addReferenceTable(tableName, withFeature);
      referenceTables.put(key, table);
    }

    return table;
  }

  protected IDBTable addReferenceTable(String tableName, boolean withFeature)
  {
    IDBTable table = getValueMapping().addTable(tableName);
    if (withFeature)
    {
      table.addField("cdo_feature", DBType.INTEGER);
    }

    table.addField("cdo_idx", DBType.INTEGER);
    table.addField("cdo_source", DBType.BIGINT);
    table.addField("cdo_target", DBType.BIGINT);
    return table;
  }

}
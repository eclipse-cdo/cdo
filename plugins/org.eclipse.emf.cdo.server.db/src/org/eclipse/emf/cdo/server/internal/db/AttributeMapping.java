package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBField;

import java.sql.ResultSet;

/**
 * @author Eike Stepper
 */
public abstract class AttributeMapping extends FeatureMapping implements IAttributeMapping
{
  private IDBField field;

  public AttributeMapping(ValueMapping valueMapping, CDOFeature feature)
  {
    super(valueMapping, feature);
    field = valueMapping.addField(feature, valueMapping.getTable());
  }

  public IDBField getField()
  {
    return field;
  }

  public void appendValue(StringBuilder builder, CDORevisionImpl revision)
  {
    IDBAdapter dbAdapter = getDBAdapter();
    Object value = getRevisionValue(revision);
    dbAdapter.appendValue(builder, field, value);
  }

  protected Object getRevisionValue(CDORevisionImpl revision)
  {
    CDOFeature feature = getFeature();
    return revision.getValue(feature);
  }

  public void extractValue(ResultSet resultSet, int column, CDORevisionImpl revision)
  {
    revision.setValue(getFeature(), getResultSetValue(resultSet, column));
  }

  protected abstract Object getResultSetValue(ResultSet resultSet, int column);
}
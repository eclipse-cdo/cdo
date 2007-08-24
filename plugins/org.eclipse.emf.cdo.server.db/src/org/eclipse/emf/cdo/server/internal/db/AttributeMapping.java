package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;

import org.eclipse.net4j.db.IDBField;

/**
 * @author Eike Stepper
 */
public class AttributeMapping extends FeatureMapping implements IAttributeMapping
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
    Object value = getRevisionValue(revision);
    field.appendValue(builder, value);
  }

  protected Object getRevisionValue(CDORevisionImpl revision)
  {
    CDOFeature feature = getFeature();
    return revision.getValue(feature);
  }
}
/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/212958
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PerfTracer;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDORevisionImpl implements InternalCDORevision
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionImpl.class);

  private static final PerfTracer READING = new PerfTracer(OM.PERF_REVISION_READING, CDORevisionImpl.class);

  private static final PerfTracer WRITING = new PerfTracer(OM.PERF_REVISION_WRITING, CDORevisionImpl.class);

  private CDOClass cdoClass;

  private CDOID id;

  private int version;

  private long created;

  private long revised;

  private CDOID resourceID;

  private Object containerID;

  private int containingFeatureID;

  private Object[] values;

  public CDORevisionImpl(CDOClass cdoClass, CDOID id)
  {
    if (cdoClass.isAbstract())
    {
      throw new IllegalArgumentException("CDOClass is abstract: " + cdoClass);
    }

    if (id == null || id.isNull())
    {
      throw new IllegalArgumentException("CDIID is null");
    }

    this.cdoClass = cdoClass;
    this.id = id;
    version = 0;
    created = UNSPECIFIED_DATE;
    revised = UNSPECIFIED_DATE;
    resourceID = CDOID.NULL;
    containerID = CDOID.NULL;
    containingFeatureID = 0;
    values = new Object[cdoClass.getAllFeatures().length];
  }

  public CDORevisionImpl(CDORevisionImpl source)
  {
    cdoClass = source.cdoClass;
    id = source.id;
    version = source.version;
    created = source.created;
    revised = source.revised; // == UNSPECIFIED
    resourceID = source.resourceID;
    containerID = source.containerID;
    containingFeatureID = source.containingFeatureID;
    copyValues(source.values);
  }

  public CDORevisionImpl(CDODataInput in) throws IOException
  {
    READING.start(this);
    cdoClass = in.readCDOClassRefAndResolve();

    id = in.readCDOID();
    version = in.readInt();
    if (!id.isTemporary())
    {
      created = in.readLong();
      revised = in.readLong();
    }

    resourceID = in.readCDOID();
    containerID = in.readCDOID();
    containingFeatureID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Reading revision: ID={0}, className={1}, version={2}, created={3}, revised={4}, resource={5}, container={6}, feature={7}",
              id, cdoClass.getName(), version, created, revised, resourceID, containerID, containingFeatureID);
    }

    readValues(in);
    READING.stop(this);
  }

  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    CDOClassRef classRef = cdoClass.createClassRef();
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Writing revision: ID={0}, classRef={1}, className={2}, version={3}, created={4}, revised={5}, resource={6}, container={7}, feature={8}",
              id, classRef, cdoClass.getName(), getVersion(), created, revised, resourceID, containerID,
              containingFeatureID);
    }

    WRITING.start(this);
    out.writeCDOClassRef(classRef);
    out.writeCDOID(id);
    out.writeInt(getVersion());
    if (!id.isTemporary())
    {
      out.writeLong(created);
      out.writeLong(revised);
    }

    out.writeCDOID(resourceID);
    Object newContainerID = out.getIDProvider().provideCDOID(containerID);
    out.writeCDOID((CDOID)newContainerID);
    out.writeInt(containingFeatureID);
    writeValues(out, referenceChunk);
    WRITING.stop(this);
  }

  public CDOClass getCDOClass()
  {
    return cdoClass;
  }

  public CDOID getID()
  {
    return id;
  }

  public void setID(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  public int getVersion()
  {
    return version < 0 ? -version : version;
  }

  public void setVersion(int version)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting version for {0}: v{1}", this, version);
    }

    this.version = version;
  }

  public boolean isTransactional()
  {
    return version < 0;
  }

  public int setTransactional()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting transactional {0}: v{1}", this, -(version + 1));
    }

    version = -(version + 1);
    return version;
  }

  public void setUntransactional()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting untransactional {0}: v{1}", this, Math.abs(version));
    }

    version = Math.abs(version);
  }

  public long getCreated()
  {
    return created;
  }

  public void setCreated(long created)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting created {0}: {1,date} {1,time}", this, created);
    }

    this.created = created;
  }

  public long getRevised()
  {
    return revised;
  }

  public void setRevised(long revised)
  {
    if (revised != UNSPECIFIED_DATE && revised < created)
    {
      throw new IllegalArgumentException("created=" + created + ", revised=" + revised);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revised {0}: {1,date} {1,time}", this, revised);
    }

    this.revised = revised;
  }

  public boolean isCurrent()
  {
    return revised == UNSPECIFIED_DATE;
  }

  public boolean isValid(long timeStamp)
  {
    return (revised == UNSPECIFIED_DATE || revised >= timeStamp) && timeStamp >= created;
  }

  public boolean isResourceNode()
  {
    return cdoClass.isResourceNode();
  }

  public boolean isResourceFolder()
  {
    return cdoClass.isResourceFolder();
  }

  public boolean isResource()
  {
    return cdoClass.isResource();
  }

  public CDORevisionData getData()
  {
    return this;
  }

  public CDORevision getRevision()
  {
    return this;
  }

  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    return (InternalCDORevisionDelta)CDORevisionDeltaUtil.create(origin, this);
  }

  public void merge(CDORevisionDelta delta)
  {
    CDORevisionMerger applier = new CDORevisionMerger();
    applier.merge(this, delta);
  }

  public CDOID getResourceID()
  {
    return resourceID;
  }

  public void setResourceID(CDOID resourceID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resourceID {0}: {1}", this, resourceID);
    }

    this.resourceID = resourceID;
  }

  public Object getContainerID()
  {
    return containerID;
  }

  public void setContainerID(Object containerID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting containerID {0}: {1}", this, containerID);
    }

    this.containerID = containerID;
  }

  public int getContainingFeatureID()
  {
    return containingFeatureID;
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting containingFeatureID {0}: {1}", this, containingFeatureID);
    }

    this.containingFeatureID = containingFeatureID;
  }

  public int hashCode(CDOFeature feature)
  {
    return getValue(feature).hashCode();
  }

  public Object get(CDOFeature feature, int index)
  {
    if (feature.isMany())
    {
      return getList(feature).get(index);
    }

    return getValue(feature);
  }

  public boolean contains(CDOFeature feature, Object value)
  {
    return getList(feature).contains(value);
  }

  public int indexOf(CDOFeature feature, Object value)
  {
    return getList(feature).indexOf(value);
  }

  public boolean isEmpty(CDOFeature feature)
  {
    return getList(feature).isEmpty();
  }

  public boolean isSet(CDOFeature feature)
  {
    return getValue(feature) != null;
  }

  public int lastIndexOf(CDOFeature feature, Object value)
  {
    return getList(feature).lastIndexOf(value);
  }

  public int size(CDOFeature feature)
  {
    return getList(feature).size();
  }

  public Object[] toArray(CDOFeature feature)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    return getList(feature).toArray();
  }

  public <T> T[] toArray(CDOFeature feature, T[] array)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    return getList(feature).toArray(array);
  }

  public void add(CDOFeature feature, int index, Object value)
  {
    getList(feature).add(index, value);
  }

  public void clear(CDOFeature feature)
  {
    setValue(feature, null);
  }

  public Object move(CDOFeature feature, int targetIndex, int sourceIndex)
  {
    return getList(feature).move(targetIndex, sourceIndex);
  }

  public Object remove(CDOFeature feature, int index)
  {
    return getList(feature).remove(index);
  }

  public Object set(CDOFeature feature, int index, Object value)
  {
    if (feature.isMany())
    {
      return getList(feature).set(index, value);
    }

    return setValue(feature, value);
  }

  public void unset(CDOFeature feature)
  {
    setValue(feature, null);
  }

  public void adjustReferences(CDOReferenceAdjuster revisionAdjuster)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adjusting references for revision {0}", this);
    }

    resourceID = (CDOID)revisionAdjuster.adjustReference(resourceID);
    containerID = revisionAdjuster.adjustReference(containerID);

    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isReference())
      {
        if (feature.isMany())
        {
          InternalCDOList list = (InternalCDOList)getValueAsList(i);
          if (list != null)
          {
            list.adjustReferences(revisionAdjuster);
          }
        }
        else
        {
          values[i] = revisionAdjuster.adjustReference(values[i]);
        }
      }
    }
  }

  private CDOList getValueAsList(int i)
  {
    return (CDOList)values[i];
  }

  @Override
  public String toString()
  {
    return cdoClass.getName() + "@" + id + "v" + version;
  }

  public Object getValue(CDOFeature feature)
  {
    int i = cdoClass.getFeatureID(feature);
    return values[i];
  }

  public Object setValue(CDOFeature feature, Object value)
  {
    int i = cdoClass.getFeatureID(feature);

    try
    {
      Object old = values[i];
      values[i] = value;
      return old;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      throw new IllegalArgumentException("Could not find feature " + feature + " in class " + cdoClass, ex);
    }
  }

  public CDOList getList(CDOFeature feature)
  {
    return getList(feature, 0);
  }

  public CDOList getList(CDOFeature feature, int size)
  {
    int i = cdoClass.getFeatureID(feature);
    CDOList list = (CDOList)values[i];
    if (list == null && size != -1)
    {
      list = CDOListFactory.DEFAULT.createList(size, 0, 0);
      values[i] = list;
    }
    return list;
  }

  public void setList(CDOFeature feature, InternalCDOList list)
  {
    int i = cdoClass.getFeatureID(feature);
    values[i] = list;
  }

  public void setListSize(CDOFeature feature, int size)
  {
    MoveableList<Object> list = getList(feature, size);
    for (int j = list.size(); j < size; j++)
    {
      list.add(InternalCDORevision.UNINITIALIZED);
    }
  }

  private void copyValues(Object[] sourceValues)
  {
    CDOFeature[] features = cdoClass.getAllFeatures();
    values = new Object[features.length];
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      CDOType type = feature.getType();
      if (feature.isMany())
      {
        InternalCDOList sourceList = (InternalCDOList)sourceValues[i];
        if (sourceList != null)
        {
          values[i] = sourceList.clone(type);
        }
      }
      else
      {
        values[i] = type.copyValue(sourceValues[i]);
      }
    }
  }

  private void readValues(CDODataInput in) throws IOException
  {
    CDOFeature[] features = cdoClass.getAllFeatures();
    values = new Object[features.length];
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      CDOType type = feature.getType();
      if (feature.isMany())
      {
        values[i] = in.readCDOList(this, feature);
      }
      else
      {
        values[i] = type.readValue(in);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read feature {0}: {1}", feature, values[i]);
        }
      }
    }
  }

  private void writeValues(CDODataOutput out, int referenceChunk) throws IOException
  {
    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isMany())
      {
        out.writeCDOList(getValueAsList(i), feature, referenceChunk);
      }
      else
      {
        Object value = values[i];
        if (value != null && feature.isReference())
        {
          value = out.getIDProvider().provideCDOID(value);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Writing feature {0}: {1}", feature, value);
        }

        feature.getType().writeValue(out, value);
      }
    }
  }

  public static Object remapID(Object value, Map<CDOIDTemp, CDOID> idMappings)
  {
    if (value instanceof CDOIDTemp)
    {
      CDOIDTemp oldID = (CDOIDTemp)value;
      if (!oldID.isNull())
      {
        CDOID newID = idMappings.get(oldID);
        if (newID == null)
        {
          throw new ImplementationError("Missing ID mapping for " + oldID);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Adjusting ID: {0} --> {1}", oldID, newID);
        }

        return newID;
      }
    }

    return value;
  }
}

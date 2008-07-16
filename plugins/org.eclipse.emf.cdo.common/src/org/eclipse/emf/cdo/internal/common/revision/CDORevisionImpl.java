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
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.MoveableArrayList;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PerfTracer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDORevisionImpl implements InternalCDORevision
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionImpl.class);

  private static final PerfTracer READING = new PerfTracer(OM.PERF_REVISION_READING, CDORevisionImpl.class);

  private static final PerfTracer WRITING = new PerfTracer(OM.PERF_REVISION_WRITING, CDORevisionImpl.class);

  private CDORevisionResolver revisionResolver;

  private CDOClass cdoClass;

  private CDOID id;

  private int version;

  private long created;

  private long revised;

  private CDOID resourceID;

  private CDOID containerID;

  private int containingFeatureID;

  private Object[] values;

  public CDORevisionImpl(CDORevisionResolver revisionResolver, CDOClass cdoClass, CDOID id)
  {
    this.revisionResolver = revisionResolver;
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
    revisionResolver = source.revisionResolver;
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

  public CDORevisionImpl(ExtendedDataInput in, CDORevisionResolver revisionResolver, CDOPackageManager packageManager)
      throws IOException
  {
    this.revisionResolver = revisionResolver;

    READING.start(this);
    CDOClassRef classRef = CDOModelUtil.readClassRef(in);
    cdoClass = classRef.resolve(packageManager);
    if (cdoClass == null)
    {
      throw new IllegalStateException("ClassRef unresolveable: " + classRef);
    }

    id = CDOIDUtil.read(in, revisionResolver.getCDOIDObjectFactory());
    version = in.readInt();
    created = in.readLong();
    revised = in.readLong();
    resourceID = CDOIDUtil.read(in, revisionResolver.getCDOIDObjectFactory());
    containerID = CDOIDUtil.read(in, revisionResolver.getCDOIDObjectFactory());
    containingFeatureID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Reading revision: ID={0}, classRef={1}, className={2}, version={3}, created={4}, revised={5}, resource={6}, container={7}, feature={8}",
              id, classRef, cdoClass.getName(), version, created, revised, resourceID, containerID, containingFeatureID);
    }

    readValues(in);
    READING.stop(this);
  }

  public void write(ExtendedDataOutput out, CDOIDProvider idProvider, int referenceChunk) throws IOException
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
    CDOModelUtil.writeClassRef(out, classRef);
    CDOIDUtil.write(out, id);
    out.writeInt(getVersion());
    out.writeLong(created);
    out.writeLong(revised);
    CDOIDUtil.write(out, resourceID);
    CDOIDUtil.write(out, containerID);
    out.writeInt(containingFeatureID);
    writeValues(out, idProvider, referenceChunk);
    WRITING.stop(this);
  }

  public CDORevisionResolver getRevisionResolver()
  {
    return revisionResolver;
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

  public CDOID getContainerID()
  {
    return containerID;
  }

  public void setContainerID(CDOID containerID)
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

  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adjusting references for revision {0}", this);
    }

    resourceID = (CDOID)remapID(resourceID, idMappings);
    containerID = (CDOID)remapID(containerID, idMappings);

    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isReference())
      {
        if (feature.isMany())
        {
          List<Object> list = getValueAsList(i);
          int size = list == null ? 0 : list.size();
          for (int j = 0; j < size; j++)
          {
            Object oldID = list.get(j);
            Object newID = remapID(oldID, idMappings);
            if (newID != oldID)
            {
              list.set(j, newID);
            }
          }
        }
        else
        {
          values[i] = remapID(values[i], idMappings);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private List<Object> getValueAsList(int i)
  {
    return (List<Object>)values[i];
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
    Object old = values[i];
    values[i] = value;
    return old;
  }

  public MoveableList<Object> getList(CDOFeature feature)
  {
    return getList(feature, 0);
  }

  @SuppressWarnings("unchecked")
  public MoveableList<Object> getList(CDOFeature feature, int size)
  {
    int i = cdoClass.getFeatureID(feature);
    MoveableList<Object> list = (MoveableList<Object>)values[i];
    if (list == null)
    {
      list = new MoveableArrayList<Object>(size);
      values[i] = list;
    }

    return list;
  }

  public void setListSize(CDOFeature feature, int size)
  {
    MoveableList<Object> list = getList(feature, size);
    for (int j = list.size(); j < size; j++)
    {
      list.add(InternalCDORevision.UNINITIALIZED);
    }
  }

  @SuppressWarnings("unchecked")
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
        MoveableList<Object> sourceList = (MoveableList<Object>)sourceValues[i];
        if (sourceList != null)
        {
          int size = sourceList.size();
          MoveableList<Object> list = new MoveableArrayList<Object>(size);
          for (int j = 0; j < size; j++)
          {
            Object value = sourceList.get(j);
            if (value instanceof CDOReferenceProxy)
            {
              list.add(new CDOReferenceProxyImpl(this, feature, ((CDOReferenceProxy)value).getIndex()));
            }
            else
            {
              list.add(type.copyValue(value));
            }
          }

          values[i] = list;
        }
      }
      else
      {
        values[i] = type.copyValue(sourceValues[i]);
      }
    }
  }

  private void readValues(ExtendedDataInput in) throws IOException
  {
    CDOFeature[] features = cdoClass.getAllFeatures();
    values = new Object[features.length];
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      CDOType type = feature.getType();
      if (feature.isMany())
      {
        int referenceChunk;
        int size = in.readInt();
        if (size < 0)
        {
          size = -size;
          referenceChunk = in.readInt();
          if (TRACER.isEnabled())
          {
            TRACER.format("Read feature {0}: size={1}, referenceChunk={2}", feature, size, referenceChunk);
          }
        }
        else
        {
          referenceChunk = size;
          if (TRACER.isEnabled())
          {
            TRACER.format("Read feature {0}: size={1}", feature, size);
          }
        }

        if (size != 0)
        {
          CDORevisionImpl baseRevision = null;
          MoveableList<Object> list = new MoveableArrayList<Object>(size);
          values[i] = list;
          int ranges = in.readInt();
          if (ranges != 0)
          {
            // This happens only on server side
            while (ranges-- > 0)
            {
              int range = in.readInt();
              if (range > 0)
              {
                while (range-- > 0)
                {
                  Object value = type.readValue(in, revisionResolver.getCDOIDObjectFactory());
                  list.add(value);
                  if (TRACER.isEnabled())
                  {
                    TRACER.trace("    " + value);
                  }
                }
              }
              else
              {
                if (baseRevision == null)
                {
                  baseRevision = (CDORevisionImpl)revisionResolver.getRevisionByVersion(id, CDORevision.UNCHUNKED,
                      getVersion() - 1);
                }

                MoveableList<Object> baseList = baseRevision.getList(feature);
                int index = in.readInt();
                while (range++ < 0)
                {
                  Object value = baseList.get(index++);
                  list.add(value);
                  if (TRACER.isEnabled())
                  {
                    TRACER.trace("    " + value);
                  }
                }
              }
            }
          }
          else
          {
            for (int j = 0; j < referenceChunk; j++)
            {
              Object value = type.readValue(in, revisionResolver.getCDOIDObjectFactory());
              list.add(value);
              if (TRACER.isEnabled())
              {
                TRACER.trace("    " + value);
              }
            }

            for (int j = referenceChunk; j < size; j++)
            {
              list.add(new CDOReferenceProxyImpl(this, feature, j));
            }
          }
        }
      }
      else
      {
        values[i] = type.readValue(in, revisionResolver.getCDOIDObjectFactory());
        if (TRACER.isEnabled())
        {
          TRACER.format("Read feature {0}: {1}", feature, values[i]);
        }
      }
    }
  }

  private void writeValues(ExtendedDataOutput out, CDOIDProvider idProvider, int referenceChunk) throws IOException
  {
    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isMany())
      {
        List<Object> list = getValueAsList(i);
        int size = list == null ? 0 : list.size();
        if (referenceChunk != CDORevision.UNCHUNKED && referenceChunk < size)
        {
          // This happens only on server-side
          if (TRACER.isEnabled())
          {
            TRACER.format("Writing feature {0}: size={1}, referenceChunk={2}", feature, size, referenceChunk);
          }

          out.writeInt(-size);
          out.writeInt(referenceChunk);
          size = referenceChunk;
        }
        else
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Writing feature {0}: size={1}", feature, size);
          }

          out.writeInt(size);
        }

        if (size != 0)
        {
          List<Integer> ranges = revisionResolver.analyzeReferenceRanges(list);
          if (ranges != null)
          {
            // This happens only on client-side
            out.writeInt(ranges.size());
            int j = 0;
            for (int range : ranges)
            {
              out.writeInt(range);
              if (range > 0)
              {
                // This is an id range
                while (range-- > 0)
                {
                  Object value = list.get(j);
                  if (value != null && feature.isReference())
                  {
                    value = idProvider.provideCDOID(value);
                    list.set(j, value);
                  }

                  if (TRACER.isEnabled())
                  {
                    TRACER.trace("    " + value);
                  }

                  feature.getType().writeValue(out, value);
                  ++j;
                }
              }
              else
              {
                // This is a proxy range
                CDOReferenceProxy proxy = (CDOReferenceProxy)list.get(j);
                out.writeInt(proxy.getIndex());
                j -= range; // Increase j to next range start
              }
            }
          }
          else
          {
            out.writeInt(0); // No ranges -> no ref proxies
            for (int j = 0; j < size; j++)
            {
              Object value = list.get(j);
              if (value != null && feature.isReference())
              {
                value = idProvider.provideCDOID(value);
                list.set(j, value);
              }

              if (TRACER.isEnabled())
              {
                TRACER.trace("    " + value);
              }

              feature.getType().writeValue(out, value);
            }
          }
        }
      }
      else
      {
        if (values[i] != null && feature.isReference())
        {
          values[i] = idProvider.provideCDOID(values[i]);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Writing feature {0}: {1}", feature, values[i]);
        }

        feature.getType().writeValue(out, values[i]);
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

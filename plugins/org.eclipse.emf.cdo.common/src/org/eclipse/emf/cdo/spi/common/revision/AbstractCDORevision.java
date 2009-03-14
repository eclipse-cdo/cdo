/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDeltaUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PerfTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractCDORevision implements InternalCDORevision
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, AbstractCDORevision.class);

  private static final PerfTracer READING = new PerfTracer(OM.PERF_REVISION_READING, AbstractCDORevision.class);

  private static final PerfTracer WRITING = new PerfTracer(OM.PERF_REVISION_WRITING, AbstractCDORevision.class);

  private CDOClassInfo classAdapter;

  private CDOID id;

  private int version;

  private long created;

  private long revised;

  private CDOID resourceID;

  private Object containerID;

  private int containingFeatureID;

  public AbstractCDORevision(EClass eClass, CDOID id)
  {
    if (eClass.isAbstract())
    {
      throw new IllegalArgumentException("EClass is abstract: " + eClass);
    }

    if (CDOIDUtil.isNull(id))
    {
      throw new IllegalArgumentException("CDOID is null");
    }

    classAdapter = CDOModelUtil.getClassInfo(eClass);
    this.id = id;
    version = 0;
    created = UNSPECIFIED_DATE;
    revised = UNSPECIFIED_DATE;
    resourceID = CDOID.NULL;
    containerID = CDOID.NULL;
    containingFeatureID = 0;
    initValues(classAdapter.getAllPersistentFeatures());
  }

  public AbstractCDORevision(AbstractCDORevision source)
  {
    classAdapter = source.classAdapter;
    id = source.id;
    version = source.version;
    created = source.created;
    revised = source.revised; // == UNSPECIFIED
    resourceID = source.resourceID;
    containerID = source.containerID;
    containingFeatureID = source.containingFeatureID;
  }

  public AbstractCDORevision(CDODataInput in) throws IOException
  {
    READING.start(this);
    classAdapter = CDOModelUtil.getClassInfo((EClass)in.readCDOClassifierRefAndResolve());

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
              "Reading revision: ID={0}, className={1}, version={2}, created={3}, revised={4}, resource={5}, container={6}, featureID={7}",
              id, getEClass().getName(), version, created, revised, resourceID, containerID, containingFeatureID);
    }

    readValues(in);
    READING.stop(this);
  }

  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    CDOClassifierRef classRef = new CDOClassifierRef(getEClass());
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Writing revision: ID={0}, className={1}, version={2}, created={3}, revised={4}, resource={5}, container={6}, featureID={7}",
              id, getEClass().getName(), getVersion(), created, revised, resourceID, containerID, containingFeatureID);
    }

    WRITING.start(this);
    out.writeCDOClassifierRef(classRef);
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

  public EClass getEClass()
  {
    return classAdapter.getEClass();
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
    if (revised != UNSPECIFIED_DATE && revised < Math.max(0, created))
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
    return classAdapter.isResourceNode();
  }

  public boolean isResourceFolder()
  {
    return classAdapter.isResourceFolder();
  }

  public boolean isResource()
  {
    return classAdapter.isResource();
  }

  public CDORevisionData data()
  {
    return this;
  }

  public CDORevision revision()
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

  public int hashCode(EStructuralFeature feature)
  {
    return getValue(feature).hashCode();
  }

  public Object get(EStructuralFeature feature, int index)
  {
    if (feature.isMany())
    {
      return getList(feature).get(index);
    }

    return getValue(feature);
  }

  public Object basicGet(EStructuralFeature feature, int index)
  {
    if (feature.isMany())
    {
      return getList(feature).get(index);
    }

    return basicGet(feature);
  }

  public boolean contains(EStructuralFeature feature, Object value)
  {
    return getList(feature).contains(value);
  }

  public int indexOf(EStructuralFeature feature, Object value)
  {
    return getList(feature).indexOf(value);
  }

  public boolean isEmpty(EStructuralFeature feature)
  {
    return getList(feature).isEmpty();
  }

  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    return getList(feature).lastIndexOf(value);
  }

  public int size(EStructuralFeature feature)
  {
    return getList(feature).size();
  }

  public Object[] toArray(EStructuralFeature feature)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    return getList(feature).toArray();
  }

  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    return getList(feature).toArray(array);
  }

  public void add(EStructuralFeature feature, int index, Object value)
  {
    getList(feature).add(index, value);
  }

  public void clear(EStructuralFeature feature)
  {
    setValue(feature, null);
  }

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    return getList(feature).move(targetIndex, sourceIndex);
  }

  public Object remove(EStructuralFeature feature, int index)
  {
    return getList(feature).remove(index);
  }

  public Object set(EStructuralFeature feature, int index, Object value)
  {
    if (feature.isMany())
    {
      return getList(feature).set(index, value);
    }

    return setValue(feature, value);
  }

  public Object basicSet(EStructuralFeature feature, int index, Object value)
  {
    if (feature.isMany())
    {
      return getList(feature).set(index, value);
    }

    return basicSet(feature, value);
  }

  public void unset(EStructuralFeature feature)
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

    EStructuralFeature[] features = classAdapter.getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isMany())
        {
          InternalCDOList list = (InternalCDOList)getValueAsList(i);
          if (list != null)
          {
            list.adjustReferences(revisionAdjuster, reference.getEReferenceType());
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature.getEType());
          setValue(i, type.adjustReferences(revisionAdjuster, getValue(i)));
        }
      }
    }
  }

  @Override
  public String toString()
  {
    return getEClass().getName() + "@" + id + "v" + version;
  }

  public Object getValue(EStructuralFeature feature)
  {
    return convertValue(feature, basicGet(feature));
  }

  public Object setValue(EStructuralFeature feature, Object value)
  {
    return convertValue(feature, basicSet(feature, value));
  }

  protected Object convertValue(EStructuralFeature feature, Object value)
  {
    if (value == null)
    {
      value = feature.getDefaultValue();
    }
    else if (value == InternalCDORevision.NIL)
    {
      value = null;
    }

    return value;
  }

  protected Object basicGet(EStructuralFeature feature)
  {
    int featureIndex = classAdapter.getFeatureIndex(feature);
    return getValue(featureIndex);
  }

  protected Object basicSet(EStructuralFeature feature, Object value)
  {
    int featureIndex = classAdapter.getFeatureIndex(feature);

    try
    {
      Object old = getValue(featureIndex);
      setValue(featureIndex, value);
      return old;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      throw new IllegalArgumentException("Could not find feature " + feature + " in class " + classAdapter, ex);
    }
  }

  public CDOList getList(EStructuralFeature feature)
  {
    return getList(feature, 0);
  }

  public CDOList getList(EStructuralFeature feature, int size)
  {
    int featureIndex = classAdapter.getFeatureIndex(feature);
    CDOList list = (CDOList)getValue(featureIndex);
    if (list == null && size != -1)
    {
      list = CDOListFactory.DEFAULT.createList(size, 0, 0);
      setValue(featureIndex, list);
    }

    return list;
  }

  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    int featureIndex = classAdapter.getFeatureIndex(feature);
    setValue(featureIndex, list);
  }

  public void setListSize(EStructuralFeature feature, int size)
  {
    MoveableList<Object> list = getList(feature, size);
    for (int j = list.size(); j < size; j++)
    {
      list.add(InternalCDORevision.UNINITIALIZED);
    }
  }

  protected abstract void initValues(EStructuralFeature[] allPersistentFeatures);

  protected abstract Object getValue(int featureIndex);

  protected abstract void setValue(int featureIndex, Object value);

  private CDOList getValueAsList(int i)
  {
    return (CDOList)getValue(i);
  }

  private void readValues(CDODataInput in) throws IOException
  {
    EStructuralFeature[] features = classAdapter.getAllPersistentFeatures();
    initValues(features);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        setValue(i, in.readCDOList(this, feature));
      }
      else
      {
        setValue(i, in.readCDOFeatureValue(feature));
        if (TRACER.isEnabled())
        {
          TRACER.format("Read feature {0}: {1}", feature.getName(), getValue(i));
        }
      }
    }
  }

  private void writeValues(CDODataOutput out, int referenceChunk) throws IOException
  {
    EStructuralFeature[] features = classAdapter.getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        out.writeCDOList(getValueAsList(i), feature, referenceChunk);
      }
      else
      {
        Object value = getValue(i);
        if (value != null && feature instanceof EReference)
        {
          value = out.getIDProvider().provideCDOID(value);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Writing feature {0}: {1}", feature.getName(), value);
        }

        out.writeCDOFeatureValue(value, feature);
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

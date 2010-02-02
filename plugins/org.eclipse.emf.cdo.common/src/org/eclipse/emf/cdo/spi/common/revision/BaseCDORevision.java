/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
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
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PerfTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class BaseCDORevision extends AbstractCDORevision
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, BaseCDORevision.class);

  private static final PerfTracer READING = new PerfTracer(OM.PERF_REVISION_READING, BaseCDORevision.class);

  private static final PerfTracer WRITING = new PerfTracer(OM.PERF_REVISION_WRITING, BaseCDORevision.class);

  private static final byte UNSET = 0;

  private static final byte SET_NULL = 1;

  private static final byte SET_NOT_NULL = 2;

  private CDOClassInfo classInfo;

  private CDOID id;

  private CDOBranchPoint branchPoint;

  private int version;

  private long revised;

  private CDOID resourceID;

  private Object containerID;

  private int containingFeatureID;

  /**
   * @since 3.0
   */
  public BaseCDORevision(EClass eClass)
  {
    if (eClass != null)
    {
      if (eClass.isAbstract())
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("AbstractCDORevision.0"), eClass)); //$NON-NLS-1$
      }

      classInfo = CDOModelUtil.getClassInfo(eClass);
      version = UNSPECIFIED_VERSION;
      revised = UNSPECIFIED_DATE;
      resourceID = CDOID.NULL;
      containerID = CDOID.NULL;
      containingFeatureID = 0;
      initValues(classInfo.getAllPersistentFeatures());
    }
  }

  protected BaseCDORevision(BaseCDORevision source)
  {
    classInfo = source.classInfo;
    id = source.id;
    branchPoint = source.branchPoint;
    version = source.version;
    revised = source.revised; // == UNSPECIFIED_DATE
    resourceID = source.resourceID;
    containerID = source.containerID;
    containingFeatureID = source.containingFeatureID;
  }

  /**
   * @since 3.0
   */
  public void read(CDODataInput in) throws IOException
  {
    READING.start(this);
    EClassifier classifier = in.readCDOClassifierRefAndResolve();
    classInfo = CDOModelUtil.getClassInfo((EClass)classifier);

    id = in.readCDOID();
    branchPoint = in.readCDOBranchPoint();
    version = in.readInt();
    if (!id.isTemporary())
    {
      revised = in.readLong();
    }

    resourceID = in.readCDOID();
    containerID = in.readCDOID();
    containingFeatureID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Reading revision: ID={0}, className={1}, version={2}, branchPoint={3}, revised={4}, resource={5}, container={6}, featureID={7}", //$NON-NLS-1$
              id, getEClass().getName(), version, branchPoint, revised, resourceID, containerID, containingFeatureID);
    }

    readValues(in);
    READING.stop(this);
  }

  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    EClass eClass = getEClass();
    CDOClassifierRef classRef = new CDOClassifierRef(eClass);
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Writing revision: ID={0}, className={1}, version={2}, branchPoint={3}, revised={4}, resource={5}, container={6}, featureID={7}", //$NON-NLS-1$
              id, eClass.getName(), getVersion(), branchPoint, revised, resourceID, containerID, containingFeatureID);
    }

    WRITING.start(this);

    out.writeCDOClassifierRef(classRef);
    out.writeCDOID(id);
    out.writeCDOBranchPoint(branchPoint);
    out.writeInt(getVersion());
    if (!id.isTemporary())
    {
      out.writeLong(revised);
    }

    out.writeCDOID(resourceID);
    out.writeCDOID(out.getIDProvider().provideCDOID(containerID));
    out.writeInt(containingFeatureID);
    writeValues(out, referenceChunk);
    WRITING.stop(this);
  }

  /**
   * @see #write(CDODataOutput, int)
   * @since 3.0
   */
  public void convertEObjects(CDOIDProvider idProvider)
  {
    if (!(containerID instanceof CDOID))
    {
      containerID = idProvider.provideCDOID(containerID);
    }

    EStructuralFeature[] features = classInfo.getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        CDOList list = getValueAsList(i);
        if (list != null)
        {
          boolean isFeatureMap = FeatureMapUtil.isFeatureMap(feature);
          for (int j = 0; j < list.size(); j++)
          {
            Object value = list.get(j, false);
            EStructuralFeature innerFeature = feature; // Prepare for possible feature map
            if (isFeatureMap)
            {
              Entry entry = (FeatureMap.Entry)value;
              innerFeature = entry.getEStructuralFeature();
              value = entry.getValue();
            }

            if (value != null && innerFeature instanceof EReference)
            {
              CDOID newValue = idProvider.provideCDOID(value);
              if (newValue != value)
              {
                list.set(j, newValue);
              }
            }
          }
        }
      }
      else
      {
        checkNoFeatureMap(feature);
        Object value = getValue(i);
        if (value != null && feature instanceof EReference)
        {
          CDOID newValue = idProvider.provideCDOID(value);
          if (newValue != value)
          {
            setValue(i, newValue);
          }
        }
      }
    }
  }

  /**
   * @since 3.0
   */
  public CDOClassInfo getClassInfo()
  {
    return classInfo;
  }

  public CDOID getID()
  {
    return id;
  }

  public void setID(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      throw new IllegalArgumentException(Messages.getString("AbstractCDORevision.1")); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  /**
   * @since 3.0
   */
  public CDOBranch getBranch()
  {
    if (branchPoint == null)
    {
      return null;
    }

    return branchPoint.getBranch();
  }

  /**
   * @since 3.0
   */
  public long getTimeStamp()
  {
    if (branchPoint == null)
    {
      return UNSPECIFIED_DATE;
    }

    return branchPoint.getTimeStamp();
  }

  /**
   * @since 3.0
   */
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    branchPoint = CDOBranchUtil.createBranchPoint(branchPoint);
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting branchPoint {0}: {1}", this, branchPoint);
    }

    this.branchPoint = branchPoint;
  }

  public int getVersion()
  {
    return version;
  }

  public void setVersion(int version)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting version for {0}: v{1}", this, version);
    }

    this.version = version;
  }

  public long getRevised()
  {
    return revised;
  }

  public void setRevised(long revised)
  {
    long created = branchPoint.getTimeStamp();
    if (revised != UNSPECIFIED_DATE && revised < Math.max(0, created))
    {
      throw new IllegalArgumentException("revision=" + this + ", created=" + created + ", revised=" + revised);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revised {0}: {1,date} {1,time,HH:mm:ss:SSS}", this, revised);
    }

    this.revised = revised;
  }

  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    return (InternalCDORevisionDelta)CDORevisionDeltaUtil.create(origin, this);
  }

  public int compareTo(CDOBranchPoint o)
  {
    return branchPoint.compareTo(o);
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
    if (feature.isMany() && index != EStore.NO_INDEX)
    {
      return getList(feature).get(index);
    }

    return getValue(feature);
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

    EStructuralFeature[] features = classInfo.getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature instanceof EReference || FeatureMapUtil.isFeatureMap(feature))
      {
        if (feature.isMany())
        {
          InternalCDOList list = (InternalCDOList)getValueAsList(i);
          if (list != null)
          {
            list.adjustReferences(revisionAdjuster, feature);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature);
          setValue(i, type.adjustReferences(revisionAdjuster, getValue(i)));
        }
      }
    }
  }

  public Object getValue(EStructuralFeature feature)
  {
    int featureIndex = classInfo.getFeatureIndex(feature);
    return getValue(featureIndex);
  }

  public Object setValue(EStructuralFeature feature, Object value)
  {
    int featureIndex = classInfo.getFeatureIndex(feature);

    try
    {
      Object old = getValue(featureIndex);
      setValue(featureIndex, value);
      return old;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("AbstractCDORevision.20"), feature,
          classInfo), ex);
    }
  }

  public CDOList getList(EStructuralFeature feature)
  {
    return getList(feature, 0);
  }

  public CDOList getList(EStructuralFeature feature, int size)
  {
    int featureIndex = classInfo.getFeatureIndex(feature);
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
    int featureIndex = classInfo.getFeatureIndex(feature);
    setValue(featureIndex, list);
  }

  protected abstract void initValues(EStructuralFeature[] allPersistentFeatures);

  protected abstract Object getValue(int featureIndex);

  protected abstract void setValue(int featureIndex, Object value);

  private CDOList getValueAsList(int i)
  {
    return (CDOList)getValue(i);
  }

  private void writeValues(CDODataOutput out, int referenceChunk) throws IOException
  {
    EClass owner = getEClass();
    EStructuralFeature[] features = classInfo.getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      Object value = getValue(i);
      if (value == null)
      {
        // Feature is NOT set
        out.writeByte(UNSET);
        continue;
      }

      // Feature IS set
      if (value == CDORevisionData.NIL)
      {
        // Feature IS null
        out.writeByte(SET_NULL);
        continue;
      }

      // Feature is NOT null
      out.writeByte(SET_NOT_NULL);
      if (feature.isMany())
      {
        CDOList list = (CDOList)value;
        out.writeCDOList(owner, feature, list, referenceChunk);
      }
      else
      {
        checkNoFeatureMap(feature);
        if (value != null && feature instanceof EReference)
        {
          value = out.getIDProvider().provideCDOID(value);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Writing feature {0}: {1}", feature.getName(), value);
        }

        out.writeCDOFeatureValue(feature, value);
      }
    }
  }

  private void readValues(CDODataInput in) throws IOException
  {
    EClass owner = getEClass();
    EStructuralFeature[] features = classInfo.getAllPersistentFeatures();
    initValues(features);
    for (int i = 0; i < features.length; i++)
    {
      Object value;
      EStructuralFeature feature = features[i];
      byte unsetState = in.readByte();
      switch (unsetState)
      {
      case UNSET:
        continue;

      case SET_NULL:
        setValue(i, CDORevisionData.NIL);
        continue;
      }

      if (feature.isMany())
      {
        value = in.readCDOList(owner, feature);
      }
      else
      {
        value = in.readCDOFeatureValue(feature);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read feature {0}: {1}", feature.getName(), value);
        }
      }

      setValue(i, value);
    }
  }

  public static void checkNoFeatureMap(EStructuralFeature feature)
  {
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      throw new UnsupportedOperationException("Single-valued feature maps not yet handled");
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
          throw new IllegalStateException(MessageFormat.format(Messages.getString("AbstractCDORevision.2"), oldID));
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

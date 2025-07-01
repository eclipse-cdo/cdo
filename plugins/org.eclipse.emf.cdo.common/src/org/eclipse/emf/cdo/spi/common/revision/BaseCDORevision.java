/*
 * Copyright (c) 2010-2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.cdo.common.security.NoPermissionException;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList.ConfigurableEquality;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PerfTracer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Map;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class BaseCDORevision extends AbstractCDORevision
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, BaseCDORevision.class);

  private static final PerfTracer READING = new PerfTracer(OM.PERF_REVISION_READING, BaseCDORevision.class);

  private static final PerfTracer WRITING = new PerfTracer(OM.PERF_REVISION_WRITING, BaseCDORevision.class);

  private static final int RESOURCE_NODE_NAME_INDEX = 1;

  private static final int RESOURCE_FOLDER_NODES_INDEX = 2;

  private static final byte UNSET_OPCODE = 0;

  private static final byte SET_NULL_OPCODE = 1;

  private static final byte SET_NOT_NULL_OPCODE = 2;

  private static final byte READ_PERMISSION_FLAG = 1 << 0; // 1

  private static final byte WRITE_PERMISSION_FLAG = 1 << 1; // 2

  private static final byte FROZEN_FLAG = 1 << 2; // 4

  private static final byte UNCHUNKED_FLAG = 1 << 3; // 8

  private static final byte BYPASS_PERMISSION_CHECKS_FLAG = 1 << 4; // 16

  private static final byte LIST_PRESERVING_FLAG = 1 << 5; // 32;

  private static final byte PERMISSION_MASK = READ_PERMISSION_FLAG | WRITE_PERMISSION_FLAG; // 3

  private static final Object[] EMPTY_ARRAY = {};

  private CDOID id;

  private CDOBranchPoint branchPoint;

  private int version;

  private long revised;

  private CDOID resourceID;

  /**
   * On a client, between a local modification and the commit the value of this <i>ID</i> can be an EObject.
   */
  private Object containerID;

  private int containingFeatureID;

  private transient byte flags;

  /**
   * @since 3.0
   */
  public BaseCDORevision(EClass eClass)
  {
    super(eClass);
    if (eClass != null)
    {
      version = UNSPECIFIED_VERSION;
      revised = UNSPECIFIED_DATE;
      resourceID = CDOID.NULL;
      containerID = CDOID.NULL;
      containingFeatureID = 0;
      initValues(getAllPersistentFeatures());
    }

    flags = CDOPermission.WRITE.getBits();
  }

  protected BaseCDORevision(BaseCDORevision source)
  {
    super(source.getEClass());
    id = source.id;
    branchPoint = source.branchPoint;
    version = source.version;
    revised = source.revised;
    resourceID = source.resourceID;
    containerID = source.containerID;
    containingFeatureID = source.containingFeatureID;
    flags = (byte)(source.flags & ~FROZEN_FLAG);
  }

  /**
   * @since 3.0
   */
  @Override
  public void read(CDODataInput in) throws IOException
  {
    if (READING.isEnabled())
    {
      READING.start(this);
    }

    readSystemValues(in);

    flags = in.readByte(); // Don't set permissions into this.falgs before readValues()
    flags |= UNCHUNKED_FLAG; // First assume all lists are unchunked; may be revised below
    flags |= BYPASS_PERMISSION_CHECKS_FLAG; // Temporarily disable permission checking to be able to set the read values

    if ((flags & PERMISSION_MASK) == CDOPermission.NONE.ordinal())
    {
      if (getClassInfo().isResourceNode())
      {
        clearValues();

        EClass eClass = getEClass();
        EStructuralFeature[] features = getAllPersistentFeatures();
        readValue(in, eClass, features[RESOURCE_NODE_NAME_INDEX], RESOURCE_NODE_NAME_INDEX, true);

        if (getClassInfo().isResourceFolder())
        {
          if (!readValue(in, eClass, features[RESOURCE_FOLDER_NODES_INDEX], RESOURCE_FOLDER_NODES_INDEX, true))
          {
            flags &= ~UNCHUNKED_FLAG;
          }
        }
      }
    }
    else
    {
      if (!readValues(in))
      {
        flags &= ~UNCHUNKED_FLAG;
      }
    }

    // Enable permission checking
    flags &= ~BYPASS_PERMISSION_CHECKS_FLAG;

    if (READING.isEnabled())
    {
      READING.stop(this);
    }
  }

  /**
   * @since 4.0
   */
  protected void readSystemValues(CDODataInput in) throws IOException
  {
    EClassifier classifier = in.readCDOClassifierRefAndResolve();
    initClassInfo((EClass)classifier);

    id = in.readCDOID();
    branchPoint = in.readCDOBranchPoint();
    version = in.readXInt();
    if (!id.isTemporary())
    {
      revised = in.readXLong();
    }

    resourceID = in.readCDOID();
    containerID = in.readCDOID();
    containingFeatureID = in.readXInt();

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading revision: ID={0}, className={1}, version={2}, branchPoint={3}, revised={4}, resource={5}, container={6}, featureID={7}", //$NON-NLS-1$
          id, getEClass().getName(), version, branchPoint, revised, resourceID, containerID, containingFeatureID);
    }
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean readValues(CDODataInput in) throws IOException
  {
    EClass owner = getEClass();
    EStructuralFeature[] features = getAllPersistentFeatures();
    initValues(features);

    boolean unchunked = true;
    for (int i = 0; i < features.length; i++)
    {
      unchunked = readValue(in, owner, features[i], i, unchunked);
    }

    return unchunked;
  }

  /**
   * @since 4.26
   */
  @Override
  public boolean readValue(CDODataInput in, EClass owner, EStructuralFeature feature, int i, boolean unchunked) throws IOException
  {
    Object value;
    byte unsetState = in.readByte();
    switch (unsetState)
    {
    case UNSET_OPCODE:
      return unchunked;

    case SET_NULL_OPCODE:
      setValue(i, CDORevisionData.NIL);
      return unchunked;
    }

    if (feature.isMany())
    {
      CDOList list = in.readCDOList(owner, feature);
      if (unchunked)
      {
        int size = list.size();
        if (size != 0)
        {
          Object lastElement = list.get(size - 1);
          if (lastElement == InternalCDOList.UNINITIALIZED || lastElement instanceof CDOElementProxy)
          {
            unchunked = false;
          }
        }
      }

      value = list;
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
    return unchunked;
  }

  /**
   * @since 4.0
   */
  @Override
  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    write(out, referenceChunk, null);
  }

  /**
   * @since 4.1
   */
  @Override
  public void write(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    if (WRITING.isEnabled())
    {
      WRITING.start(this);
    }

    writeSystemValues(out);

    CDOPermissionProvider permissionProvider = out.getPermissionProvider();
    CDOPermission permission = permissionProvider.getPermission(this, securityContext);
    out.writeByte(permission.getBits());

    if (permission == CDOPermission.NONE)
    {
      if (getClassInfo().isResourceNode())
      {
        EClass eClass = getEClass();
        EStructuralFeature[] features = getAllPersistentFeatures();
        writeValue(out, eClass, features[RESOURCE_NODE_NAME_INDEX], RESOURCE_NODE_NAME_INDEX, referenceChunk);

        if (getClassInfo().isResourceFolder())
        {
          writeValue(out, eClass, features[RESOURCE_FOLDER_NODES_INDEX], RESOURCE_FOLDER_NODES_INDEX, referenceChunk);
        }
      }
    }
    else
    {
      if (!isUnchunked() && referenceChunk != 0)
      {
        CDORevisionUnchunker unchunker = out.getRevisionUnchunker();
        if (unchunker != null)
        {
          unchunker.ensureChunks(this, referenceChunk);
        }
      }

      writeValues(out, referenceChunk);
    }

    if (WRITING.isEnabled())
    {
      WRITING.stop(this);
    }
  }

  /**
   * @since 4.0
   */
  protected void writeSystemValues(CDODataOutput out) throws IOException
  {
    EClass eClass = getEClass();
    CDOClassifierRef classRef = new CDOClassifierRef(eClass);

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision: ID={0}, className={1}, version={2}, branchPoint={3}, revised={4}, resource={5}, container={6}, featureID={7}", //$NON-NLS-1$
          id, eClass.getName(), getVersion(), branchPoint, revised, resourceID, containerID, containingFeatureID);
    }

    out.writeCDOClassifierRef(classRef);
    out.writeCDOID(id);
    out.writeCDOBranchPoint(branchPoint);
    out.writeXInt(getVersion());
    if (!id.isTemporary())
    {
      out.writeXLong(revised);
    }

    out.writeCDOID(resourceID);
    out.writeCDOID(out.getIDProvider().provideCDOID(containerID));
    out.writeXInt(containingFeatureID);
  }

  /**
   * @since 4.3
   */
  @Override
  public void writeValues(CDODataOutput out, int referenceChunk) throws IOException
  {
    EClass owner = getEClass();
    EStructuralFeature[] features = getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      writeValue(out, owner, features[i], i, referenceChunk);
    }
  }

  /**
   * @since 4.26
   */
  @Override
  public void writeValue(CDODataOutput out, EClass owner, EStructuralFeature feature, int i, int referenceChunk) throws IOException
  {
    Object value = getValue(i);
    if (value == null)
    {
      // Feature IS NOT SET
      out.writeByte(UNSET_OPCODE);
      return;
    }

    // Feature IS SET
    if (value == CDORevisionData.NIL)
    {
      // Feature IS NULL
      out.writeByte(SET_NULL_OPCODE);
      return;
    }

    // Feature IS NOT NULL
    out.writeByte(SET_NOT_NULL_OPCODE);
    if (feature.isMany())
    {
      CDOList list = (CDOList)value;
      out.writeCDOList(owner, feature, list, referenceChunk);
    }
    else
    {
      if (feature instanceof EReference)
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

  /**
   * @see #write(CDODataOutput, int)
   * @since 3.0
   */
  @Override
  public void convertEObjects(CDOIDProvider idProvider)
  {
    if (!(containerID instanceof CDOID))
    {
      containerID = idProvider.provideCDOID(containerID);
    }

    EStructuralFeature[] features = getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
      {
        CDOList list = getValueAsList(i);
        if (list != null)
        {
          for (int j = 0; j < list.size(); j++)
          {
            Object value = list.get(j, false);
            EStructuralFeature innerFeature = feature; // Prepare for possible feature map

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

  @Override
  public CDOID getID()
  {
    return id;
  }

  @Override
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
   * @since 4.2
   */
  @Override
  public InternalCDOBranch getBranch()
  {
    if (branchPoint == null)
    {
      return null;
    }

    return (InternalCDOBranch)branchPoint.getBranch();
  }

  /**
   * @since 3.0
   */
  @Override
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
  @Override
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting branchPoint {0}: {1}", this, branchPoint);
    }

    this.branchPoint = branchPoint;
  }

  @Override
  public int getVersion()
  {
    return version;
  }

  @Override
  public void setVersion(int version)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting version for {0}: v{1}", this, version);
    }

    this.version = version;
  }

  @Override
  public long getRevised()
  {
    return revised;
  }

  @Override
  public void setRevised(long revised)
  {
    long created = branchPoint.getTimeStamp();
    if (revised != UNSPECIFIED_DATE && revised < Math.max(0, created))
    {
      throw new IllegalArgumentException(
          "revision=" + this + ", created=" + CDOCommonUtil.formatTimeStamp(created) + ", revised=" + CDOCommonUtil.formatTimeStamp(revised));
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revised {0}: {1}", this, CDOCommonUtil.formatTimeStamp(revised));
    }

    this.revised = revised;
  }

  @Override
  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    return new CDORevisionDeltaImpl(origin, this);
  }

  @Override
  public void merge(CDORevisionDelta delta)
  {
    CDORevisionMerger applier = new CDORevisionMerger();
    applier.merge(this, delta);
  }

  @Override
  public CDOID getResourceID()
  {
    return resourceID;
  }

  @Override
  public void setResourceID(CDOID resourceID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resourceID {0}: {1}", this, resourceID);
    }

    this.resourceID = resourceID;
  }

  @Override
  public Object getContainerID()
  {
    return containerID;
  }

  @Override
  public void setContainerID(Object containerID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting containerID {0}: {1}", this, containerID);
    }

    this.containerID = containerID;
  }

  @Override
  public int getContainingFeatureID()
  {
    return containingFeatureID;
  }

  @Override
  public void setContainingFeatureID(int containingFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting containingFeatureID {0}: {1}", this, containingFeatureID);
    }

    this.containingFeatureID = containingFeatureID;
  }

  @Override
  public int hashCode(EStructuralFeature feature)
  {
    Object value = getValue(feature);
    if (value == null)
    {
      // See how AbstractEList.hashCode() returns 1 for an empty list.
      return 1;
    }

    return value.hashCode();
  }

  @Override
  public Object get(EStructuralFeature feature, int index)
  {
    if (feature.isMany())
    {
      CDOList list = getListOrNull(feature);
      if (list == null)
      {
        throw new CDOListImpl.IndexOutOfBoundsException(index, 0);
      }

      return list.get(index);
    }

    return getValue(feature);
  }

  @Override
  public boolean contains(EStructuralFeature feature, Object value)
  {
    CDOList list = getListOrNull(feature);
    return list != null && list.contains(value);
  }

  @Override
  public int indexOf(EStructuralFeature feature, Object value)
  {
    CDOList list = getListOrNull(feature);
    return list == null ? -1 : list.indexOf(value);
  }

  @Override
  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    CDOList list = getListOrNull(feature);
    return list == null ? -1 : list.lastIndexOf(value);
  }

  @Override
  public boolean isEmpty(EStructuralFeature feature)
  {
    CDOList list = getListOrNull(feature);
    return ObjectUtil.isEmpty(list);
  }

  @Override
  public int size(EStructuralFeature feature)
  {
    CDOList list = getListOrNull(feature);
    return list == null ? 0 : list.size();
  }

  @Override
  public Object[] toArray(EStructuralFeature feature)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    CDOList list = getListOrNull(feature);
    return list == null ? EMPTY_ARRAY : list.toArray();
  }

  @Override
  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    if (!feature.isMany())
    {
      throw new IllegalStateException("!feature.isMany()");
    }

    CDOList list = getListOrNull(feature);
    if (list == null)
    {
      if (array.length != 0)
      {
        @SuppressWarnings("unchecked")
        T[] emptyArray = (T[])Array.newInstance(array.getClass().getComponentType(), 0);
        return emptyArray;
      }

      return array;
    }

    return list.toArray(array);
  }

  @Override
  public void add(EStructuralFeature feature, int index, Object value)
  {
    CDOList list = getOrCreateList(feature);
    list.add(index, value);
  }

  @Override
  public void clear(EStructuralFeature feature)
  {
    if (feature.isMany() && feature.isUnsettable())
    {
      getOrCreateList(feature).clear();
    }
    else
    {
      setValue(feature, null);
    }
  }

  @Override
  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    CDOList list = getListOrNull(feature);
    if (list == null)
    {
      throw new CDOListImpl.IndexOutOfBoundsException(sourceIndex, 0);
    }

    return list.move(targetIndex, sourceIndex);
  }

  @Override
  public Object remove(EStructuralFeature feature, int index)
  {
    CDOList list = getListOrNull(feature);
    if (list == null)
    {
      throw new CDOListImpl.IndexOutOfBoundsException(index, 0);
    }

    return list.remove(index);
  }

  @Override
  public Object set(EStructuralFeature feature, int index, Object value)
  {
    if (feature.isMany())
    {
      CDOList list = getOrCreateList(feature);
      return list.set(index, value);
    }

    return setValue(feature, value);
  }

  @Override
  public void unset(EStructuralFeature feature)
  {
    // if (feature.isMany() && isListPreserving())
    // {
    // // isListPreserving() is always false except for HibernateStoreAccessor.
    // // For performance reasons this Hibernate-specific feature should probably be implemented via a custom revision
    // factory.
    // getOrCreateList(feature).clear();
    // }
    // else
    {
      setValue(feature, null);
    }
  }

  /**
   * @since 4.0
   */
  @Override
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adjusting references for revision {0}", this);
    }

    boolean changed = false;

    CDOID id1 = (CDOID)referenceAdjuster.adjustReference(resourceID, CDOContainerFeatureDelta.CONTAINER_FEATURE, CDOFeatureDelta.NO_INDEX);
    if (id1 != resourceID)
    {
      resourceID = id1;
      changed = true;
    }

    Object id2 = referenceAdjuster.adjustReference(containerID, CDOContainerFeatureDelta.CONTAINER_FEATURE, CDOFeatureDelta.NO_INDEX);
    if (id2 != containerID)
    {
      containerID = id2;
      changed = true;
    }

    EStructuralFeature[] features = getAllPersistentFeatures();
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature instanceof EReference)
      {
        if (feature.isMany())
        {
          InternalCDOList list = (InternalCDOList)getValueAsList(i);
          if (list != null)
          {
            changed |= list.adjustReferences(referenceAdjuster, feature);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature);
          Object oldValue = getValue(i);
          Object newValue = type.adjustReferences(referenceAdjuster, oldValue, feature, CDOFeatureDelta.NO_INDEX);
          if (oldValue != newValue) // Just an optimization for NOOP adjusters
          {
            setValue(i, newValue);
            changed = true;
          }
        }
      }
    }

    return changed;
  }

  /**
   * @since 4.3
   */
  @Override
  public void adjustBranches(CDOBranchManager newBranchManager)
  {
    if (branchPoint != null)
    {
      CDOBranch branch = branchPoint.getBranch();
      if (branch != null)
      {
        branch = newBranchManager.getBranch(branch.getID());
        branchPoint = branch.getPoint(branchPoint.getTimeStamp());
      }
    }
  }

  @Override
  public Object getValue(EStructuralFeature feature)
  {
    checkReadable(feature);

    int featureIndex = getFeatureIndex(feature);
    return getValue(featureIndex);
  }

  @Override
  public Object setValue(EStructuralFeature feature, Object value)
  {
    int featureIndex = getFeatureIndex(feature);

    try
    {
      Object old = getValue(featureIndex);
      setValue(featureIndex, value);
      return old;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("AbstractCDORevision.20"), feature, getClassInfo()), ex);
    }
  }

  /**
   * @since 4.7
   */
  @Override
  public CDOList getListOrNull(EStructuralFeature feature)
  {
    return getOrCreateList(feature, DO_NOT_CREATE_LIST);
  }

  @Override
  public CDOList getOrCreateList(EStructuralFeature feature)
  {
    return getOrCreateList(feature, 0);
  }

  @Override
  public CDOList getOrCreateList(EStructuralFeature feature, int size)
  {
    checkReadable(feature);

    int featureIndex = getFeatureIndex(feature);
    InternalCDOList list = (InternalCDOList)getValue(featureIndex);
    if (list == null)
    {
      if (size == DO_NOT_CREATE_LIST)
      {
        return null;
      }

      list = (InternalCDOList)CDOListFactory.DEFAULT.createList(size, 0, 0);
      if (feature instanceof EReference && list instanceof ConfigurableEquality)
      {
        ((ConfigurableEquality)list).setUseEquals(false);
      }

      synchronized (this)
      {
        boolean bypassPermissionChecks = bypassPermissionChecks(true);

        try
        {
          setValue(featureIndex, list);
        }
        finally
        {
          bypassPermissionChecks(bypassPermissionChecks);
        }
      }
    }

    return list;
  }

  @Override
  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    int featureIndex = getFeatureIndex(feature);
    setValue(featureIndex, list);
  }

  /**
   * @since 4.2
   */
  @Override
  public EStructuralFeature[] clearValues()
  {
    EStructuralFeature[] features = getClassInfo().getAllPersistentFeatures();
    initValues(features);
    return features;
  }

  /**
   * @since 4.3
   */
  @Override
  public String getResourceNodeName()
  {
    return (String)doGetValue(RESOURCE_NODE_NAME_INDEX);
  }

  /**
   * @since 4.1
   */
  @Override
  public CDOPermission getPermission()
  {
    return CDOPermission.get(flags & PERMISSION_MASK);
  }

  /**
   * @since 4.1
   */
  @Override
  public void setPermission(CDOPermission permission)
  {
    flags = (byte)(flags & ~PERMISSION_MASK | permission.getBits() & PERMISSION_MASK);

    if (permission == CDOPermission.NONE)
    {
      EStructuralFeature[] features = getClassInfo().getAllPersistentFeatures();

      CDOClassInfo classInfo = getClassInfo();
      if (classInfo.isResourceNode())
      {
        for (EStructuralFeature feature : features)
        {
          if (!CDOModelUtil.isResourcePathFeature(feature))
          {
            setValue(feature, null);
          }
        }
      }
      else
      {
        initValues(features);
      }
    }
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean bypassPermissionChecks(boolean on)
  {
    boolean old = (flags & BYPASS_PERMISSION_CHECKS_FLAG) != 0;

    if (on)
    {
      flags |= BYPASS_PERMISSION_CHECKS_FLAG;
    }
    else
    {
      flags &= ~BYPASS_PERMISSION_CHECKS_FLAG;
    }

    return old;
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean isListPreserving()
  {
    return (flags & LIST_PRESERVING_FLAG) != 0;
  }

  /**
   * @since 4.3
   */
  @Override
  public void setListPreserving()
  {
    flags |= LIST_PRESERVING_FLAG;
  }

  /**
   * @since 4.1
   */
  @Override
  public void freeze()
  {
    flags |= FROZEN_FLAG;

    if (isReadable())
    {
      EStructuralFeature[] features = getAllPersistentFeatures();
      for (int i = 0; i < features.length; i++)
      {
        EStructuralFeature feature = features[i];
        if (feature.isMany())
        {
          InternalCDOList list = (InternalCDOList)doGetValue(i);
          if (list != null)
          {
            list.freeze();
          }
        }
      }
    }
  }

  /**
   * @since 4.11
   */
  public void unfreeze()
  {
    flags &= ~FROZEN_FLAG;

    if (isReadable())
    {
      EStructuralFeature[] features = getAllPersistentFeatures();
      for (int i = 0; i < features.length; i++)
      {
        EStructuralFeature feature = features[i];
        if (feature.isMany())
        {
          Object value = doGetValue(i);
          if (value instanceof CDOListImpl)
          {
            CDOListImpl list = (CDOListImpl)value;
            list.unfreeze();
          }
        }
      }
    }
  }

  /**
   * @since 4.2
   */
  @Override
  public boolean isFrozen()
  {
    return (flags & FROZEN_FLAG) != 0;
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isUnchunked()
  {
    return (flags & UNCHUNKED_FLAG) != 0;
  }

  /**
   * @since 4.1
   */
  @Override
  public void setUnchunked()
  {
    flags |= UNCHUNKED_FLAG;
  }

  protected Object getValue(int featureIndex)
  {
    return doGetValue(featureIndex);
  }

  protected void setValue(int featureIndex, Object value)
  {
    checkUnfrozen(featureIndex, value);
    checkWritable();
    doSetValue(featureIndex, value);
  }

  protected abstract void initValues(EStructuralFeature[] allPersistentFeatures);

  /**
   * @since 4.1
   */
  protected abstract Object doGetValue(int featureIndex);

  /**
   * @since 4.1
   */
  protected abstract void doSetValue(int featureIndex, Object value);

  private CDOList getValueAsList(int i)
  {
    return (CDOList)getValue(i);
  }

  private void checkUnfrozen(int featureIndex, Object value)
  {
    if ((flags & FROZEN_FLAG) != 0)
    {
      // Exception 1: LoadPermissionsRequest needs to "reload" revision values in case the original permission was NONE.
      // In this case BYPASS_PERMISSION_CHECKS_FLAG is set.
      if ((flags & BYPASS_PERMISSION_CHECKS_FLAG) != 0)
      {
        return;
      }

      Object oldValue = getValue(featureIndex);

      // Exception 2: Setting an empty list as the value for an isMany feature, is allowed if the old value is null.
      // This is a case of lazy initialization.
      boolean newIsEmptyList = value instanceof EList<?> && ((EList<?>)value).size() == 0;
      if (newIsEmptyList && oldValue == null)
      {
        return;
      }

      // Exception 3a: Replacing a temp ID with a regular ID is allowed (happens during postCommit of new objects)
      // Exception 3b: Replacing a temp ID with another temp ID is also allowed (happens when changes are imported in a
      // PushTx).
      if (oldValue instanceof CDOIDTemp && value instanceof CDOID)
      {
        return;
      }

      throw new IllegalStateException("Cannot modify a frozen revision");
    }
  }

  private void checkReadable(EStructuralFeature feature)
  {
    if ((flags & BYPASS_PERMISSION_CHECKS_FLAG) != 0)
    {
      return;
    }

    if (CDOModelUtil.isResourcePathFeature(feature))
    {
      return;
    }

    if ((flags & READ_PERMISSION_FLAG) == 0)
    {
      throw new NoPermissionException(this, false);
    }
  }

  private void checkWritable()
  {
    if ((flags & BYPASS_PERMISSION_CHECKS_FLAG) != 0)
    {
      return;
    }

    if ((flags & WRITE_PERMISSION_FLAG) == 0)
    {
      throw new NoPermissionException(this, true);
    }
  }

  /**
   * @deprecated As of 4.5 {@link org.eclipse.emf.ecore.util.FeatureMap feature maps} are no longer supported.
   */
  @Deprecated
  public static void checkNoFeatureMap(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public static Object remapID(Object value, Map<CDOID, CDOID> idMappings, boolean allowUnmappedTempIDs)
  {
    if (value instanceof CDOID)
    {
      CDOID oldID = (CDOID)value;
      if (!oldID.isNull())
      {
        CDOID newID = idMappings.get(oldID);
        if (newID != null)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Adjusting ID: {0} --> {1}", oldID, newID);
          }

          return newID;
        }

        if (oldID instanceof CDOIDTemp)
        {
          throw new IllegalStateException(MessageFormat.format(Messages.getString("AbstractCDORevision.2"), oldID));
        }
      }
    }

    return value;
  }

  /**
   * @since 4.3
   */
  public static String formatFlags(BaseCDORevision revision)
  {
    int flags = revision.flags;

    StringBuilder builder = new StringBuilder();
    if ((flags & UNCHUNKED_FLAG) != 0)
    {
      builder.append("UNCHUNKED");
    }

    if ((flags & FROZEN_FLAG) != 0)
    {
      StringUtil.appendSeparator(builder, '|');
      builder.append("FROZEN");
    }

    if ((flags & READ_PERMISSION_FLAG) != 0)
    {
      StringUtil.appendSeparator(builder, '|');
      builder.append("READ");
    }

    if ((flags & WRITE_PERMISSION_FLAG) != 0)
    {
      StringUtil.appendSeparator(builder, '|');
      builder.append("WRITE");
    }

    if ((flags & BYPASS_PERMISSION_CHECKS_FLAG) != 0)
    {
      StringUtil.appendSeparator(builder, '|');
      builder.append("BYPASS_PERMISSION_CHECKS");
    }

    return builder.toString();
  }
}

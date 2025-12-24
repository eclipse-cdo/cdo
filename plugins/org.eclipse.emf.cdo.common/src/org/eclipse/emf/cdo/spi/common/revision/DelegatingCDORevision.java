/*
 * Copyright (c) 2010-2014, 2018, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - bug 341081
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionValueVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class DelegatingCDORevision implements InternalCDORevision
{
  public DelegatingCDORevision()
  {
  }

  public abstract InternalCDORevision getDelegate();

  /**
   * @since 4.2
   */
  @Override
  public InternalCDORevision getRevisionForID(CDOID id)
  {
    return getDelegate().getRevisionForID(id);
  }

  /**
   * @since 4.2
   */
  @Override
  public InternalCDORevision getProperRevision()
  {
    return getDelegate().getProperRevision();
  }

  /**
   * @since 4.0
   */
  @Override
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    return getDelegate().adjustReferences(referenceAdjuster);
  }

  /**
   * @since 4.3
   */
  @Override
  public void adjustBranches(CDOBranchManager newBranchManager)
  {
    getDelegate().adjustBranches(newBranchManager);
  }

  @Override
  public long getTimeStamp()
  {
    return getDelegate().getTimeStamp();
  }

  /**
   * @since 4.2
   */
  @Override
  public InternalCDOBranch getBranch()
  {
    return getDelegate().getBranch();
  }

  @Override
  public boolean isHistorical()
  {
    return getDelegate().isHistorical();
  }

  @Override
  public CDOID getID()
  {
    return getDelegate().getID();
  }

  @Override
  public CDORevision revision()
  {
    return getDelegate().revision();
  }

  @Override
  public CDOID getResourceID()
  {
    return getDelegate().getResourceID();
  }

  @Override
  public Object getContainerID()
  {
    return getDelegate().getContainerID();
  }

  /**
   * @since 4.27
   */
  @Override
  public int getContainerFeatureID()
  {
    return getDelegate().getContainerFeatureID();
  }

  @Override
  public Object get(EStructuralFeature feature, int index)
  {
    return getDelegate().get(feature, index);
  }

  @Override
  public EClass getEClass()
  {
    return getDelegate().getEClass();
  }

  @Override
  public int getVersion()
  {
    return getDelegate().getVersion();
  }

  @Override
  public int size(EStructuralFeature feature)
  {
    return getDelegate().size(feature);
  }

  @Override
  public long getRevised()
  {
    return getDelegate().getRevised();
  }

  @Override
  public boolean isEmpty(EStructuralFeature feature)
  {
    return getDelegate().isEmpty(feature);
  }

  @Override
  public boolean isValid(long timeStamp)
  {
    return getDelegate().isValid(timeStamp);
  }

  /**
   * @since 4.0
   */
  @Override
  public boolean isValid(CDOBranchPoint branchPoint)
  {
    return getDelegate().isValid(branchPoint);
  }

  /**
   * @since 4.0
   */
  @Override
  public InternalCDORevision copy()
  {
    return null;
  }

  /**
   * @since 4.2
   */
  @Override
  public InternalCDOClassInfo getClassInfo()
  {
    return getDelegate().getClassInfo();
  }

  @Override
  public void setID(CDOID id)
  {
    getDelegate().setID(id);
  }

  @Override
  public boolean contains(EStructuralFeature feature, Object value)
  {
    return getDelegate().contains(feature, value);
  }

  @Override
  public boolean isResourceNode()
  {
    return getDelegate().isResourceNode();
  }

  @Override
  public void setVersion(int version)
  {
    getDelegate().setVersion(version);
  }

  @Override
  public boolean isResourceFolder()
  {
    return getDelegate().isResourceFolder();
  }

  @Override
  public int indexOf(EStructuralFeature feature, Object value)
  {
    return getDelegate().indexOf(feature, value);
  }

  @Override
  public boolean isResource()
  {
    return getDelegate().isResource();
  }

  @Override
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    getDelegate().setBranchPoint(branchPoint);
  }

  @Override
  public void adjustForCommit(CDOBranch branch, long timeStamp)
  {
    getDelegate().adjustForCommit(branch, timeStamp);
  }

  @Override
  public CDORevisionData data()
  {
    return getDelegate().data();
  }

  @Override
  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    return getDelegate().lastIndexOf(feature, value);
  }

  @Override
  public void setRevised(long revised)
  {
    getDelegate().setRevised(revised);
  }

  @Override
  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    return getDelegate().compare(origin);
  }

  @Override
  public void setResourceID(CDOID resourceID)
  {
    getDelegate().setResourceID(resourceID);
  }

  @Override
  public void merge(CDORevisionDelta delta)
  {
    getDelegate().merge(delta);
  }

  @Override
  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    return getDelegate().toArray(feature, array);
  }

  @Override
  public void setContainerID(Object containerID)
  {
    getDelegate().setContainerID(containerID);
  }

  @Override
  public void setContainerFeatureID(int containingFeatureID)
  {
    getDelegate().setContainerFeatureID(containingFeatureID);
  }

  @Override
  public Object[] toArray(EStructuralFeature feature)
  {
    return getDelegate().toArray(feature);
  }

  @Override
  public void add(EStructuralFeature feature, int index, Object value)
  {
    getDelegate().add(feature, index, value);
  }

  @Override
  public int hashCode(EStructuralFeature feature)
  {
    return getDelegate().hashCode(feature);
  }

  @Override
  public void clear(EStructuralFeature feature)
  {
    getDelegate().clear(feature);
  }

  @Override
  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    return getDelegate().move(feature, targetIndex, sourceIndex);
  }

  @Override
  public Object remove(EStructuralFeature feature, int index)
  {
    return getDelegate().remove(feature, index);
  }

  @Override
  public Object set(EStructuralFeature feature, int index, Object value)
  {
    return getDelegate().set(feature, index, value);
  }

  @Override
  public void unset(EStructuralFeature feature)
  {
    getDelegate().unset(feature);
  }

  @Override
  public Object getValue(EStructuralFeature feature)
  {
    return getDelegate().getValue(feature);
  }

  @Override
  public Object setValue(EStructuralFeature feature, Object value)
  {
    return getDelegate().setValue(feature, value);
  }

  @Override
  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    getDelegate().setList(feature, list);
  }

  @Override
  public CDOList getListOrNull(EStructuralFeature feature)
  {
    return getDelegate().getListOrNull(feature);
  }

  @Override
  public CDOList getOrCreateList(EStructuralFeature feature)
  {
    return getDelegate().getOrCreateList(feature);
  }

  @Override
  public CDOList getOrCreateList(EStructuralFeature feature, int size)
  {
    return getDelegate().getOrCreateList(feature, size);
  }

  @Override
  public void read(CDODataInput in) throws IOException
  {
    getDelegate().read(in);
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean readValues(CDODataInput in) throws IOException
  {
    return getDelegate().readValues(in);
  }

  @Override
  public boolean readValue(CDODataInput in, EClass owner, EStructuralFeature feature, int i, boolean unchunked) throws IOException
  {
    return getDelegate().readValue(in, owner, feature, i, unchunked);
  }

  @Override
  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    getDelegate().write(out, referenceChunk);
  }

  /**
   * @since 4.1
   */
  @Override
  public void write(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    getDelegate().write(out, referenceChunk, securityContext);
  }

  /**
   * @since 4.3
   */
  @Override
  public void writeValues(CDODataOutput out, int referenceChunk) throws IOException
  {
    getDelegate().writeValues(out, referenceChunk);
  }

  @Override
  public void writeValue(CDODataOutput out, EClass owner, EStructuralFeature feature, int i, int referenceChunk) throws IOException
  {
    getDelegate().writeValue(out, owner, feature, i, referenceChunk);
  }

  @Override
  public void convertEObjects(CDOIDProvider oidProvider)
  {
    getDelegate().convertEObjects(oidProvider);
  }

  /**
   * @since 4.2
   */
  @Override
  public EStructuralFeature[] clearValues()
  {
    return getDelegate().clearValues();
  }

  /**
   * @since 4.3
   */
  @Override
  public String getResourceNodeName()
  {
    return getDelegate().getResourceNodeName();
  }

  /**
   * @since 4.1
   */
  @Override
  public CDOPermission getPermission()
  {
    return getDelegate().getPermission();
  }

  /**
   * @since 4.1
   */
  @Override
  public void setPermission(CDOPermission permission)
  {
    getDelegate().setPermission(permission);
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isReadable()
  {
    return getDelegate().isReadable();
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isWritable()
  {
    return getDelegate().isWritable();
  }

  /**
   * @since 4.2
   */
  @Override
  public void accept(CDORevisionValueVisitor visitor)
  {
    getDelegate().accept(visitor);
  }

  /**
   * @since 4.2
   */
  @Override
  public void accept(CDORevisionValueVisitor visitor, java.util.function.Predicate<EStructuralFeature> filter)
  {
    getDelegate().accept(visitor, filter);
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean bypassPermissionChecks(boolean on)
  {
    return getDelegate().bypassPermissionChecks(on);
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean isListPreserving()
  {
    return getDelegate().isListPreserving();
  }

  /**
   * @since 4.3
   */
  @Override
  public void setListPreserving()
  {
    getDelegate().setListPreserving();
  }

  /**
   * @since 4.0
   */
  @Override
  public void freeze()
  {
    getDelegate().freeze();
  }

  /**
   * @since 4.2
   */
  @Override
  public boolean isFrozen()
  {
    return getDelegate().isFrozen();
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isUnchunked()
  {
    return getDelegate().isUnchunked();
  }

  /**
   * @since 4.1
   */
  @Override
  public void setUnchunked()
  {
    getDelegate().setUnchunked();
  }

  @Override
  @Deprecated
  public int getContainingFeatureID()
  {
    return getContainerFeatureID();
  }

  @Override
  @Deprecated
  public void setContainingFeatureID(int containerFeatureID)
  {
    setContainerFeatureID(containerFeatureID);
  }

  @Override
  @Deprecated
  public CDOList getList(EStructuralFeature feature)
  {
    return getDelegate().getList(feature);
  }

  @Override
  @Deprecated
  public CDOList getList(EStructuralFeature feature, int initialCapacity)
  {
    return getDelegate().getList(feature, initialCapacity);
  }

  /**
   * @since 4.2
   */
  @Override
  @Deprecated
  public void accept(CDORevisionValueVisitor visitor, org.eclipse.net4j.util.Predicate<EStructuralFeature> filter)
  {
    getDelegate().accept(visitor, filter);
  }

  /**
   *
   * @author Eike Stepper
   */
  @SuppressWarnings("unused")
  private static final class InternalCompletenessChecker extends DelegatingCDORevision
  {
    @Override
    public InternalCDORevision getDelegate()
    {
      return null;
    }
  }
}

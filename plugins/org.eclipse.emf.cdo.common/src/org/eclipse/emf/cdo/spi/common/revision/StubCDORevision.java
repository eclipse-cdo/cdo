/*
 * Copyright (c) 2010-2015, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
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
public class StubCDORevision extends AbstractCDORevision
{
  public StubCDORevision(EClass eClass)
  {
    super(eClass);
  }

  /**
   * @since 4.2
   */
  protected StubCDORevision(InternalCDOClassInfo classInfo)
  {
    super(classInfo);
  }

  public int compareTo(CDOBranchPoint o)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setID(CDOID id)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setVersion(int version)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setRevised(long revised)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setResourceID(CDOID resourceID)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setContainerID(Object containerID)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setContainingFeatureID(int containingFeatureID)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void add(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void clear(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object remove(EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object set(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void unset(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object getValue(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object setValue(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public CDOList getListOrNull(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public CDOList getOrCreateList(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public CDOList getOrCreateList(EStructuralFeature feature, int size)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void read(CDODataInput in) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean readValues(CDODataInput in) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.26
   */
  @Override
  public boolean readValue(CDODataInput in, EClass owner, EStructuralFeature feature, int i, boolean unchunked) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.1
   */
  @Override
  public void write(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.3
   */
  @Override
  public void writeValues(CDODataOutput out, int referenceChunk) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.26
   */
  @Override
  public void writeValue(CDODataOutput out, EClass owner, EStructuralFeature feature, int i, int referenceChunk) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void convertEObjects(CDOIDProvider oidProvider)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public int getVersion()
  {
    return 0;
  }

  @Override
  public long getRevised()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public void merge(CDORevisionDelta delta)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public InternalCDORevision copy()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public CDOID getID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.2
   */
  @Override
  public InternalCDOBranch getBranch()
  {
    return null;
  }

  @Override
  public long getTimeStamp()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public CDOID getResourceID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object getContainerID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public int getContainingFeatureID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object get(EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public int size(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public boolean isEmpty(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public boolean contains(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public int indexOf(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public Object[] toArray(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  @Override
  public int hashCode(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.0
   */
  @Override
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.3
   */
  @Override
  public void adjustBranches(CDOBranchManager newBranchManager)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.2
   */
  @Override
  public EStructuralFeature[] clearValues()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.3
   */
  @Override
  public String getResourceNodeName()
  {
    return "?";
  }

  /**
   * @since 4.1
   */
  @Override
  public CDOPermission getPermission()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.1
   */
  @Override
  public void setPermission(CDOPermission permission)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean bypassPermissionChecks(boolean on)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.3
   */
  @Override
  public boolean isListPreserving()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.3
   */
  @Override
  public void setListPreserving()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.0
   */
  @Override
  public void freeze()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.2
   */
  @Override
  public boolean isFrozen()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isUnchunked()
  {
    return true;
  }

  /**
   * @since 4.1
   */
  @Override
  public void setUnchunked()
  {
    // Do nothing
  }

  private String getExceptionMessage()
  {
    return "Unsupported operation in " + getClass().getSimpleName();
  }
}

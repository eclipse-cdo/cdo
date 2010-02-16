/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class StubCDORevision extends AbstractCDORevision
{
  public StubCDORevision()
  {
  }

  public int compareTo(CDOBranchPoint o)
  {
    throw new UnsupportedOperationException();
  }

  public CDOClassInfo getClassInfo()
  {
    throw new UnsupportedOperationException();
  }

  public void setID(CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  public void setVersion(int version)
  {
    throw new UnsupportedOperationException();
  }

  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException();
  }

  public void setRevised(long revised)
  {
    throw new UnsupportedOperationException();
  }

  public void setResourceID(CDOID resourceID)
  {
    throw new UnsupportedOperationException();
  }

  public void setContainerID(Object containerID)
  {
    throw new UnsupportedOperationException();
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    throw new UnsupportedOperationException();
  }

  public void add(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public void clear(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    throw new UnsupportedOperationException();
  }

  public Object remove(EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException();
  }

  public Object set(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public void unset(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public Object getValue(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public Object setValue(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    throw new UnsupportedOperationException();
  }

  public CDOList getList(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public CDOList getList(EStructuralFeature feature, int size)
  {
    throw new UnsupportedOperationException();
  }

  public void read(CDODataInput in) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void convertEObjects(CDOIDProvider oidProvider)
  {
    throw new UnsupportedOperationException();
  }

  public int getVersion()
  {
    throw new UnsupportedOperationException();
  }

  public long getRevised()
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    throw new UnsupportedOperationException();
  }

  public void merge(CDORevisionDelta delta)
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDORevision copy()
  {
    throw new UnsupportedOperationException();
  }

  public CDOID getID()
  {
    throw new UnsupportedOperationException();
  }

  public CDOBranch getBranch()
  {
    throw new UnsupportedOperationException();
  }

  public long getTimeStamp()
  {
    throw new UnsupportedOperationException();
  }

  public CDOID getResourceID()
  {
    throw new UnsupportedOperationException();
  }

  public Object getContainerID()
  {
    throw new UnsupportedOperationException();
  }

  public int getContainingFeatureID()
  {
    throw new UnsupportedOperationException();
  }

  public Object get(EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException();
  }

  public int size(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isEmpty(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public boolean contains(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public int indexOf(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public int hashCode(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public void adjustReferences(CDOReferenceAdjuster revisionAdjuster)
  {
    throw new UnsupportedOperationException();
  }
}

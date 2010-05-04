package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class TestRevision implements InternalCDORevision
{
  private CDOID id;

  private int version;

  private long created;

  private long revised;

  public TestRevision(long id, int version, long created, long revised)
  {
    this.id = CDOIDUtil.createLong(id);
    this.version = version;
    this.created = created;
    this.revised = revised;
  }

  public TestRevision(long id, int version, long created)
  {
    this(id, version, created, CDORevision.UNSPECIFIED_DATE);
  }

  public TestRevision(long id)
  {
    this(id, 0, CDORevision.UNSPECIFIED_DATE);
  }

  public CDOID getID()
  {
    return id;
  }

  public void setID(CDOID id)
  {
    this.id = id;
  }

  public int getVersion()
  {
    return version;
  }

  public void setVersion(int version)
  {
    this.version = version;
  }

  public long getCreated()
  {
    return created;
  }

  public void setCreated(long created)
  {
    this.created = created;
  }

  public long getRevised()
  {
    return revised;
  }

  public void setRevised(long revised)
  {
    this.revised = revised;
  }

  public boolean isCurrent()
  {
    return revised == UNSPECIFIED_DATE;
  }

  public boolean isTransactional()
  {
    return version < 0;
  }

  public boolean isValid(long timeStamp)
  {
    return (revised == UNSPECIFIED_DATE || revised >= timeStamp) && timeStamp >= created;
  }

  public EClass getEClass()
  {
    throw new UnsupportedOperationException();
  }

  public CDORevisionData data()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isResourceNode()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isResourceFolder()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isResource()
  {
    throw new UnsupportedOperationException();
  }

  public CDORevisionDelta compare(CDORevision origin)
  {
    throw new UnsupportedOperationException();
  }

  public void merge(CDORevisionDelta delta)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision copy()
  {
    return new TestRevision(CDOIDUtil.getLong(id), version, created, revised);
  }

  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void add(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public void adjustReferences(CDOReferenceAdjuster revisionAdjuster)
  {
    throw new UnsupportedOperationException();
  }

  public void clear(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public boolean contains(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public Object get(EStructuralFeature feature, int index)
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

  public CDOList getList(EStructuralFeature feature, int size)
  {
    throw new UnsupportedOperationException();
  }

  public CDOList getList(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public CDOID getResourceID()
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision revision()
  {
    throw new UnsupportedOperationException();
  }

  public Object getValue(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public int hashCode(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public int indexOf(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isEmpty(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public int lastIndexOf(EStructuralFeature feature, Object value)
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

  public void setContainerID(Object containerID)
  {
    throw new UnsupportedOperationException();
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    throw new UnsupportedOperationException();
  }

  public void setListSize(EStructuralFeature feature, int size)
  {
    throw new UnsupportedOperationException();
  }

  public void setResourceID(CDOID resourceID)
  {
    throw new UnsupportedOperationException();
  }

  public int setTransactional()
  {
    throw new UnsupportedOperationException();
  }

  public void setUntransactional()
  {
    throw new UnsupportedOperationException();
  }

  public Object setValue(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException();
  }

  public int size(EStructuralFeature feature)
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

  public void unset(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    throw new UnsupportedOperationException();
  }

  public Object basicGet(EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException();
  }

  public Object basicSet(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException();
  }
}

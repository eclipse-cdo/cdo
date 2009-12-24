/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public class CDORevisionProxy implements HibernateProxy, InternalCDORevision, Serializable
{
  private static final long serialVersionUID = 1L;

  private CDORevisionLazyInitializer li;

  CDORevisionProxy(CDORevisionLazyInitializer li)
  {
    this.li = li;
  }

  public CDORevision copy()
  {
    return new CDORevisionProxy(li);
  }

  public void read(CDODataInput in) throws IOException
  {
    li.getRevision().read(in);
  }

  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    li.getRevision().write(out, referenceChunk);
  }

  public void convertEObjects(CDOIDProvider idProvider)
  {
    li.getRevision().convertEObjects(idProvider);
  }

  public Object writeReplace()
  {
    return this;
  }

  public LazyInitializer getHibernateLazyInitializer()
  {
    return li;
  }

  public CDOClassInfo getClassInfo()
  {
    return li.getRevision().getClassInfo();
  }

  public EClass getEClass()
  {
    return li.getRevision().getEClass();
  }

  public Object getContainerID()
  {
    return li.getRevision().getContainerID();
  }

  public void setContainerID(Object containerID)
  {
    li.getRevision().setContainerID(containerID);
  }

  public int getContainingFeatureID()
  {
    return li.getRevision().getContainingFeatureID();
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    li.getRevision().setContainingFeatureID(containingFeatureID);
  }

  public CDORevisionData data()
  {
    return li.getRevision().data();
  }

  public CDOID getID()
  {
    return li.getRevision().getID();
  }

  public void setID(CDOID id)
  {
    li.getRevision().setID(id);
  }

  public int getVersion()
  {
    return li.getRevision().getVersion();
  }

  public void setVersion(int version)
  {
    li.getRevision().setVersion(version);
  }

  public boolean isTransactional()
  {
    return li.getRevision().isTransactional();
  }

  public int setTransactional(boolean on)
  {
    return li.getRevision().setTransactional(on);
  }

  public CDOID getResourceID()
  {
    return li.getRevision().getResourceID();
  }

  public void setResourceID(CDOID resourceID)
  {
    li.getRevision().setResourceID(resourceID);
  }

  public long getCreated()
  {
    return li.getRevision().getCreated();
  }

  public void setCreated(long created)
  {
    li.getRevision().setCreated(created);
  }

  public long getRevised()
  {
    return li.getRevision().getRevised();
  }

  public void setRevised(long revised)
  {
    li.getRevision().setRevised(revised);
  }

  public boolean isCurrent()
  {
    return li.getRevision().isCurrent();
  }

  public boolean isValid(long timeStamp)
  {
    return li.getRevision().isValid(timeStamp);
  }

  public CDORevision revision()
  {
    return li.getRevision().revision();
  }

  public boolean isResourceNode()
  {
    return li.getRevision().isResourceNode();
  }

  public boolean isResourceFolder()
  {
    return li.getRevision().isResourceFolder();
  }

  public boolean isResource()
  {
    return li.getRevision().isResource();
  }

  public void add(EStructuralFeature feature, int index, Object value)
  {
    li.getRevision().add(feature, index, value);
  }

  public void adjustReferences(CDOReferenceAdjuster idMappings)
  {
    li.getRevision().adjustReferences(idMappings);
  }

  public void clear(EStructuralFeature feature)
  {
    li.getRevision().clear(feature);
  }

  public CDORevisionDelta compare(CDORevision origin)
  {
    return li.getRevision().compare(origin);
  }

  public boolean contains(EStructuralFeature feature, Object value)
  {
    return li.getRevision().contains(feature, value);
  }

  public Object get(EStructuralFeature feature, int index)
  {
    return li.getRevision().get(feature, index);
  }

  public Object getValue(EStructuralFeature feature)
  {
    return li.getRevision().getValue(feature);
  }

  public CDOList getList(EStructuralFeature feature, int size)
  {
    return li.getRevision().getList(feature, size);
  }

  public CDOList getList(EStructuralFeature feature)
  {
    return li.getRevision().getList(feature);
  }

  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    li.getRevision().setList(feature, list);
  }

  public int indexOf(EStructuralFeature feature, Object value)
  {
    return li.getRevision().indexOf(feature, value);
  }

  public boolean isEmpty(EStructuralFeature feature)
  {
    return li.getRevision().isEmpty(feature);
  }

  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    return li.getRevision().lastIndexOf(feature, value);
  }

  public void merge(CDORevisionDelta delta)
  {
    li.getRevision().merge(delta);
  }

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    return li.getRevision().move(feature, targetIndex, sourceIndex);
  }

  public Object remove(EStructuralFeature feature, int index)
  {
    return li.getRevision().remove(feature, index);
  }

  public Object set(EStructuralFeature feature, int index, Object value)
  {
    return li.getRevision().set(feature, index, value);
  }

  public Object setValue(EStructuralFeature feature, Object value)
  {
    return li.getRevision().setValue(feature, value);
  }

  public int size(EStructuralFeature feature)
  {
    return li.getRevision().size(feature);
  }

  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    return li.getRevision().toArray(feature, array);
  }

  public Object[] toArray(EStructuralFeature feature)
  {
    return li.getRevision().toArray(feature);
  }

  public void unset(EStructuralFeature feature)
  {
    li.getRevision().unset(feature);
  }

  public Object getValue(EStructuralFeature feature, int index)
  {
    return li.getRevision().getValue(feature, index);
  }

  public Object setValue(EStructuralFeature feature, int index, Object value)
  {
    return li.getRevision().setValue(feature, index, value);
  }

  public int hashCode(EStructuralFeature feature)
  {
    return li.getRevision().hashCode(feature);
  }
}

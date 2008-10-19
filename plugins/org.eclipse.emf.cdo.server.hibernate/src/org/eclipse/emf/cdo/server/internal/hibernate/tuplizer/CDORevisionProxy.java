/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

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

  public Object writeReplace()
  {
    return this;
  }

  public LazyInitializer getHibernateLazyInitializer()
  {
    return li;
  }

  public void add(CDOFeature feature, int index, Object value)
  {
    li.getRevision().add(feature, index, value);
  }

  public void adjustReferences(CDOReferenceAdjuster idMappings)
  {
    li.getRevision().adjustReferences(idMappings);
  }

  public void clear(CDOFeature feature)
  {
    li.getRevision().clear(feature);
  }

  public CDORevisionDelta compare(CDORevision origin)
  {
    return li.getRevision().compare(origin);
  }

  public boolean contains(CDOFeature feature, Object value)
  {
    return li.getRevision().contains(feature, value);
  }

  public Object get(CDOFeature feature, int index)
  {
    return li.getRevision().get(feature, index);
  }

  public CDOClass getCDOClass()
  {
    return li.getRevision().getCDOClass();
  }

  public Object getContainerID()
  {
    return li.getRevision().getContainerID();
  }

  public int getContainingFeatureID()
  {
    return li.getRevision().getContainingFeatureID();
  }

  public long getCreated()
  {
    return li.getRevision().getCreated();
  }

  public CDORevisionData getData()
  {
    return li.getRevision().getData();
  }

  public CDOID getID()
  {
    return li.getRevision().getID();
  }

  public CDOList getList(CDOFeature feature, int size)
  {
    return li.getRevision().getList(feature, size);
  }

  public CDOList getList(CDOFeature feature)
  {
    return li.getRevision().getList(feature);
  }

  public void setList(CDOFeature feature, InternalCDOList list)
  {
    li.getRevision().setList(feature, list);
  }

  public CDOID getResourceID()
  {
    return li.getRevision().getResourceID();
  }

  public long getRevised()
  {
    return li.getRevision().getRevised();
  }

  public CDORevision getRevision()
  {
    return li.getRevision().getRevision();
  }

  public Object getValue(CDOFeature feature)
  {
    return li.getRevision().getValue(feature);
  }

  public int getVersion()
  {
    return li.getRevision().getVersion();
  }

  public int hashCode(CDOFeature feature)
  {
    return li.getRevision().hashCode(feature);
  }

  public int indexOf(CDOFeature feature, Object value)
  {
    return li.getRevision().indexOf(feature, value);
  }

  public boolean isCurrent()
  {
    return li.getRevision().isCurrent();
  }

  public boolean isEmpty(CDOFeature feature)
  {
    return li.getRevision().isEmpty(feature);
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

  public boolean isSet(CDOFeature feature)
  {
    return li.getRevision().isSet(feature);
  }

  public boolean isTransactional()
  {
    return li.getRevision().isTransactional();
  }

  public boolean isValid(long timeStamp)
  {
    return li.getRevision().isValid(timeStamp);
  }

  public int lastIndexOf(CDOFeature feature, Object value)
  {
    return li.getRevision().lastIndexOf(feature, value);
  }

  public void merge(CDORevisionDelta delta)
  {
    li.getRevision().merge(delta);
  }

  public Object move(CDOFeature feature, int targetIndex, int sourceIndex)
  {
    return li.getRevision().move(feature, targetIndex, sourceIndex);
  }

  public Object remove(CDOFeature feature, int index)
  {
    return li.getRevision().remove(feature, index);
  }

  public Object set(CDOFeature feature, int index, Object value)
  {
    return li.getRevision().set(feature, index, value);
  }

  public void setContainerID(Object containerID)
  {
    li.getRevision().setContainerID(containerID);
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    li.getRevision().setContainingFeatureID(containingFeatureID);
  }

  public void setCreated(long created)
  {
    li.getRevision().setCreated(created);
  }

  public void setID(CDOID id)
  {
    li.getRevision().setID(id);
  }

  public void setListSize(CDOFeature feature, int size)
  {
    li.getRevision().setListSize(feature, size);
  }

  public void setResourceID(CDOID resourceID)
  {
    li.getRevision().setResourceID(resourceID);
  }

  public void setRevised(long revised)
  {
    li.getRevision().setRevised(revised);
  }

  public int setTransactional()
  {
    return li.getRevision().setTransactional();
  }

  public void setUntransactional()
  {
    li.getRevision().setUntransactional();
  }

  public Object setValue(CDOFeature feature, Object value)
  {
    return li.getRevision().setValue(feature, value);
  }

  public void setVersion(int version)
  {
    li.getRevision().setVersion(version);
  }

  public int size(CDOFeature feature)
  {
    return li.getRevision().size(feature);
  }

  public <T> T[] toArray(CDOFeature feature, T[] array)
  {
    return li.getRevision().toArray(feature, array);
  }

  public Object[] toArray(CDOFeature feature)
  {
    return li.getRevision().toArray(feature);
  }

  public void unset(CDOFeature feature)
  {
    li.getRevision().unset(feature);
  }
}

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
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;

import org.eclipse.net4j.util.collection.MoveableList;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface InternalCDORevision extends CDORevision, CDORevisionData
{
  public static final Object UNINITIALIZED = CDORevisionUtil.UNINITIALIZED;

  public void setID(CDOID id);

  public void setVersion(int version);

  public int setTransactional();

  public void setUntransactional();

  public void setCreated(long created);

  public void setRevised(long revised);

  public void setResourceID(CDOID resourceID);

  public void setContainerID(CDOID containerID);

  public void setContainingFeatureID(int containingFeatureID);

  public void add(CDOFeature feature, int index, Object value);

  public void clear(CDOFeature feature);

  public Object move(CDOFeature feature, int targetIndex, int sourceIndex);

  public Object remove(CDOFeature feature, int index);

  public Object set(CDOFeature feature, int index, Object value);

  public void unset(CDOFeature feature);

  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings);

  public Object getValue(CDOFeature feature);

  public Object setValue(CDOFeature feature, Object value);

  public MoveableList<Object> getList(CDOFeature feature);

  public MoveableList<Object> getList(CDOFeature feature, int size);

  public void setListSize(CDOFeature feature, int size);
}

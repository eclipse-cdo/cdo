/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;

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

  /**
   * @since 2.0
   */
  public void setContainerID(Object containerID);

  public void setContainingFeatureID(int containingFeatureID);

  public void add(CDOFeature feature, int index, Object value);

  public void clear(CDOFeature feature);

  public Object move(CDOFeature feature, int targetIndex, int sourceIndex);

  public Object remove(CDOFeature feature, int index);

  public Object set(CDOFeature feature, int index, Object value);

  public void unset(CDOFeature feature);

  /**
   * @since 2.0
   */
  public void adjustReferences(CDOReferenceAdjuster revisionAdjuster);

  public Object getValue(CDOFeature feature);

  public Object setValue(CDOFeature feature, Object value);

  /**
   * @since 2.0
   */
  public void setList(CDOFeature feature, InternalCDOList list);

  /**
   * @since 2.0
   */
  public CDOList getList(CDOFeature feature);

  /**
   * @param size
   *          the size of a new list to be created if this revision has no list so far, or -1 to skip list creation and
   *          return <code>null</code> in this case.
   * @since 2.0
   */
  public CDOList getList(CDOFeature feature, int size);

  @Deprecated
  public void setListSize(CDOFeature feature, int size);
}

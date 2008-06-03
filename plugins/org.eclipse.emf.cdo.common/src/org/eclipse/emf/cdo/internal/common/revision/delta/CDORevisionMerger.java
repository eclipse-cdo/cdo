/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

/**
 * @author Simon McDuff
 */
public class CDORevisionMerger extends CDOFeatureDeltaVisitorImpl
{
  private InternalCDORevision revision;

  public CDORevisionMerger()
  {
  }

  public void merge(InternalCDORevision revision, CDORevisionDelta delta)
  {
    this.revision = revision;
    delta.accept(this);
    revision = null;
  }

  @Override
  public void visit(CDOMoveFeatureDelta delta)
  {
    revision.move(delta.getFeature(), delta.getOldPosition(), delta.getNewPosition());
  }

  @Override
  public void visit(CDOAddFeatureDelta delta)
  {
    revision.add(delta.getFeature(), delta.getIndex(), delta.getValue());
  }

  @Override
  public void visit(CDORemoveFeatureDelta delta)
  {
    revision.remove(delta.getFeature(), delta.getIndex());
  }

  @Override
  public void visit(CDOSetFeatureDelta delta)
  {
    revision.set(delta.getFeature(), delta.getIndex(), delta.getValue());
  }

  @Override
  public void visit(CDOUnsetFeatureDelta delta)
  {
    revision.unset(delta.getFeature());
  }

  @Override
  public void visit(CDOClearFeatureDelta delta)
  {
    revision.clear(delta.getFeature());
  }

  @Override
  public void visit(CDOContainerFeatureDelta delta)
  {
    revision.setContainerID(delta.getContainerID());
    revision.setContainingFeatureID(delta.getContainerFeatureID());
  }
}

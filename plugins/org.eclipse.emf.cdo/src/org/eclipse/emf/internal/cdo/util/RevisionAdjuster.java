/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/

package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import java.util.List;

/**
 * @author Simon McDuff
 */
public class RevisionAdjuster extends CDOFeatureDeltaVisitorImpl
{
  private CDOIDProvider idProvider;

  private InternalCDORevision revision;

  public RevisionAdjuster(CDOIDProvider idProvider)
  {
    this.idProvider = idProvider;
  }

  public void adjustRevision(InternalCDORevision revision, CDORevisionDelta revisionDelta)
  {
    this.revision = revision;
    revisionDelta.accept(this);
  }

  @Override
  public void visit(CDOAddFeatureDelta delta)
  {
    // Delta value must have been adjusted before!
    revision.setValue(delta.getFeature(), delta.getValue());
  }

  @Override
  public void visit(CDOSetFeatureDelta delta)
  {
    CDOFeature feature = delta.getFeature();
    Object value = delta.getValue();
    if (value != null && feature.isReference() && !(value instanceof CDOReferenceProxy))
    {
      revision.setValue(feature, idProvider.provideCDOID(value));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void visit(CDOListFeatureDelta deltas)
  {
    CDOFeature feature = deltas.getFeature();
    List<Object> list = (List<Object>)revision.getValue(feature);
    int[] indices = ((CDOListFeatureDeltaImpl)deltas).reconstructAddedIndices();
    for (int i = 1; i <= indices[0]; i++)
    {
      int index = indices[i];
      Object value = list.get(index);
      if (value != null && feature.isReference() && !(value instanceof CDOReferenceProxy))
      {
        value = idProvider.provideCDOID(value);
        list.set(index, value);
      }
    }
  }
}

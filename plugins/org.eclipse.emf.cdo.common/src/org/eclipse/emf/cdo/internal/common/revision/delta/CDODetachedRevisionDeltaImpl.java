/*
 * Copyright (c) 2010-2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDODetachedRevisionDeltaImpl implements CDORevisionDelta
{
  public CDODetachedRevisionDeltaImpl()
  {
  }

  @Override
  public CDOID getID()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOBranch getBranch()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getVersion()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public EClass getEClass()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDORevisable getTarget()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isResourcePathChange()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDORevisionDelta copy()
  {
    return this;
  }

  @Override
  public CDOFeatureDelta getFeatureDelta(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<CDOFeatureDelta> getFeatureDeltas()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void apply(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void applyTo(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void accept(CDOFeatureDeltaVisitor visitor, org.eclipse.net4j.util.Predicate<EStructuralFeature> filter)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor, java.util.function.Predicate<EStructuralFeature> filter)
  {
    throw new UnsupportedOperationException();
  }
}

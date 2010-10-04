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
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDODetachedRevisionDeltaImpl implements CDORevisionDelta
{
  /**
   * This constant is only passed into conflict resolvers to indicate that a conflict was caused by remote detachment of
   * an object. Calling any method on this marker instance will result in an {@link UnsupportedOperationException} being
   * thrown.
   * 
   * @since 3.0
   */
  public static final CDORevisionDelta DETACHED = new CDODetachedRevisionDeltaImpl();

  public CDODetachedRevisionDeltaImpl()
  {
  }

  public CDOID getID()
  {
    throw new UnsupportedOperationException();
  }

  public CDOBranch getBranch()
  {
    throw new UnsupportedOperationException();
  }

  public int getVersion()
  {
    throw new UnsupportedOperationException();
  }

  public EClass getEClass()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isEmpty()
  {
    throw new UnsupportedOperationException();
  }

  public List<CDOFeatureDelta> getFeatureDeltas()
  {
    throw new UnsupportedOperationException();
  }

  public void apply(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    throw new UnsupportedOperationException();
  }
}

/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDODetachedRevisionDeltaImpl;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface CDORevisionDelta extends CDORevisionKey
{
  /**
   * This constant is only passed into conflict resolvers to indicate that a conflict was caused by remote detachment of
   * an object. Calling any method on this marker instance will result in an {@link UnsupportedOperationException} being
   * thrown.
   * 
   * @since 3.1
   */
  public static final CDORevisionDelta DETACHED = new CDODetachedRevisionDeltaImpl();

  /**
   * @since 3.0
   */
  public EClass getEClass();

  /**
   * @since 3.0
   */
  public boolean isEmpty();

  /**
   * @since 3.1
   */
  public CDORevisionDelta copy();

  public List<CDOFeatureDelta> getFeatureDeltas();

  public void apply(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);
}

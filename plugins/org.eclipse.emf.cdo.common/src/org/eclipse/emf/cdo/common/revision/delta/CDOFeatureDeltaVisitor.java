/*
 * Copyright (c) 2008, 2009, 2011-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.revision.delta;

/**
 * Visits {@link CDOFeatureDelta feature deltas} of a {@link CDORevisionDelta revision delta}.
 *
 * @see CDORevisionDelta#accept(CDOFeatureDeltaVisitor)
 * @author Simon McDuff
 */
public interface CDOFeatureDeltaVisitor
{
  public void visit(CDOMoveFeatureDelta delta);

  public void visit(CDOAddFeatureDelta delta);

  public void visit(CDORemoveFeatureDelta delta);

  public void visit(CDOSetFeatureDelta delta);

  public void visit(CDOUnsetFeatureDelta delta);

  public void visit(CDOListFeatureDelta delta);

  public void visit(CDOClearFeatureDelta delta);

  public void visit(CDOContainerFeatureDelta delta);
}

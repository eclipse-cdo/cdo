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
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;

/**
 * @author Simon McDuff
 */
public interface CDOFeatureDelta
{
  public Type getType();

  public CDOFeature getFeature();

  public void apply(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);

  /**
   * @author Simon McDuff
   */
  public enum Type
  {
    ADD, REMOVE, CLEAR, MOVE, SET, UNSET, LIST, CONTAINER
  }
}

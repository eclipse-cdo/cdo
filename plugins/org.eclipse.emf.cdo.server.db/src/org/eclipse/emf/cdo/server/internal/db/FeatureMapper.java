/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.protocol.model.CDOFeature;

/**
 * @author Eike Stepper
 */
public class FeatureMapper
{
  private CDOFeature feature;

  private int index;

  public FeatureMapper(CDOFeature feature, int index)
  {
    this.feature = feature;
    this.index = index;
  }

  public CDOFeature getFeature()
  {
    return feature;
  }

  public int getIndex()
  {
    return index;
  }
}

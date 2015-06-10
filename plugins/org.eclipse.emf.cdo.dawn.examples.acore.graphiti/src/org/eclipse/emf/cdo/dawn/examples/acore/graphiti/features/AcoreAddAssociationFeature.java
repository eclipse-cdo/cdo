/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;

/**
 * @author Martin Fluegge
 */
public class AcoreAddAssociationFeature extends AcoreBasicAddConnectionFeature
{
  public AcoreAddAssociationFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context)
  {
    return context instanceof IAddConnectionContext && context.getNewObject() instanceof AClass;
  }

  @Override
  protected Polyline createTargetDecoration(GraphicsAlgorithmContainer gaContainer)
  {
    return null;
  }

  @Override
  protected String getConnectionLabel()
  {
    return "association";
  }
}

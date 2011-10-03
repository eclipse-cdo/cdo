/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * @author Martin Fluegge
 */
public class AcoreAddImplementationFeature extends AcoreBasicAddConnectionFeature
{
  public AcoreAddImplementationFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context)
  {
    return context instanceof IAddConnectionContext && context.getNewObject() instanceof ABasicClass;
  }

  @Override
  protected Polyline createTargetDecoration(GraphicsAlgorithmContainer gaContainer)
  {
    IGaService gaService = Graphiti.getGaService();
    Polyline polyline = gaService.createPolyline(gaContainer, new int[] { -15, 10, 0, 0, -15, -10 });
    polyline.setForeground(manageColor(IColorConstant.BLACK));
    polyline.setLineWidth(2);
    return polyline;
  }

  @Override
  protected String getConnectionLabel()
  {
    return "implements";
  }
}

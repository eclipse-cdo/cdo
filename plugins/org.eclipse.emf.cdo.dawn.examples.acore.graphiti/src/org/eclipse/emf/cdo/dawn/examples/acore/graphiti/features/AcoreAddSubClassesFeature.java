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

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * @author Martin Fluegge
 */
public class AcoreAddSubClassesFeature extends AcoreBasicAddConnectionFeature
{
  public AcoreAddSubClassesFeature(IFeatureProvider fp)
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
    IGaService gaService = Graphiti.getGaService();
    Polygon polygon = gaService.createPolygon(gaContainer, new int[] { -15, 10, 0, 0, -15, -10 });

    polygon.setForeground(manageColor(IColorConstant.BLACK));
    polygon.setBackground(manageColor(IColorConstant.WHITE));
    polygon.setLineWidth(2);
    return polygon;
  }

  @Override
  protected String getConnectionLabel()
  {
    return "extends";
  }
}

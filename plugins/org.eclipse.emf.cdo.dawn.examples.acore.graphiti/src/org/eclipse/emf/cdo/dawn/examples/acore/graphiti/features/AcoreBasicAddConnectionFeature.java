/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.ecore.EObject;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * @author Martin Fluegge
 */
public abstract class AcoreBasicAddConnectionFeature extends AbstractAddFeature
{
  public AcoreBasicAddConnectionFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  public PictogramElement add(IAddContext context)
  {
    IAddConnectionContext addConContext = (IAddConnectionContext)context;
    // EReference addedEReference = (EReference)context.getNewObject();
    EObject addedObject = (EObject)context.getNewObject();
    IPeCreateService peCreateService = Graphiti.getPeCreateService();

    // CONNECTION WITH POLYLINE
    Connection connection = peCreateService.createFreeFormConnection(getDiagram());
    connection.setStart(addConContext.getSourceAnchor());
    connection.setEnd(addConContext.getTargetAnchor());

    IGaService gaService = Graphiti.getGaService();
    Polyline polyline = gaService.createPolyline(connection);
    polyline.setLineWidth(2);
    polyline.setForeground(manageColor(IColorConstant.BLACK));

    // we need to put in an object, otherwise the line will be marked dotted
    // link(connection, addedObject);

    ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);

    Text text = gaService.createDefaultText(getDiagram(), textDecorator);

    text.setForeground(manageColor(IColorConstant.BLACK));
    gaService.setLocation(text, 10, 0);

    text.setValue(getConnectionLabel());

    ConnectionDecorator cd;
    cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
    createTargetDecoration(cd);
    return connection;
  }

  protected abstract String getConnectionLabel();

  public abstract boolean canAdd(IAddContext context);

  protected abstract Polyline createTargetDecoration(GraphicsAlgorithmContainer gaContainer);
}

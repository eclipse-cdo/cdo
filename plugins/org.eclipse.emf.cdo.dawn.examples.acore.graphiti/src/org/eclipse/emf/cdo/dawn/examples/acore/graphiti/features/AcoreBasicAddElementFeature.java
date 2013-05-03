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

import org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.DawnGraphitiAcoreResourceUtil;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

/**
 * @author Martin Fluegge
 */
public abstract class AcoreBasicAddElementFeature extends AbstractAddShapeFeature
{
  public AcoreBasicAddElementFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  public abstract boolean canAdd(IAddContext context);

  protected abstract Color getBackgroundColor();

  protected abstract Color getForegroundColor();

  public PictogramElement add(IAddContext context)
  {
    ABasicClass addedClass = (ABasicClass)context.getNewObject();
    Diagram targetDiagram = (Diagram)context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    IPeCreateService peCreateService = Graphiti.getPeCreateService();
    // define a default size for the shape
    int width = 100;
    int height = 50;
    IGaService gaService = Graphiti.getGaService();

    addToResourceIfNeeded(addedClass);

    ContainerShape containerShape = createContainerShape(context, addedClass, targetDiagram, peCreateService, width,
        height, gaService);

    return containerShape;
  }

  private ContainerShape createContainerShape(IAddContext context, ABasicClass addedClass, Diagram targetDiagram,
      IPeCreateService peCreateService, int width, int height, IGaService gaService)
  {
    ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);
    // create and set graphics algorithm
    RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(containerShape, 5, 5);
    roundedRectangle.setForeground(getForegroundColor());
    roundedRectangle.setBackground(getBackgroundColor());
    roundedRectangle.setLineWidth(2);
    gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), width, height);

    // create link and wire it
    link(containerShape, addedClass);

    // SHAPE WITH LINE
    {
      // create shape for line
      Shape shape = peCreateService.createShape(containerShape, false);

      // create and set graphics algorithm
      Polyline polyline = gaService.createPolyline(shape, new int[] { 0, 20, width, 20 });
      polyline.setForeground(getForegroundColor());
      polyline.setLineWidth(2);
    }

    // SHAPE WITH TEXT
    {
      // create shape for text
      final Shape shape = peCreateService.createShape(containerShape, false);

      // create and set text graphics algorithm
      final Text text = gaService.createText(shape, addedClass.getName());
      // text.setStyle(StyleUtil.getStyleForAClassText(getDiagram()));
      text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
      text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
      gaService.setLocationAndSize(text, 0, 0, width, 20);

      // create link and wire it
      link(shape, addedClass);

      activateDirectEditing(containerShape, shape, text);
    }

    // add a chopbox anchor to the shape
    peCreateService.createChopboxAnchor(containerShape);

    // create an additional box relative anchor at middle-right
    final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);

    boxAnchor.setRelativeWidth(1.0);
    boxAnchor.setRelativeHeight(0.5);

    // anchor references visible rectangle instead of invisible rectangle
    boxAnchor.setReferencedGraphicsAlgorithm(roundedRectangle);

    // assign a graphics algorithm for the box relative anchor
    Rectangle rectangle = gaService.createRectangle(boxAnchor);
    rectangle.setFilled(true);

    // anchor is located on the right border of the visible rectangle
    // and touches the border of the invisible rectangle

    int w = 6;
    gaService.setLocationAndSize(rectangle, -2 * w, -w, 2 * w, 2 * w);
    rectangle.setForeground(getForegroundColor());
    rectangle.setBackground(getBackgroundColor());

    // call the layout feature
    layoutPictogramElement(containerShape);
    return containerShape;
  }

  private void activateDirectEditing(ContainerShape containerShape, final Shape shape, final Text text)
  {
    // provide information to support direct-editing directly
    // after object creation (must be activated additionally)
    final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
    // set container shape for direct editing after object creation
    directEditingInfo.setMainPictogramElement(containerShape);
    // set shape and graphics algorithm where the editor for
    // direct editing shall be opened after object creation
    directEditingInfo.setPictogramElement(shape);
    directEditingInfo.setGraphicsAlgorithm(text);
  }

  private void addToResourceIfNeeded(ABasicClass addedClass)
  {
    // if added Class has no resource we add it to the resource
    // of the diagram
    // in a real scenario the business model would have its own resource
    if (addedClass.eResource() == null)
    {
      // getDiagram().eResource().getContents().add(addedClass);
      DawnGraphitiAcoreResourceUtil.addToModelResource(addedClass, getDiagram().eResource().getResourceSet());
    }
  }
}

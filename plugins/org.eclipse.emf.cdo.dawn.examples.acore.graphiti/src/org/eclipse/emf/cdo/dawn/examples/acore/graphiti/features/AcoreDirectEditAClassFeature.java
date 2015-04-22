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

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * @author Martin Fluegge
 */
public class AcoreDirectEditAClassFeature extends AbstractDirectEditingFeature
{

  public AcoreDirectEditAClassFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  public int getEditingType()
  {
    // there are several possible editor-types supported:
    // text-field, checkbox, color-chooser, combobox, ...
    return TYPE_TEXT;
  }

  @Override
  public boolean canDirectEdit(IDirectEditingContext context)
  {
    PictogramElement pe = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pe);
    GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
    // support direct editing, if it is a AClass, and the user clicked
    // directly on the text and not somewhere else in the rectangle
    if (bo instanceof AClass && ga instanceof Text)
    {
      return true;
    }
    // direct editing not supported in all other cases
    return false;
  }

  public String getInitialValue(IDirectEditingContext context)
  {
    // return the current name of the AClass
    PictogramElement pe = context.getPictogramElement();
    AClass AClass = (AClass)getBusinessObjectForPictogramElement(pe);
    return AClass.getName();
  }

  @Override
  public String checkValueValid(String value, IDirectEditingContext context)
  {
    if (value.length() < 1)
    {
      return "Please enter any text as class name.";
    }
    if (value.contains(" "))
    {
      return "Spaces are not allowed in class names.";
    }
    if (value.contains("\n"))
    {
      return "Line breakes are not allowed in class names.";
    }

    // null means, that the value is valid
    return null;
  }

  @Override
  public void setValue(String value, IDirectEditingContext context)
  {
    // set the new name for the AClass
    PictogramElement pe = context.getPictogramElement();
    AClass AClass = (AClass)getBusinessObjectForPictogramElement(pe);
    AClass.setName(value);

    // Explicitly update the shape to display the new value in the diagram
    // Note, that this might not be necessary in future versions of Graphiti
    // (currently in discussion)

    // we know, that pe is the Shape of the Text, so its container is the
    // main shape of the AClass
    updatePictogramElement(((Shape)pe).getContainer());
  }
}

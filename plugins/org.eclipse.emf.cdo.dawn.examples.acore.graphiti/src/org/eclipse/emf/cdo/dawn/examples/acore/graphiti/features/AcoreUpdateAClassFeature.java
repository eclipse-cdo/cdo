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
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * @author Martin Fluegge
 */
public class AcoreUpdateAClassFeature extends AbstractUpdateFeature
{

  public AcoreUpdateAClassFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  public boolean canUpdate(IUpdateContext context)
  {
    // return true, if linked business object is a AClass
    Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return bo instanceof AClass;
  }

  public IReason updateNeeded(IUpdateContext context)
  {
    // retrieve name from pictogram model
    String pictogramName = null;
    PictogramElement pictogramElement = context.getPictogramElement();
    if (pictogramElement instanceof ContainerShape)
    {
      ContainerShape cs = (ContainerShape)pictogramElement;
      for (Shape shape : cs.getChildren())
      {
        if (shape.getGraphicsAlgorithm() instanceof Text)
        {
          Text text = (Text)shape.getGraphicsAlgorithm();
          pictogramName = text.getValue();
        }
      }
    }

    // retrieve name from business model
    String businessName = null;
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof AClass)
    {
      AClass AClass = (AClass)bo;
      businessName = AClass.getName();
    }

    // update needed, if names are different
    boolean updateNameNeeded = pictogramName == null && businessName != null
        || pictogramName != null && !pictogramName.equals(businessName);
    if (updateNameNeeded)
    {
      return Reason.createTrueReason("Name is out of date");
    }
    else
    {
      return Reason.createFalseReason();
    }
  }

  public boolean update(IUpdateContext context)
  {
    // retrieve name from business model
    String businessName = null;
    PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    if (bo instanceof AClass)
    {
      AClass AClass = (AClass)bo;
      businessName = AClass.getName();
    }

    // Set name in pictogram model
    if (pictogramElement instanceof ContainerShape)
    {
      ContainerShape cs = (ContainerShape)pictogramElement;
      for (Shape shape : cs.getChildren())
      {
        if (shape.getGraphicsAlgorithm() instanceof Text)
        {
          Text text = (Text)shape.getGraphicsAlgorithm();
          text.setValue(businessName);
          return true;
        }
      }
    }

    return false;
  }
}

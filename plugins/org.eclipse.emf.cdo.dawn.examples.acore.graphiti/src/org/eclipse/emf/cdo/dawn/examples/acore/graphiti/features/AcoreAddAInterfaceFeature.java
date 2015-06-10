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

import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * @author Martin Fluegge
 */
public class AcoreAddAInterfaceFeature extends AcoreBasicAddElementFeature
{
  private static final IColorConstant CLASS_TEXT_FOREGROUND = new ColorConstant(51, 51, 153);

  private static final IColorConstant CLASS_FOREGROUND = new ColorConstant(150, 150, 255);

  private static final IColorConstant CLASS_BACKGROUND = new ColorConstant(255, 255, 190);

  public AcoreAddAInterfaceFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context)
  {
    // check if user wants to add a AClass
    if (context.getNewObject() instanceof AInterface)
    {
      // check if user wants to add to a diagram
      if (context.getTargetContainer() instanceof Diagram)
      {
        return true;
      }
    }
    return false;
  }

  @Override
  protected Color getBackgroundColor()
  {
    return manageColor(CLASS_BACKGROUND);
  }

  @Override
  protected Color getForegroundColor()
  {
    return manageColor(CLASS_FOREGROUND);
  }
}

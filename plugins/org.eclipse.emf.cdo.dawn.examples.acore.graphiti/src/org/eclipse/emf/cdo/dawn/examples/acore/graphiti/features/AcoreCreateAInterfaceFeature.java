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
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Martin Fluegge
 */
public class AcoreCreateAInterfaceFeature extends AcoreBasicCreateElementFeature
{
  public AcoreCreateAInterfaceFeature(IFeatureProvider fp)
  {
    // set name and description of the creation feature
    super(fp, "AInterface", "Create AInterface");
  }

  @Override
  public boolean canCreate(ICreateContext context)
  {
    return context.getTargetContainer() instanceof Diagram;
  }

  @Override
  public String getCreateImageId()
  {
    return "org.eclipse.graphiti.examples.tutorial.ereference";
  }

  @Override
  protected EObject createElement()
  {
    AInterface aInterface = AcoreFactory.eINSTANCE.createAInterface();
    aInterface.setName("AInterface");
    return aInterface;
  }
}

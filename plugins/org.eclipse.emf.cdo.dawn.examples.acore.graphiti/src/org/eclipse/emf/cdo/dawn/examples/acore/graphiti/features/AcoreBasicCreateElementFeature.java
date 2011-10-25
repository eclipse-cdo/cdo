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

import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.DawnGraphitiAcoreResourceUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Martin Fluegge
 */
public abstract class AcoreBasicCreateElementFeature extends AbstractCreateFeature
{
  public AcoreBasicCreateElementFeature(IFeatureProvider fp, String name, String description)
  {
    super(fp, name, description);
  }

  public boolean canCreate(ICreateContext context)
  {
    return context.getTargetContainer() instanceof Diagram;
  }

  public Object[] create(ICreateContext context)
  {
    EObject newObject = createElement();

    DawnGraphitiAcoreResourceUtil.addToModelResource(newObject, getDiagram().eResource().getResourceSet());

    // do the add
    addGraphicalRepresentation(context, newObject);

    // activate direct editing after object creation
    getFeatureProvider().getDirectEditingInfo().setActive(true);
    // return newly created business object(s)
    return new Object[] { newObject };
  }

  protected abstract EObject createElement();

  @Override
  public String getCreateImageId()
  {
    return "org.eclipse.graphiti.examples.tutorial.ereference";
  }
}

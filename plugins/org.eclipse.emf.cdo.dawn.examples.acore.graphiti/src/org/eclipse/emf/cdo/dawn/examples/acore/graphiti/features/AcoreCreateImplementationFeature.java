/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.AcoreGraphitiContextUtil.ConnectionType;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;

/**
 * @author Martin Fluegge
 */
public class AcoreCreateImplementationFeature extends AcoreBasicCreateConnectionFeature
{
  public AcoreCreateImplementationFeature(IFeatureProvider fp)
  {
    // provide name and description for the UI, e.g. the palette
    super(fp, "implements", "implements");
  }

  @Override
  public boolean canStartConnection(ICreateConnectionContext context)
  {
    ABasicClass aClass = getAClass(context.getSourceAnchor());
    if (aClass != null)
    {
      return true;
    }
    return false;
  }

  /**
   * Creates a EReference between two AClasses.
   */
  @Override
  protected void createConnection(EObject source, EObject target)
  {
    ((AClass)source).getImplementedInterfaces().add((AInterface)target);
  }

  @Override
  protected EObject getTarget(Anchor targetAnchor)
  {
    return getAClass(targetAnchor);
  }

  @Override
  protected EObject getSource(Anchor anchor)
  {
    return getAClass(anchor);
  }

  @Override
  protected ConnectionType getConnectionType()
  {
    return ConnectionType.IMPLEMENTATIONS;
  }

  private ABasicClass getAClass(Anchor anchor)
  {
    if (anchor != null)
    {
      Object object = getBusinessObjectForPictogramElement(anchor.getParent());
      if (object instanceof ABasicClass)
      {
        return (ABasicClass)object;
      }
    }
    return null;
  }
}

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

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.AcoreGraphitiContextUtil.ConnectionType;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;

/**
 * @author Martin Fluegge
 */
public class AcoreCreateAggregationFeature extends AcoreBasicCreateConnectionFeature
{
  public AcoreCreateAggregationFeature(IFeatureProvider fp)
  {
    // provide name and description for the UI, e.g. the palette
    super(fp, "aggregation", "aggregation");
  }

  @Override
  public boolean canCreate(ICreateConnectionContext context)
  {
    // return true if both anchors belong to an AClass
    // and those AClasses are not identical
    AClass source = getAClass(context.getSourceAnchor());
    AClass target = getAClass(context.getTargetAnchor());
    if (source != null && target != null && source != target)
    {
      return true;
    }
    return false;
  }

  @Override
  public boolean canStartConnection(ICreateConnectionContext context)
  {
    if (getAClass(context.getSourceAnchor()) != null)
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
    ((AClass)source).getAssociations().add((AClass)target);
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
    return ConnectionType.AGGREGATIONS;
  }

  /**
   * Returns the AClass belonging to the anchor, or null if not available.
   */
  private AClass getAClass(Anchor anchor)
  {
    if (anchor != null)
    {
      Object object = getBusinessObjectForPictogramElement(anchor.getParent());
      if (object instanceof AClass)
      {
        return (AClass)object;
      }
    }
    return null;
  }
}

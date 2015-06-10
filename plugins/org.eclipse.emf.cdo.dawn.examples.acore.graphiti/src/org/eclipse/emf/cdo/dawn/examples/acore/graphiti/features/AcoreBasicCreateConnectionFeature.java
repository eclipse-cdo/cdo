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

import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.AcoreGraphitiContextUtil;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.AcoreGraphitiContextUtil.ConnectionType;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;

/**
 * @author Martin Fluegge
 */
public abstract class AcoreBasicCreateConnectionFeature extends AbstractCreateConnectionFeature
{
  public AcoreBasicCreateConnectionFeature(IFeatureProvider fp, String name, String description)
  {
    super(fp, name, description);
  }

  public boolean canCreate(ICreateConnectionContext context)
  {
    EObject source = getSource(context.getSourceAnchor());
    EObject target = getTarget(context.getTargetAnchor());
    if (source != null && target != null && source != target)
    {
      return true;
    }
    return false;
  }

  public abstract boolean canStartConnection(ICreateConnectionContext context);

  public Connection create(ICreateConnectionContext context)
  {
    Connection newConnection = null;

    // get AClasses which should be connected
    EObject source = getSource(context.getSourceAnchor());
    EObject target = getTarget(context.getTargetAnchor());

    if (source != null && target != null)
    {
      // create new business object
      createConnection(source, target);
      // add connection for business object
      AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());

      addContext.putProperty(AcoreGraphitiContextUtil.CONNECTTION_TYPE, getConnectionType());

      addContext.setNewObject(source);
      newConnection = (Connection)getFeatureProvider().addIfPossible(addContext);
    }

    return newConnection;
  }

  protected abstract ConnectionType getConnectionType();

  protected abstract EObject getTarget(Anchor targetAnchor);

  /**
   * Returns the AClass belonging to the anchor, or null if not available.
   */
  protected abstract EObject getSource(Anchor anchor);

  /**
   * Creates a EReference between two AClasses.
   */
  protected abstract void createConnection(EObject source, EObject target);
}

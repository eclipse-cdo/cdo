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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;

/**
 * @author Martin Fluegge
 */
public class AcoreReconnectionFeature extends DefaultReconnectionFeature
{
  public AcoreReconnectionFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  @Override
  public boolean canReconnect(IReconnectionContext context)
  {
    // do not allow to reconnect
    return true;
  }
}

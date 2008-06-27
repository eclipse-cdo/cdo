/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Simon McDuff
 */
public interface CDOChangeSubscriptionPolicy
{
  public static CDOChangeSubscriptionPolicy NONE =  new CDOChangeSubscriptionPolicy()
  {
    public boolean valid(EObject eObject, Adapter adapter)
    {
      return false;
    }
  };
  
  public static CDOChangeSubscriptionPolicy ALL =  new CDOChangeSubscriptionPolicy()
  {
    public boolean valid(EObject eObject, Adapter adapter)
    {
      return true;
    }
  };
  
  boolean valid(EObject eObject, Adapter adapter);
}

/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.compare;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.scope.IComparisonScope;

import java.util.Set;

/**
 * A {@link Comparison comparison} that can be closed to dispose of used resources.
 *
 * @author Eike Stepper
 */
public class CDOComparison extends DelegatingComparison implements CloseableComparison
{
  private final IComparisonScope scope;

  private Set<Object> objectsToDeactivateOnClose;

  public CDOComparison(IComparisonScope scope, Comparison delegate, Set<Object> objectsToDeactivateOnClose)
  {
    super(delegate);
    this.scope = scope;
    this.objectsToDeactivateOnClose = objectsToDeactivateOnClose;
  }

  public final IComparisonScope getScope()
  {
    return scope;
  }

  public boolean isClosed()
  {
    return delegate == null;
  }

  public void close()
  {
    if (delegate != null)
    {
      delegate = null;
      if (objectsToDeactivateOnClose != null)
      {
        for (Object object : objectsToDeactivateOnClose)
        {
          LifecycleUtil.deactivate(object);
        }

        objectsToDeactivateOnClose = null;
      }
    }
  }
}
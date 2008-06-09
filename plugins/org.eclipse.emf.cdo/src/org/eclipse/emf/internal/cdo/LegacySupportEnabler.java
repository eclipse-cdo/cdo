/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class LegacySupportEnabler implements IElementProcessor
{
  private boolean legacySupportEnabled;

  public LegacySupportEnabler(boolean legacySupportEnabled)
  {
    this.legacySupportEnabled = legacySupportEnabled;
  }

  public LegacySupportEnabler()
  {
    this(true);
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof CDOSessionImpl)
    {
      CDOSessionImpl session = (CDOSessionImpl)element;
      session.setLegacySupportEnabled(legacySupportEnabled);
    }

    return element;
  }
}

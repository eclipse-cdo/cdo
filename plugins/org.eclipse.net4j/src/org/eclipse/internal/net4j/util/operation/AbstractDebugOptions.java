/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.operation;

import org.eclipse.net4j.util.operation.IDebugOptions;

/**
 * @author Eike Stepper
 */
public abstract class AbstractDebugOptions implements IDebugOptions
{
  private boolean initialized;

  private boolean debugging;

  public AbstractDebugOptions()
  {
  }

  public void dispose()
  {
  }

  public boolean isDebugging()
  {
    if (!initialized)
    {
      debugging = getBooleanOption("debug", false);
      initialized = true;
    }

    return debugging;
  }

  public void setDebugging(boolean debugging)
  {
    this.debugging = debugging;
    initialized = true;
  }
}

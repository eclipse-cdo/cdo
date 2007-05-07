/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

/**
 * @author Eike Stepper
 */
public class ParallelStep extends CompoundStep
{
  public ParallelStep()
  {
  }

  @Override
  public boolean isReady()
  {
    for (Step child : this)
    {
      if (child.isReady())
      {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isFinished()
  {
    for (Step child : this)
    {
      if (!child.isFinished())
      {
        return false;
      }
    }

    return true;
  }
}

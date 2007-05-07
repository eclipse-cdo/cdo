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
public class SequentialStep extends CompoundStep
{
  public SequentialStep()
  {
  }

  @Override
  public boolean isReady()
  {
    return isEmpty() || getFirst().isReady();
  }

  @Override
  public boolean isFinished()
  {
    return isEmpty() || getLast().isFinished();
  }
}

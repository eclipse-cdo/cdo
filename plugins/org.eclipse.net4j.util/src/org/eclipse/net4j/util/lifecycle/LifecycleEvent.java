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
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.event.Event;

/**
 * @author Eike Stepper
 */
public class LifecycleEvent extends Event implements ILifecycleEvent
{
  private static final long serialVersionUID = 1L;

  private Kind kind;

  public LifecycleEvent(Lifecycle lifecycle, Kind kind)
  {
    super(lifecycle);
    this.kind = kind;
  }

  public ILifecycle getLifecycle()
  {
    return (ILifecycle)getSource();
  }

  public Kind getKind()
  {
    return kind;
  }
}

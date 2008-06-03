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
package org.eclipse.net4j.util.event;

import java.util.EventObject;

/**
 * @author Eike Stepper
 */
public class Event extends EventObject implements IEvent
{
  private static final long serialVersionUID = 1L;

  public Event(INotifier notifier)
  {
    super(notifier);
  }

  public INotifier getSource()
  {
    return (INotifier)source;
  }
}

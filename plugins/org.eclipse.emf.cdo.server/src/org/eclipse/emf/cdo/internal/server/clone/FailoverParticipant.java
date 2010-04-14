/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.clone;

/**
 * @author Eike Stepper
 */
public class FailoverParticipant extends SynchronizableRepository
{
  private Type type;

  public FailoverParticipant()
  {
    setState(State.OFFLINE);
  }

  @Override
  public Type getType()
  {
    return type;
  }

  public void setType(Type type)
  {
    this.type = type;
  }
}

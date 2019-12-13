/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IBuddyContainer;
import org.eclipse.net4j.buddies.common.ISession;

/**
 * @author Eike Stepper
 */
public interface IBuddySession extends ISession, IBuddyContainer
{
  @Override
  public IBuddy getSelf();

  @Override
  public void close();
}

/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.server.ITopic;

/**
 * @author Eike Stepper
 * @since 4.17
 */
public interface InternalTopic extends ITopic
{
  @Override
  public InternalTopicManager getManager();

  public boolean containsSession(InternalSession session);

  @Override
  public InternalSession[] getSessions();

  public InternalSession[] addSession(InternalSession session);

  public boolean removeSession(InternalSession session);
}

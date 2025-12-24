/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.server.ITopicManager;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.17
 */
public interface InternalTopicManager extends ITopicManager
{
  @Override
  public InternalSessionManager getSessionManager();

  @Override
  public InternalTopic getTopic(String id);

  public InternalTopic getTopic(String id, boolean createOnDemand);

  @Override
  public InternalTopic[] getTopics();

  public InternalSession[] addSubscription(String id, InternalSession session);

  public void removeSubscription(String id, InternalSession session);

  public void removeTopic(InternalTopic topic);

  public List<InternalTopic> sessionClosed(InternalSession session);
}

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

/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.chat;

import org.eclipse.net4j.buddies.chat.IChat;
import org.eclipse.net4j.buddies.spi.common.ClientFacilityFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class ChatFactory extends ClientFacilityFactory
{
  public ChatFactory()
  {
    super(IChat.TYPE);
  }

  @Override
  public Chat create(String description) throws ProductCreationException
  {
    return new Chat();
  }
}

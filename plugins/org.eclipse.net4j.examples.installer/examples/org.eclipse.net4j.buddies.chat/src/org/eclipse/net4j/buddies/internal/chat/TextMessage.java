/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.chat;

import org.eclipse.net4j.buddies.spi.common.Message;

/**
 * @author Eike Stepper
 */
public class TextMessage extends Message
{
  private static final long serialVersionUID = 1L;

  private String text;

  public TextMessage(String text)
  {
    this.text = encode(text);
  }

  protected TextMessage()
  {
  }

  public String getText()
  {
    return decode(text);
  }
}

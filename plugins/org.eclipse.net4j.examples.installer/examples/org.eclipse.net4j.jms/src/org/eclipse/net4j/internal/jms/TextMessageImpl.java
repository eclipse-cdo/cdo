/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import java.io.IOException;

public class TextMessageImpl extends MessageImpl implements TextMessage
{
  private String text;

  public TextMessageImpl()
  {
  }

  public TextMessageImpl(String text)
  {
    this.text = text;
  }

  @Override
  public String getText()
  {
    return text;
  }

  @Override
  public void setText(String text)
  {
    this.text = text;
  }

  @Override
  public void populate(Message source) throws JMSException
  {
    super.populate(source);
    TextMessage text = (TextMessage)source;
    setText(text.getText());
  }

  @Override
  public void write(ExtendedDataOutputStream out) throws IOException
  {
    super.write(out);
    out.writeString(text);
  }

  @Override
  public void read(ExtendedDataInputStream in) throws IOException
  {
    super.read(in);
    text = in.readString();
  }
}

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

import org.eclipse.net4j.internal.jms.bundle.OM;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectMessageImpl extends MessageImpl implements ObjectMessage
{
  private Serializable object;

  public ObjectMessageImpl()
  {
  }

  public ObjectMessageImpl(Serializable object)
  {
    this.object = object;
  }

  @Override
  public Serializable getObject()
  {
    return object;
  }

  @Override
  public void setObject(Serializable object)
  {
    this.object = object;
  }

  @Override
  public void populate(Message source) throws JMSException
  {
    super.populate(source);
    ObjectMessage object = (ObjectMessage)source;
    setObject(object.getObject());
  }

  @Override
  public void write(ExtendedDataOutputStream out) throws IOException
  {
    super.write(out);
    if (object != null)
    {
      out.writeBoolean(true);
      ObjectOutputStream stream = new ObjectOutputStream(out);
      stream.writeObject(object);
    }
    else
    {
      out.writeBoolean(false);
    }
  }

  @Override
  public void read(ExtendedDataInputStream in) throws IOException
  {
    super.read(in);
    boolean notNull = in.readBoolean();
    if (notNull)
    {
      try
      {
        ObjectInputStream stream = new ObjectInputStream(in);
        object = (Serializable)stream.readObject();
      }
      catch (ClassNotFoundException ex)
      {
        OM.LOG.error(ex);
        throw new IOException(ex.getMessage());
      }
    }
  }
}

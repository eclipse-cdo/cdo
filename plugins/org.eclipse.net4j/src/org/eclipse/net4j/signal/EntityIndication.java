/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringCompressor;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 4.20
 */
public class EntityIndication extends IndicationWithResponse
{
  private int opcode;

  private String namespace;

  private String[] names;

  public EntityIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_ENTITY);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    opcode = in.readVarInt();

    switch (opcode)
    {
    case EntityRequest.OPCODE_QUERY_ENTITIES_BY_NAMES:
      namespace = readCompressedString(in);
      names = readStrings(in);
      break;

    default:
      break;
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    Entity.Provider entityProvider = getEntityProvider();
    if (entityProvider == null)
    {
      out.writeBoolean(false);
      return;
    }

    out.writeBoolean(true);
    StringCompressor compressor = getStringCompressor();

    switch (opcode)
    {
    case EntityRequest.OPCODE_QUERY_ENTITIES_BY_NAMES:
      int result = 0;

      for (String name : names)
      {
        Entity entity = entityProvider.getEntity(namespace, name);
        if (entity != null)
        {
          out.writeBoolean(true);
          entity.write(out, compressor);
          ++result;
        }
        else
        {
          out.writeBoolean(false);
        }
      }

      out.writeVarInt(result);
      break;

    default:
      break;
    }
  }

  protected Entity.Provider getEntityProvider()
  {
    Object infraStructure = getProtocol().getInfraStructure();
    if (infraStructure instanceof Entity.Provider)
    {
      return (Entity.Provider)infraStructure;
    }

    return null;
  }

  protected StringCompressor getStringCompressor()
  {
    return getProtocol().getStringCompressor();
  }

  private String readCompressedString(ExtendedDataInputStream in) throws IOException
  {
    StringCompressor compressor = getStringCompressor();
    if (compressor != null)
    {
      return compressor.read(in);
    }

    return in.readString();
  }

  private static String[] readStrings(ExtendedDataInputStream in) throws IOException
  {
    int length = in.readVarInt();
    String[] strings = new String[length];

    for (int i = 0; i < length; i++)
    {
      strings[i] = in.readString();
    }

    return strings;
  }
}

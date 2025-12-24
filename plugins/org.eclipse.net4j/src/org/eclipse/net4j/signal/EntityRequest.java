/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * @author Eike Stepper
 * @since 4.20
 */
public class EntityRequest extends RequestWithConfirmation<Integer>
{
  public static final int OPCODE_QUERY_ENTITIES_BY_NAMES = 1;

  public static final int ERROR_NO_PROVIDER = -1;

  public static final int ERROR_ILLEGAL_OPCODE = -2;

  private int opcode;

  private BiConsumer<String, Entity> entityHandler;

  private String namespace;

  private String[] names;

  private EntityRequest(SignalProtocol<?> protocol, int opcode)
  {
    super(protocol, SignalProtocol.SIGNAL_ENTITY);
    this.opcode = opcode;
  }

  public EntityRequest(SignalProtocol<?> protocol, BiConsumer<String, Entity> entityHandler, String namespace, String... names)
  {
    this(protocol, OPCODE_QUERY_ENTITIES_BY_NAMES);
    this.entityHandler = entityHandler;
    this.namespace = namespace;
    this.names = names;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeVarInt(opcode);

    switch (opcode)
    {
    case OPCODE_QUERY_ENTITIES_BY_NAMES:
      writeCompressedString(out, namespace);
      writeStrings(out, names);
      break;

    default:
      break;
    }
  }

  @Override
  protected Integer confirming(ExtendedDataInputStream in) throws Exception
  {
    boolean providerExists = in.readBoolean();
    if (!providerExists)
    {
      return ERROR_NO_PROVIDER;
    }

    switch (opcode)
    {
    case OPCODE_QUERY_ENTITIES_BY_NAMES:
    {
      StringCompressor compressor = getStringCompressor();

      for (int i = 0, size = names.length; i < size; i++)
      {
        Entity entity = in.readBoolean() ? Entity.read(in, compressor) : null;
        entityHandler.accept(names[i], entity);
      }

      return in.readVarInt();
    }

    default:
      return ERROR_ILLEGAL_OPCODE;
    }
  }

  protected StringCompressor getStringCompressor()
  {
    return getProtocol().getStringCompressor();
  }

  private void writeCompressedString(ExtendedDataOutputStream out, String string) throws IOException
  {
    StringCompressor compressor = getStringCompressor();
    if (compressor != null)
    {
      compressor.write(out, string);
    }
    else
    {
      out.writeString(string);
    }
  }

  @Override
  protected String getAdditionalInfo()
  {
    return MessageFormat.format("opcode={0}, namespace={1}, names={2}", opcode, namespace, Arrays.asList(names));
  }

  private static void writeStrings(ExtendedDataOutputStream out, String... strings) throws IOException
  {
    out.writeVarInt(strings.length);

    for (String string : strings)
    {
      out.writeString(string);
    }
  }
}

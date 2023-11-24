/*
 * Copyright (c) 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security.operations;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public final class AuthorizableOperationImpl
{
  private static final ClassLoader CLASS_LOADER = OM.BUNDLE.getClass().getClassLoader();

  private final String id;

  private final Map<String, Object> parameters = new HashMap<>();

  public AuthorizableOperationImpl(String id)
  {
    CheckUtil.checkArg(id, "id"); //$NON-NLS-1$
    this.id = id;
  }

  public AuthorizableOperationImpl(ExtendedDataInput in) throws IOException
  {
    id = in.readString();

    int size = in.readVarInt();
    for (int i = 0; i < size; i++)
    {
      String key = in.readString();

      Object value = in.readObject(CLASS_LOADER);
      parameters.put(key, value);
    }
  }

  public final String getID()
  {
    return id;
  }

  public AuthorizableOperationImpl parameter(String key, Object value)
  {
    CheckUtil.checkArg(key, "key"); //$NON-NLS-1$
    CheckUtil.checkArg(value, "value"); //$NON-NLS-1$

    parameters.put(key, value);
    return this;
  }

  public Object getParameter(String key)
  {
    return parameters.get(key);
  }

  public Map<String, Object> getParameters()
  {
    return Collections.unmodifiableMap(parameters);
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeString(id);
    out.writeVarInt(parameters.size());

    for (Map.Entry<String, Object> entry : parameters.entrySet())
    {
      out.writeString(entry.getKey());
      out.writeObject(entry.getValue());
    }
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (!(obj instanceof AuthorizableOperationImpl))
    {
      return false;
    }

    AuthorizableOperationImpl other = (AuthorizableOperationImpl)obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public String toString()
  {
    if (parameters != null)
    {
      return "AuthorizableOperation[" + id + ", parameters=" + parameters + "]";
    }

    return "AuthorizableOperation[" + id + "]";
  }
}

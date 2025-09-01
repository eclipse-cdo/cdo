/*
 * Copyright (c) 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
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
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public final class AuthorizableOperation
{
  private static final ClassLoader CLASS_LOADER = OM.BUNDLE.getClass().getClassLoader();

  private final String id;

  private final Map<String, Object> parameters;

  private AuthorizableOperation(String id, Map<String, Object> parameters)
  {
    this.id = id;
    this.parameters = parameters;
  }

  public String getID()
  {
    return id;
  }

  public Object getParameter(String key)
  {
    return parameters.get(key);
  }

  public Map<String, Object> getParameters()
  {
    return Collections.unmodifiableMap(parameters);
  }

  /**
   * @since 3.28
   */
  public boolean hasParameters()
  {
    return !parameters.isEmpty();
  }

  /**
   * @since 3.28
   */
  public AuthorizableOperation stripParameters()
  {
    return parameters.isEmpty() ? this : build(id);
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

    if (!(obj instanceof AuthorizableOperation))
    {
      return false;
    }

    AuthorizableOperation other = (AuthorizableOperation)obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public String toString()
  {
    if (parameters.isEmpty())
    {
      return "AuthorizableOperation[" + id + "]";
    }

    return "AuthorizableOperation[" + id + ", parameters=" + parameters + "]";
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

  public static AuthorizableOperation read(ExtendedDataInput in) throws IOException
  {
    String id = in.readString();
    Builder builder = builder(id);

    int size = in.readVarInt();
    for (int i = 0; i < size; i++)
    {
      String key = in.readString();

      Object value = in.readObject(CLASS_LOADER);
      builder.parameter(key, value);
    }

    return builder.build();
  }

  public static AuthorizableOperation build(String operationID)
  {
    return builder(operationID).build();
  }

  public static Builder builder(String operationID)
  {
    return new Builder(operationID);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Builder
  {
    private static final Map<String, WeakReference<AuthorizableOperation>> OPERATIONS = new HashMap<>();

    private final String id;

    private final Map<String, Object> parameters = new HashMap<>();

    public Builder(String id)
    {
      CheckUtil.checkArg(id, "id"); //$NON-NLS-1$
      this.id = id.intern();
    }

    public String id()
    {
      return id;
    }

    public Map<String, Object> parameters()
    {
      return parameters;
    }

    public Object parameter(String key)
    {
      return parameters.get(key);
    }

    public Builder parameter(String key, Object value)
    {
      CheckUtil.checkArg(key, "key"); //$NON-NLS-1$
      CheckUtil.checkArg(value, "value"); //$NON-NLS-1$

      parameters.put(key.intern(), value);
      return this;
    }

    public AuthorizableOperation build()
    {
      if (parameters.isEmpty())
      {
        AuthorizableOperation operation;

        synchronized (OPERATIONS)
        {
          WeakReference<AuthorizableOperation> ref = OPERATIONS.get(id);
          if (ref != null)
          {
            operation = ref.get();
            if (operation != null)
            {
              return operation;
            }
          }

          operation = new AuthorizableOperation(id, Collections.emptyMap());
          OPERATIONS.put(id, new WeakReference<>(operation));
        }

        return operation;
      }

      return new AuthorizableOperation(id, parameters);
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

      if (!(obj instanceof Builder))
      {
        return false;
      }

      Builder other = (Builder)obj;
      return id == other.id; // IDs are interned.
    }

    @Override
    public String toString()
    {
      if (parameters.isEmpty())
      {
        return "AuthorizableOperation.Builder[" + id + "]";
      }

      return "AuthorizableOperation.Builder[" + id + ", parameters=" + parameters + "]";
    }
  }
}

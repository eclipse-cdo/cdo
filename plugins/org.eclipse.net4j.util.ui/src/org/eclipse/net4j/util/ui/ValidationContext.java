/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public interface ValidationContext
{
  public void setValidationError(Object source, String message);

  /**
   * @author Eike Stepper
   */
  public static class Aggregator implements ValidationContext
  {
    private ValidationContext delegate;

    private Map<Object, String> messages = new WeakHashMap<>();

    public Aggregator(ValidationContext delegate)
    {
      this.delegate = delegate;
    }

    @Override
    public void setValidationError(Object source, String message)
    {
      if (message != null)
      {
        messages.put(source, message);
      }
      else
      {
        messages.remove(source);
      }

      if (delegate != null)
      {
        delegate.setValidationError(this, messages.isEmpty() ? null : formatMessage(messages));
      }
    }

    protected String formatMessage(Map<Object, String> messages)
    {
      if (messages.size() == 1)
      {
        return messages.values().iterator().next();
      }

      StringBuilder builder = new StringBuilder();
      for (String message : messages.values())
      {
        if (builder.length() != 0)
        {
          builder.append("\n");
        }

        builder.append("- " + message);
      }

      return builder.toString();
    }
  }
}

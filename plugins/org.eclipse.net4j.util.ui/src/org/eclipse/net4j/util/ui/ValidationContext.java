/*
 * Copyright (c) 2011, 2012, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.StringUtil;

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
        StringUtil.appendSeparator(builder, '\n');
        builder.append("- " + message);
      }

      return builder.toString();
    }
  }
}

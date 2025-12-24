/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.util.confirmation;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Set;

/**
 * A provider of user confirmation of some action.
 *
 * @author Christian W. Damus (CEA LIST)
 * @since 3.4
 */
public interface IConfirmationProvider
{
  /**
   * Requests confirmation of some action/operation/consequence pertaining to a
   * {@code subject} and described by a {@code message}.  Any of the non-empty
   * set of {@code acceptable} responses may be returned, and the requester
   * may optionally provide a {@code suggestion} of a suitable/safe default
   * answer.
   */
  public Confirmation confirm(String subject, String message, Set<Confirmation> acceptable, Confirmation suggestion);

  public boolean isInteractive();

  /**
   * @author Christian W. Damus (CEA LIST)
   * @since 3.4
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.confirmationProviders"; //$NON-NLS-1$

    public static final String DEFAULT_TYPE = "default"; //$NON-NLS-1$

    public static final String INTERACTIVE_TYPE = "interactive"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    /**
     * @author Christian W. Damus (CEA LIST)
     * @since 3.4
     */
    public static class Default extends Factory
    {
      public Default()
      {
        super(DEFAULT_TYPE);
      }

      @Override
      public Object create(String description) throws ProductCreationException
      {
        return new ConfirmationProvider();
      }

      private static final class ConfirmationProvider implements IConfirmationProvider
      {
        @Override
        public boolean isInteractive()
        {
          return false;
        }

        @Override
        public Confirmation confirm(String subject, String message, Set<Confirmation> acceptable, Confirmation suggestion)
        {
          // Just return the suggestion, or else the greatest of the acceptable set
          return suggestion != null ? suggestion : max(acceptable);
        }

        private Confirmation max(Set<Confirmation> confirmations)
        {
          Confirmation[] all = Confirmation.values();
          for (int i = all.length - 1; i >= 0; i--)
          {
            if (confirmations.contains(all[i]))
            {
              return all[i];
            }
          }

          return null;
        }
      }
    }
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   * @since 3.4
   */
  public static interface Provider
  {
    public IConfirmationProvider getConfirmationProvider();
  }
}

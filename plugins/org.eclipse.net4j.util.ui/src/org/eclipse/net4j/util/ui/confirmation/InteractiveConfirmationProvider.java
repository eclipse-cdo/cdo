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
package org.eclipse.net4j.util.ui.confirmation;

import org.eclipse.net4j.util.confirmation.Confirmation;
import org.eclipse.net4j.util.confirmation.IConfirmationProvider;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.widgets.Display;

import java.util.Set;

/**
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 3.4
 */
public class InteractiveConfirmationProvider implements IConfirmationProvider
{
  public InteractiveConfirmationProvider()
  {
  }

  @Override
  public boolean isInteractive()
  {
    return true;
  }

  @Override
  public Confirmation confirm(final String subject, final String message, final Set<Confirmation> acceptable, final Confirmation suggestion)
  {
    final Confirmation[] confirmation = new Confirmation[1];
    final Display display = UIUtil.getDisplay();
    display.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        confirmation[0] = ConfirmationDialog.openConfirm(UIUtil.getShell(), subject, message, acceptable, suggestion);
      }
    });

    return confirmation[0];
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   *
   * @since 3.4
   */
  public static class Factory extends IConfirmationProvider.Factory
  {
    public Factory()
    {
      super(INTERACTIVE_TYPE);
    }

    @Override
    public Object create(String description) throws ProductCreationException
    {
      return new InteractiveConfirmationProvider();
    }
  }
}

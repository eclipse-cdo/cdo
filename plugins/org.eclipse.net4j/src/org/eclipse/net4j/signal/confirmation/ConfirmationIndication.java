/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.signal.confirmation;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.ExceptionHandler;
import org.eclipse.net4j.util.confirmation.Confirmation;
import org.eclipse.net4j.util.confirmation.IConfirmationProvider;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 4.3
 */
public class ConfirmationIndication<PROTOCOL extends SignalProtocol<?> & IConfirmationProvider.Provider> extends IndicationWithMonitoring
{
  private ConfirmationPrompt prompt;

  public ConfirmationIndication(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  public ConfirmationIndication(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  public ConfirmationIndication(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  @Override
  protected int getIndicatingWorkPercent()
  {
    return 1;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    prompt = ConfirmationPrompt.read(in);
  }

  protected final ConfirmationPrompt getPrompt()
  {
    return prompt;
  }

  @Override
  protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      IConfirmationProvider confirmationProvider = getConfirmationProvider();
      if (confirmationProvider == null)
      {
        throw new IllegalStateException("No confirmation provider configured"); //$NON-NLS-1$
      }

      Confirmation confirmation = confirmationProvider.confirm(prompt.getSubject(), prompt.getMessage(), prompt.getAcceptableResponses(),
          prompt.getSuggestedResponse());
      if (confirmation == null)
      {
        throw new IllegalStateException("No confirmation provided"); //$NON-NLS-1$
      }

      out.writeBoolean(true);
      out.writeEnum(confirmation);
    }
    catch (Throwable ex)
    {
      out.writeBoolean(false);
      ExceptionHandler.Factory.handle(this, ex, "", OM.LOG);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  public IConfirmationProvider getConfirmationProvider()
  {
    return ((IConfirmationProvider.Provider)getProtocol()).getConfirmationProvider();
  }
}

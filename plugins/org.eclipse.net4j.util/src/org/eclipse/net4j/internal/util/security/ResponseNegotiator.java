/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.security;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.IChallengeResponse;
import org.eclipse.net4j.util.security.ICredentialsProvider;

import java.nio.ByteBuffer;

/**
 * @author Eike Stepper
 */
public class ResponseNegotiator extends Negotiator implements IChallengeResponse
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ResponseNegotiator.class);

  private ICredentialsProvider credentialsProvider;

  public ResponseNegotiator()
  {
    super(false);
  }

  public ICredentialsProvider getCredentialsProvider()
  {
    return credentialsProvider;
  }

  public void setCredentialsProvider(ICredentialsProvider credentialsProvider)
  {
    this.credentialsProvider = credentialsProvider;
  }

  @Override
  protected int negotiate(int phase, ByteBuffer buffer)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Negotiating phase {0}", phase);
    }

    switch (phase)
    {
    case INITIAL:
      return PHASE_RESPONSE;

    case PHASE_RESPONSE:
    case PHASE_ACKNOWLEDGE:
      break;

    default:
      break;
    }

    return 0;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (credentialsProvider == null)
    {
      throw new IllegalStateException("credentialsProvider == null");
    }
  }
}

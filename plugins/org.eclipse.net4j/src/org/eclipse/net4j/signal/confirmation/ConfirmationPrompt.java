/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.signal.confirmation;

import org.eclipse.net4j.util.confirmation.Confirmation;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 4.3
 */
public class ConfirmationPrompt
{
  private final String subject;

  private final String message;

  private final Set<Confirmation> acceptableResponses;

  private final Confirmation suggestedResponse;

  public ConfirmationPrompt(String subject, String message, Confirmation suggestedResponse, Confirmation acceptableResponse, Confirmation... more)
  {
    this(subject, message, suggestedResponse, EnumSet.of(acceptableResponse, more));
  }

  public ConfirmationPrompt(String subject, String message, Confirmation suggestedResponse, Collection<Confirmation> acceptableResponses)
  {
    this.subject = subject;
    this.message = message;
    this.acceptableResponses = EnumSet.copyOf(acceptableResponses);
    this.suggestedResponse = suggestedResponse;
  }

  public String getSubject()
  {
    return subject;
  }

  public String getMessage()
  {
    return message;
  }

  public Set<Confirmation> getAcceptableResponses()
  {
    return acceptableResponses;
  }

  public Confirmation getSuggestedResponse()
  {
    return suggestedResponse;
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeString(subject);
    out.writeString(message);
    out.writeEnum(suggestedResponse);
    out.writeInt(acceptableResponses.size());
    for (Confirmation acceptable : acceptableResponses)
    {
      out.writeEnum(acceptable);
    }
  }

  public static ConfirmationPrompt read(ExtendedDataInput in) throws IOException
  {
    String subject = in.readString();
    String message = in.readString();
    Confirmation suggestion = in.readEnum(Confirmation.class);
    EnumSet<Confirmation> acceptable = EnumSet.noneOf(Confirmation.class);
    int count = in.readInt();
    for (int i = 0; i < count; i++)
    {
      acceptable.add(in.readEnum(Confirmation.class));
    }

    return new ConfirmationPrompt(subject, message, suggestion, acceptable);
  }
}

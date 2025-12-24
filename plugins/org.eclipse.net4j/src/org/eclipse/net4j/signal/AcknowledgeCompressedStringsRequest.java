/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringCompressor;

import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public class AcknowledgeCompressedStringsRequest extends Request
{
  private final Collection<Integer> acknowledgements;

  public AcknowledgeCompressedStringsRequest(SignalProtocol<?> protocol, Collection<Integer> acknowledgements)
  {
    super(protocol, SignalProtocol.SIGNAL_ACKNOWLEDGE_COMPRESSED_STRINGS);
    this.acknowledgements = acknowledgements;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    StringCompressor compressor = getProtocol().getStringCompressor();
    compressor.writeAcknowledgements(out, acknowledgements);
  }
}

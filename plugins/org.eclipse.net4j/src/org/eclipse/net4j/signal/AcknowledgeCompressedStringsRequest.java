/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

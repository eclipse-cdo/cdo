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

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.StringCompressor;

import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public class AcknowledgeCompressedStringsIndication extends Indication
{
  public AcknowledgeCompressedStringsIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_ACKNOWLEDGE_COMPRESSED_STRINGS);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    StringCompressor compressor = getProtocol().getStringCompressor();

    Collection<Integer> acknowledgements = compressor.readAcknowledgements(in);
    compressor.processAcknowledgements(acknowledgements);
  }
}

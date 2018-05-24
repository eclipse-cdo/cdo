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

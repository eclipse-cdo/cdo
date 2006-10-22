package org.eclipse.net4j.transport.tcp;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public interface TCPSelectorListener
{
  /**
   * @author Eike Stepper
   */
  public interface Passive
  {
    public void handleAccept(TCPSelector selector, ServerSocketChannel serverSocketChannel);
  }

  /**
   * @author Eike Stepper
   */
  public interface Active
  {
    public void handleConnect(TCPSelector selector, SocketChannel channel);

    public void handleRead(TCPSelector selector, SocketChannel socketChannel);

    public void handleWrite(TCPSelector selector, SocketChannel socketChannel);
  }
}
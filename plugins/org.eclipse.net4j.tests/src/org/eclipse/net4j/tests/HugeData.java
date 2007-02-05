/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public final class HugeData
{
  public static final String NL = System.getProperty("line.separator");

  public static StringTokenizer getTokenizer()
  {
    return new StringTokenizer(getText(), NL);
  }

  public static String[] getArray()
  {
    return getText().split(NL);
  }

  public static byte[] getBytes()
  {
    return getText().getBytes();
  }

  public static String getText()
  {
    return "/***************************************************************************" + NL
        + " * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany." + NL
        + " * All rights reserved. This program and the accompanying materials" + NL
        + " * are made available under the terms of the Eclipse Public License v1.0" + NL
        + " * which accompanies this distribution, and is available at" + NL
        + " * http://www.eclipse.org/legal/epl-v10.html" + NL + " * " + NL + " * Contributors:" + NL
        + " *    Eike Stepper - initial API and implementation" + NL
        + " **************************************************************************/" + NL
        + "package org.eclipse.internal.net4j.transport.connector;" + NL + "" + NL
        + "import org.eclipse.net4j.transport.buffer.BufferProvider;" + NL
        + "import org.eclipse.net4j.transport.channel.Channel;" + NL
        + "import org.eclipse.net4j.transport.channel.Multiplexer;" + NL
        + "import org.eclipse.net4j.transport.connector.Connector;" + NL
        + "import org.eclipse.net4j.transport.connector.ConnectorException;" + NL
        + "import org.eclipse.net4j.transport.connector.Credentials;" + NL
        + "import org.eclipse.net4j.transport.connector.Protocol;" + NL
        + "import org.eclipse.net4j.transport.connector.ProtocolFactory;" + NL
        + "import org.eclipse.net4j.util.lifecycle.LifecycleListener;" + NL
        + "import org.eclipse.net4j.util.lifecycle.LifecycleNotifier;" + NL
        + "import org.eclipse.net4j.util.registry.IRegistry;" + NL + "" + NL
        + "import org.eclipse.internal.net4j.transport.channel.ChannelImpl;" + NL
        + "import org.eclipse.internal.net4j.util.stream.BufferQueue;" + NL
        + "import org.eclipse.internal.net4j.util.lifecycle.AbstractComponent;" + NL
        + "import org.eclipse.internal.net4j.util.lifecycle.LifecycleUtil;" + NL + "" + NL
        + "import java.util.ArrayList;" + NL + "import java.util.List;" + NL + "import java.util.Queue;" + NL
        + "import java.util.concurrent.ConcurrentLinkedQueue;" + NL + "import java.util.concurrent.CountDownLatch;"
        + NL + "import java.util.concurrent.ExecutorService;" + NL + "import java.util.concurrent.TimeUnit;" + NL + ""
        + NL + "/**" + NL + " * @author Eike Stepper" + NL + " */" + NL
        + "public abstract class AbstractConnector extends AbstractLifecycle implements Connector, ChannelMultiplexer"
        + NL + "{" + NL + "  private static final ChannelImpl NULL_CHANNEL = new ChannelImpl(null);" + NL + "" + NL
        + "  private ConnectorCredentials credentials;" + NL + "" + NL
        + "  private IRegistry<String, ProtocolFactory> protocolFactoryRegistry;" + NL + "" + NL
        + "  private BufferProvider bufferProvider;" + NL + "" + NL + "  /**" + NL
        + "   * An optional executor to be used by the {@link Channel}s to process their" + NL
        + "   * {@link ChannelImpl#receiveQueue} instead of the current thread. If not" + NL
        + "   * <code>null</code> the sender and the receiver peers become decoupled." + NL + "   * <p>" + NL + "   */"
        + NL + "  private ExecutorService receiveExecutor;" + NL + "" + NL + "  /**" + NL
        + "   * TODO synchronize on channels?" + NL + "   */" + NL
        + "  private List<ChannelImpl> channels = new ArrayList();" + NL + "" + NL
        + "  private State state = State.DISCONNECTED;" + NL + "" + NL + "  /**" + NL
        + "   * Don\'t initialize lazily to circumvent synchronization!" + NL + "   */" + NL
        + "  private Queue<StateListener> stateListeners = new ConcurrentLinkedQueue();" + NL + "" + NL + "  /**" + NL
        + "   * Don\'t initialize lazily to circumvent synchronization!" + NL + "   */" + NL
        + "  private Queue<ChannelListener> channelListeners = new ConcurrentLinkedQueue();" + NL + "" + NL + "  /**"
        + NL + "   * Is registered with each {@link Channel} of this {@link Connector}." + NL + "   * <p>" + NL
        + "   */" + NL + "  private LifecycleListener channelLifecycleListener = new ChannelLifecycleListener();" + NL
        + "" + NL + "  private CountDownLatch finishedConnecting;" + NL + "" + NL
        + "  private CountDownLatch finishedNegotiating;" + NL + "" + NL + "  public AbstractConnector()" + NL + "  {"
        + NL + "  }" + NL + "" + NL + "  public ExecutorService getReceiveExecutor()" + NL + "  {" + NL
        + "    return receiveExecutor;" + NL + "  }" + NL + "" + NL
        + "  public void setReceiveExecutor(ExecutorService receiveExecutor)" + NL + "  {" + NL
        + "    this.receiveExecutor = receiveExecutor;" + NL + "  }" + NL + "" + NL
        + "  public IRegistry<String, ProtocolFactory> getProtocolFactoryRegistry()" + NL + "  {" + NL
        + "    return protocolFactoryRegistry;" + NL + "  }" + NL + "" + NL
        + "  public void setProtocolFactoryRegistry(IRegistry<String, ProtocolFactory> protocolFactoryRegistry)" + NL
        + "  {" + NL + "    this.protocolFactoryRegistry = protocolFactoryRegistry;" + NL + "  }" + NL + "" + NL
        + "  public void addStateListener(StateListener listener)" + NL + "  {" + NL
        + "    stateListeners.add(listener);" + NL + "  }" + NL + "" + NL
        + "  public void removeStateListener(StateListener listener)" + NL + "  {" + NL
        + "    stateListeners.remove(listener);" + NL + "  }" + NL + "" + NL
        + "  public void addChannelListener(ChannelListener listener)" + NL + "  {" + NL
        + "    channelListeners.add(listener);" + NL + "  }" + NL + "" + NL
        + "  public void removeChannelListener(ChannelListener listener)" + NL + "  {" + NL
        + "    channelListeners.remove(listener);" + NL + "  }" + NL + "" + NL
        + "  public BufferProvider getBufferProvider()" + NL + "  {" + NL + "    return bufferProvider;" + NL + "  }"
        + NL + "" + NL + "  public void setBufferProvider(BufferProvider bufferProvider)" + NL + "  {" + NL
        + "    this.bufferProvider = bufferProvider;" + NL + "  }" + NL + "" + NL + "  public boolean isClient()" + NL
        + "  {" + NL + "    return getType() == Type.CLIENT;" + NL + "  }" + NL + "" + NL
        + "  public boolean isServer()" + NL + "  {" + NL + "    return getType() == Type.SERVER;" + NL + "  }" + NL
        + "" + NL + "  public ConnectorCredentials getCredentials()" + NL + "  {" + NL + "    return credentials;" + NL
        + "  }" + NL + "" + NL + "  public void setCredentials(ConnectorCredentials credentials)" + NL + "  {" + NL
        + "    this.credentials = credentials;" + NL + "  }" + NL + "" + NL + "  public State getState()" + NL + "  {"
        + NL + "    return state;" + NL + "  }" + NL + "" + NL
        + "  public void setState(State newState) throws ConnectorException" + NL + "  {" + NL
        + "    State oldState = getState();" + NL + "    if (newState != oldState)" + NL + "    {" + NL
        + "      System.out.println(toString() + \": Setting state \" + newState + \" (was \"" + NL
        + "          + oldState.toString().toLowerCase() + \")\");" + NL + "      state = newState;" + NL
        + "      fireStateChanged(newState, oldState);" + NL + "" + NL + "      switch (newState)" + NL + "      {"
        + NL + "      case DISCONNECTED:" + NL + "        if (finishedConnecting != null)" + NL + "        {" + NL
        + "          finishedConnecting.countDown();" + NL + "          finishedConnecting = null;" + NL + "        }"
        + NL + "" + NL + "        if (finishedNegotiating != null)" + NL + "        {" + NL
        + "          finishedNegotiating.countDown();" + NL + "          finishedNegotiating = null;" + NL
        + "        }" + NL + "        break;" + NL + "" + NL + "      case CONNECTING:" + NL
        + "        finishedConnecting = new CountDownLatch(1);" + NL
        + "        finishedNegotiating = new CountDownLatch(1);" + NL + "        if (getType() == Type.SERVER)" + NL
        + "        {" + NL + "          setState(State.NEGOTIATING);" + NL + "        }" + NL + "        break;" + NL
        + "" + NL + "      case NEGOTIATING:" + NL + "        finishedConnecting.countDown();" + NL
        + "        setState(State.CONNECTED); // TODO Implement negotiation" + NL + "        break;" + NL + "" + NL
        + "      case CONNECTED:" + NL + "        finishedConnecting.countDown(); // Just in case of suspicion" + NL
        + "        finishedNegotiating.countDown();" + NL + "        break;" + NL + "" + NL + "      }" + NL + "    }"
        + NL + "  }" + NL + "" + NL + "  public boolean isConnected()" + NL + "  {" + NL
        + "    return getState() == State.CONNECTED;" + NL + "  }" + NL + "" + NL
        + "  public void connectAsync() throws ConnectorException" + NL + "  {" + NL + "    try" + NL + "    {" + NL
        + "      activate();" + NL + "    }" + NL + "    catch (ConnectorException ex)" + NL + "    {" + NL
        + "      throw ex;" + NL + "    }" + NL + "    catch (Exception ex)" + NL + "    {" + NL
        + "      throw new ConnectorException(ex);" + NL + "    }" + NL + "  }" + NL + "" + NL
        + "  public boolean waitForConnection(long timeout) throws ConnectorException" + NL + "  {" + NL
        + "    State state = getState();" + NL + "    if (state == State.DISCONNECTED)" + NL + "    {" + NL
        + "      return false;" + NL + "    }" + NL + "" + NL + "    try" + NL + "    {" + NL
        + "      System.out.println(toString() + \": Waiting for connection...\");" + NL
        + "      return finishedNegotiating.await(timeout, TimeUnit.MILLISECONDS);" + NL + "    }" + NL
        + "    catch (InterruptedException ex)" + NL + "    {" + NL + "      return false;" + NL + "    }" + NL + "  }"
        + NL + "" + NL + "  public boolean connect(long timeout) throws ConnectorException" + NL + "  {" + NL
        + "    connectAsync();" + NL + "    return waitForConnection(timeout);" + NL + "  }" + NL + "" + NL
        + "  public ConnectorException disconnect()" + NL + "  {" + NL + "    Exception ex = deactivate();" + NL
        + "    if (ex == null)" + NL + "    {" + NL + "      return null;" + NL + "    }" + NL + "" + NL
        + "    if (ex instanceof ConnectorException)" + NL + "    {" + NL + "      return (ConnectorException)ex;" + NL
        + "    }" + NL + "" + NL + "    return new ConnectorException(ex);" + NL + "  }" + NL + "" + NL
        + "  public Channel[] getChannels()" + NL + "  {" + NL
        + "    final List<Channel> result = new ArrayList<Channel>();" + NL + "    synchronized (channels)" + NL
        + "    {" + NL + "      for (final ChannelImpl channel : channels)" + NL + "      {" + NL
        + "        if (channel != NULL_CHANNEL)" + NL + "        {" + NL + "          result.add(channel);" + NL
        + "        }" + NL + "      }" + NL + "    }" + NL + "" + NL
        + "    return result.toArray(new Channel[result.size()]);" + NL + "  }" + NL + "" + NL
        + "  public Channel openChannel() throws ConnectorException" + NL + "  {" + NL
        + "    return openChannel(null);" + NL + "  }" + NL + "" + NL
        + "  public Channel openChannel(String protocolID) throws ConnectorException" + NL + "  {" + NL
        + "    short channelIndex = findFreeChannelIndex();" + NL
        + "    ChannelImpl channel = createChannel(channelIndex, protocolID);" + NL
        + "    registerChannelWithPeer(channelIndex, protocolID);" + NL + "" + NL + "    try" + NL + "    {" + NL
        + "      channel.activate();" + NL + "    }" + NL + "    catch (ConnectorException ex)" + NL + "    {" + NL
        + "      throw ex;" + NL + "    }" + NL + "    catch (Exception ex)" + NL + "    {" + NL
        + "      throw new ConnectorException(ex);" + NL + "    }" + NL + "" + NL + "    return channel;" + NL + "  }"
        + NL + "" + NL + "  public ChannelImpl createChannel(short channelIndex, String protocolID)" + NL + "  {" + NL
        + "    Protocol protocol = createProtocol(protocolID);" + NL + "    if (protocol == null)" + NL + "    {" + NL
        + "      System.out.println(toString() + \": Opening channel without protocol\");" + NL + "    }" + NL
        + "    else" + NL + "    {" + NL
        + "      System.out.println(toString() + \": Opening channel with protocol \" + protocolID);" + NL + "    }"
        + NL + "" + NL + "    ChannelImpl channel = new ChannelImpl(receiveExecutor);" + NL
        + "    channel.setChannelIndex(channelIndex);" + NL + "    channel.setMultiplexer(this);" + NL
        + "    channel.setReceiveHandler(protocol);" + NL
        + "    channel.addLifecycleListener(channelLifecycleListener);" + NL + "    addChannel(channel);" + NL
        + "    return channel;" + NL + "  }" + NL + "" + NL + "  public ChannelImpl getChannel(short channelIndex)"
        + NL + "  {" + NL + "    try" + NL + "    {" + NL + "      ChannelImpl channel = channels.get(channelIndex);"
        + NL + "      if (channel == null || channel == NULL_CHANNEL)" + NL + "      {" + NL
        + "        throw new NullPointerException();" + NL + "      }" + NL + "" + NL + "      return channel;" + NL
        + "    }" + NL + "    catch (IndexOutOfBoundsException ex)" + NL + "    {" + NL
        + "      System.out.println(toString() + \": Invalid channelIndex \" + channelIndex);" + NL
        + "      return null;" + NL + "    }" + NL + "  }" + NL + "" + NL
        + "  protected List<BufferQueue> getChannelBufferQueues()" + NL + "  {" + NL
        + "    final List<BufferQueue> result = new ArrayList<BufferQueue>();" + NL + "    synchronized (channels)"
        + NL + "    {" + NL + "      for (final ChannelImpl channel : channels)" + NL + "      {" + NL
        + "        if (channel != NULL_CHANNEL)" + NL + "        {" + NL
        + "          BufferQueue bufferQueue = channel.getSendQueue();" + NL + "          result.add(bufferQueue);"
        + NL + "        }" + NL + "      }" + NL + "    }" + NL + "" + NL + "    return result;" + NL + "  }" + NL + ""
        + NL + "  protected short findFreeChannelIndex()" + NL + "  {" + NL + "    synchronized (channels)" + NL
        + "    {" + NL + "      int size = channels.size();" + NL + "      for (short i = 0; i < size; i++)" + NL
        + "      {" + NL + "        if (channels.get(i) == NULL_CHANNEL)" + NL + "        {" + NL
        + "          return i;" + NL + "        }" + NL + "      }" + NL + "" + NL
        + "      channels.add(NULL_CHANNEL);" + NL + "      return (short)size;" + NL + "    }" + NL + "  }" + NL + ""
        + NL + "  protected void addChannel(ChannelImpl channel)" + NL + "  {" + NL
        + "    short channelIndex = channel.getChannelIndex();" + NL + "    while (channelIndex >= channels.size())"
        + NL + "    {" + NL + "      channels.add(NULL_CHANNEL);" + NL + "    }" + NL + "" + NL
        + "    channels.set(channelIndex, channel);" + NL + "  }" + NL + "" + NL
        + "  protected void removeChannel(ChannelImpl channel)" + NL + "  {" + NL
        + "    channel.removeLifecycleListener(channelLifecycleListener);" + NL
        + "    int channelIndex = channel.getChannelIndex();" + NL + "" + NL
        + "    System.out.println(toString() + \": Removing channel \" + channelIndex);" + NL
        + "    channels.set(channelIndex, NULL_CHANNEL);" + NL + "  }" + NL + "" + NL
        + "  protected Protocol createProtocol(String protocolID)" + NL + "  {" + NL
        + "    if (protocolID == null || protocolID.length() == 0)" + NL + "    {" + NL + "      return null;" + NL
        + "    }" + NL + "" + NL + "    IRegistry<String, ProtocolFactory> registry = getProtocolFactoryRegistry();"
        + NL + "    if (registry == null)" + NL + "    {" + NL + "      return null;" + NL + "    }" + NL + "" + NL
        + "    ProtocolFactory factory = registry.lookup(protocolID);" + NL + "    if (factory == null)" + NL + "    {"
        + NL + "      return null;" + NL + "    }" + NL + "" + NL
        + "    System.out.println(toString() + \": Creating protocol \" + protocolID);" + NL
        + "    return factory.createProtocol();" + NL + "  }" + NL + "" + NL
        + "  protected void fireChannelOpened(Channel channel)" + NL + "  {" + NL
        + "    for (ChannelListener listener : channelListeners)" + NL + "    {" + NL + "      try" + NL + "      {"
        + NL + "        listener.notifyChannelOpened(channel);" + NL + "      }" + NL + "      catch (Exception ex)"
        + NL + "      {" + NL + "        ex.printStackTrace();" + NL + "      }" + NL + "    }" + NL + "  }" + NL + ""
        + NL + "  protected void fireChannelClosing(Channel channel)" + NL + "  {" + NL
        + "    for (ChannelListener listener : channelListeners)" + NL + "    {" + NL + "      try" + NL + "      {"
        + NL + "        listener.notifyChannelClosing(channel);" + NL + "      }" + NL + "      catch (Exception ex)"
        + NL + "      {" + NL + "        ex.printStackTrace();" + NL + "      }" + NL + "    }" + NL + "  }" + NL + ""
        + NL + "  protected void fireStateChanged(State newState, State oldState)" + NL + "  {" + NL
        + "    for (StateListener listener : stateListeners)" + NL + "    {" + NL + "      try" + NL + "      {" + NL
        + "        listener.notifyStateChanged(this, newState, oldState);" + NL + "      }" + NL
        + "      catch (Exception ex)" + NL + "      {" + NL + "        ex.printStackTrace();" + NL + "      }" + NL
        + "    }" + NL + "  }" + NL + "" + NL + "  @Override" + NL
        + "  protected void onAccessBeforeActivate() throws Exception" + NL + "  {" + NL
        + "    super.onAccessBeforeActivate();" + NL + "    if (bufferProvider == null)" + NL + "    {" + NL
        + "      throw new IllegalStateException(\"bufferProvider == null\");" + NL + "    }" + NL + "" + NL
        + "    if (protocolFactoryRegistry == null)" + NL + "    {" + NL
        + "      System.out.println(toString() + \": (INFO) protocolFactoryRegistry == null\");" + NL + "    }" + NL
        + "" + NL + "    if (receiveExecutor == null)" + NL + "    {" + NL
        + "      System.out.println(toString() + \": (INFO) receiveExecutor == null\");" + NL + "    }" + NL + "  }"
        + NL + "" + NL + "  @Override" + NL + "  protected void onActivate() throws Exception" + NL + "  {" + NL
        + "    super.onActivate();" + NL + "    setState(State.CONNECTING);" + NL + "  }" + NL + "" + NL
        + "  @Override" + NL + "  protected void onDeactivate() throws Exception" + NL + "  {" + NL
        + "    setState(State.DISCONNECTED);" + NL + "    for (short i = 0; i < channels.size(); i++)" + NL + "    {"
        + NL + "      ChannelImpl channel = channels.get(i);" + NL + "      if (channel != null)" + NL + "      {" + NL
        + "        LifecycleUtil.deactivate(channel);" + NL + "      }" + NL + "    }" + NL + "" + NL
        + "    channels.clear();" + NL + "    super.onDeactivate();" + NL + "  }" + NL + "" + NL
        + "  protected abstract void registerChannelWithPeer(short channelIndex, String protocolID)" + NL
        + "      throws ConnectorException;" + NL + "" + NL + "  /**" + NL
        + "   * Is registered with each {@link Channel} of this {@link Connector}." + NL + "   * <p>" + NL + "   * "
        + NL + "   * @author Eike Stepper" + NL + "   */" + NL
        + "  private final class ChannelLifecycleListener implements LifecycleListener" + NL + "  {" + NL
        + "    public void notifyLifecycleActivated(LifecycleNotifier notifier)" + NL + "    {" + NL
        + "      ChannelImpl channel = (ChannelImpl)notifier;" + NL + "      fireChannelOpened(channel);" + NL
        + "    }" + NL + "" + NL + "    public void notifyLifecycleDeactivating(LifecycleNotifier notifier)" + NL
        + "    {" + NL + "      ChannelImpl channel = (ChannelImpl)notifier;" + NL
        + "      fireChannelClosing(channel);" + NL + "      removeChannel(channel);" + NL + "    }" + NL + "  }" + NL
        + "}" + NL;
  }
}

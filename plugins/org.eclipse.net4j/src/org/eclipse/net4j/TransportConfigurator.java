/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2016, 2019-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.wrapping.StreamWrapperInjector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IStreamWrapper;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.security.NegotiatorFactory;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.spi.net4j.Acceptor;
import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.InternalChannelMultiplexer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads an XML config file and creates, wires and starts the configured {@link IAcceptor acceptors}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class TransportConfigurator implements IManagedContainerProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TransportConfigurator.class);

  private IManagedContainer container;

  public TransportConfigurator(IManagedContainer container)
  {
    this.container = container;
  }

  @Override
  public IManagedContainer getContainer()
  {
    return container;
  }

  public IAcceptor[] configure(File configFile) throws ParserConfigurationException, SAXException, IOException, CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Configuring Net4j server from " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    List<IAcceptor> acceptors = new ArrayList<>();
    Document document = getDocument(configFile);

    NodeList acceptorConfigs = document.getElementsByTagName("acceptor"); //$NON-NLS-1$
    for (int i = 0; i < acceptorConfigs.getLength(); i++)
    {
      Element acceptorConfig = (Element)acceptorConfigs.item(i);
      IAcceptor acceptor = configureAcceptor(acceptorConfig);
      acceptors.add(acceptor);
    }

    NodeList streamWrapperConfigs = document.getElementsByTagName("streamWrapper"); //$NON-NLS-1$
    for (int i = 0; i < streamWrapperConfigs.getLength(); i++)
    {
      Element streamWrapperConfig = (Element)streamWrapperConfigs.item(i);
      if (getLevel(streamWrapperConfig) == 2)
      {
        configureStreamWrapper(streamWrapperConfig, null);
      }
    }

    return acceptors.toArray(new IAcceptor[acceptors.size()]);
  }

  protected IAcceptor configureAcceptor(Element acceptorConfig)
  {
    String type = acceptorConfig.getAttribute("type"); //$NON-NLS-1$
    String description = acceptorConfig.getAttribute("description"); //$NON-NLS-1$
    if (StringUtil.isEmpty(description))
    {
      try
      {
        AcceptorDescriptionParser parser = (AcceptorDescriptionParser)container.getElement(AcceptorDescriptionParser.Factory.PRODUCT_GROUP, type, null, true);
        description = parser.getAcceptorDescription(acceptorConfig);
      }
      catch (FactoryNotFoundException | ProductCreationException ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }

    Acceptor acceptor = (Acceptor)container.getElement(AcceptorFactory.PRODUCT_GROUP, type, description, false);

    NodeList negotiatorConfigs = acceptorConfig.getElementsByTagName("negotiator"); //$NON-NLS-1$
    if (negotiatorConfigs.getLength() > 1)
    {
      throw new IllegalStateException("A maximum of one negotiator can be configured for acceptor " + acceptor); //$NON-NLS-1$
    }

    if (negotiatorConfigs.getLength() == 1)
    {
      Element negotiatorConfig = (Element)negotiatorConfigs.item(0);
      INegotiator negotiator = configureNegotiator(negotiatorConfig);
      acceptor.getConfig().setNegotiator(negotiator);
    }

    NodeList streamWrapperConfigs = acceptorConfig.getElementsByTagName("streamWrapper"); //$NON-NLS-1$
    for (int i = 0; i < streamWrapperConfigs.getLength(); i++)
    {
      Element streamWrapperConfig = (Element)streamWrapperConfigs.item(i);
      if (getLevel(streamWrapperConfig) == 3)
      {
        configureStreamWrapper(streamWrapperConfig, acceptor);
      }
    }

    OM.LOG.info("Net4j acceptor starting: " + type + "://" + description);
    acceptor.activate();
    return acceptor;
  }

  protected INegotiator configureNegotiator(Element negotiatorConfig)
  {
    String type = negotiatorConfig.getAttribute("type"); //$NON-NLS-1$
    String description = negotiatorConfig.getAttribute("description"); //$NON-NLS-1$
    return (INegotiator)container.getElement(NegotiatorFactory.PRODUCT_GROUP, type, description);
  }

  /**
   * @since 4.5
   */
  protected void configureStreamWrapper(Element streamWrapperConfig, Acceptor acceptor)
  {
    String type = streamWrapperConfig.getAttribute("type");//$NON-NLS-1$
    String description = streamWrapperConfig.getAttribute("description"); //$NON-NLS-1$
    String protocolName = streamWrapperConfig.getAttribute("protocol");//$NON-NLS-1$

    IStreamWrapper streamWrapper = (IStreamWrapper)container.getElement(IStreamWrapper.Factory.PRODUCT_GROUP, type, description);
    if (streamWrapper != null)
    {
      IElementProcessor injector = new AcceptorStreamWrapperInjector(protocolName, acceptor, streamWrapper);
      container.addPostProcessor(injector);
    }
  }

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  protected Element getStoreConfig(Element repositoryConfig)
  {
    NodeList storeConfigs = repositoryConfig.getElementsByTagName("store"); //$NON-NLS-1$
    if (storeConfigs.getLength() != 1)
    {
      String repositoryName = repositoryConfig.getAttribute("name"); //$NON-NLS-1$
      throw new IllegalStateException("Exactly one store must be configured for repository " + repositoryName); //$NON-NLS-1$
    }

    return (Element)storeConfigs.item(0);
  }

  public static Map<String, String> getProperties(Element element, int levels)
  {
    Map<String, String> properties = new HashMap<>();
    collectProperties(element, "", properties, levels); //$NON-NLS-1$
    return properties;
  }

  private static void collectProperties(Element element, String prefix, Map<String, String> properties, int levels)
  {
    if ("property".equals(element.getNodeName())) //$NON-NLS-1$
    {
      String name = element.getAttribute("name"); //$NON-NLS-1$
      String value = element.getAttribute("value"); //$NON-NLS-1$
      properties.put(prefix + name, value);
      prefix += name + "."; //$NON-NLS-1$
    }

    if (levels > 0)
    {
      NodeList childNodes = element.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++)
      {
        Node childNode = childNodes.item(i);
        if (childNode instanceof Element)
        {
          collectProperties((Element)childNode, prefix, properties, levels - 1);
        }
      }
    }
  }

  private static int getLevel(Node node)
  {
    Node parentNode = node.getParentNode();
    return parentNode != null ? getLevel(parentNode) + 1 : 0;
  }

  /**
   * @author Eike Stepper
   * @since 4.10
   */
  public interface AcceptorDescriptionParser
  {
    public String getAcceptorDescription(Element acceptorConfig);

    /**
     * @author Eike Stepper
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.net4j.acceptorDescriptionParsers"; //$NON-NLS-1$

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract AcceptorDescriptionParser create(String description) throws ProductCreationException;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AcceptorStreamWrapperInjector extends StreamWrapperInjector
  {
    private final IAcceptor acceptor;

    public AcceptorStreamWrapperInjector(String protocolID, IAcceptor acceptor, IStreamWrapper streamWrapper)
    {
      super(protocolID, streamWrapper);
      this.acceptor = acceptor;
    }

    @Override
    protected boolean shouldInject(IManagedContainer container, String productGroup, String factoryType, String description, SignalProtocol<?> signalProtocol)
    {
      if (super.shouldInject(container, productGroup, factoryType, description, signalProtocol))
      {
        if (acceptor == null)
        {
          return true;
        }

        IChannelMultiplexer multiplexer = InternalChannelMultiplexer.CONTEXT_MULTIPLEXER.get();
        if (multiplexer instanceof IServerConnector)
        {
          IServerConnector serverConnector = (IServerConnector)multiplexer;
          if (serverConnector.getAcceptor() == acceptor)
          {
            return true;
          }
        }
      }

      return false;
    }
  }
}

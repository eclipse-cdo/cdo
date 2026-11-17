/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.StringConverter;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.XMLUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.SingletonFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.regex.Pattern;

/**
 * Tests standard and nested StringConverter expression evaluation.
 *
 * @author Eike Stepper
 */
public class StringUtilConvertTest extends AbstractOMTest
{
  private static final String TRANSPORT_PROPERTY = "transport"; //$NON-NLS-1$

  private static final String IF_NAMESPACE = "urn:org.eclipse.net4j:xml:if"; //$NON-NLS-1$

  private IManagedContainer container;

  @Override
  protected void doSetUp() throws Exception
  {
    container = ContainerUtil.createInitializedContainer();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(container);
  }

  public void testOrdinaryExpressionsAndPatternOverload() throws Exception
  {
    assertEquals("before FOO after", StringUtil.convert("before $$$upper(foo)$$$ after", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$unknown(foo)$$$", StringUtil.convert("$$$unknown(foo)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$

    Pattern pattern = Pattern.compile("\\$\\$\\$([^(]+):([^$]*)\\$\\$\\$"); //$NON-NLS-1$
    assertEquals("FOO", StringUtil.convert("$$$upper:foo$$$", container, pattern)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testNestedAndIndependentExpressions() throws Exception
  {
    assertEquals("true", StringUtil.convert("$$$equalsIgnoreCase($$$upper(ws)$$$,WS)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equalsIgnoreCase($$$upper(ws)$$$, $$$lower(WS)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("VALUE", StringUtil.convert("$$$upper($$$lower($$$upper(value)$$$)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("A-b-C", StringUtil.convert("$$$upper(a)$$$-$$$lower(B)$$$-$$$upper(c)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testUnknownAndMalformedExpressions() throws Exception
  {
    assertEquals("$$$unknown(FOO)$$$", StringUtil.convert("$$$unknown($$$upper(foo)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo", StringUtil.convert("$$$upper(foo", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo)$$", StringUtil.convert("$$$upper(foo)$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper foo$$$", StringUtil.convert("$$$upper foo$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo) and bar", StringUtil.convert("$$$upper(foo) and $$$lower(BAR)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo and bar", StringUtil.convert("$$$upper(foo and $$$lower(BAR)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$equalsIgnoreCase($$$upper(foo) and bar", //$NON-NLS-1$
        StringUtil.convert("$$$equalsIgnoreCase($$$upper(foo) and $$$lower(BAR)$$$", container)); //$NON-NLS-1$
  }

  public void testConverterResultsAreNotConvertedAgain() throws Exception
  {
    container.registerFactory(new SingletonFactory(StringConverter.PRODUCT_GROUP, "returnsExpression", //$NON-NLS-1$
        (StringConverter)value -> "$$$upper(foo)$$$")); //$NON-NLS-1$

    assertEquals("$$$upper(foo)$$$", StringUtil.convert("$$$returnsExpression(value)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testSystemPropertyDefaultSemantics() throws Exception
  {
    String previousValue = System.getProperty(TRANSPORT_PROPERTY);
    try
    {
      System.clearProperty(TRANSPORT_PROPERTY);
      assertEquals("fallback", StringUtil.convert("$$$system_property(" + TRANSPORT_PROPERTY + ",fallback)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$

      System.setProperty(TRANSPORT_PROPERTY, ""); //$NON-NLS-1$
      assertEquals("", StringUtil.convert("$$$system_property(" + TRANSPORT_PROPERTY + ",fallback)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$

      System.setProperty(TRANSPORT_PROPERTY, "false"); //$NON-NLS-1$
      assertEquals("false", StringUtil.convert("$$$system_property(" + TRANSPORT_PROPERTY + ",fallback)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals("false", StringUtil.convert("$$$system_property(" + TRANSPORT_PROPERTY + ")$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    }
    finally
    {
      if (previousValue == null)
      {
        System.clearProperty(TRANSPORT_PROPERTY);
      }
      else
      {
        System.setProperty(TRANSPORT_PROPERTY, previousValue);
      }
    }
  }

  public void testNestedSystemPropertyDefaultForConditionalTransport() throws Exception
  {
    String previousValue = System.getProperty(TRANSPORT_PROPERTY);
    try
    {
      System.clearProperty(TRANSPORT_PROPERTY);
      assertConditionalTransportSelected("tcp"); //$NON-NLS-1$
      assertEquals("true", convertTransportCondition("tcp")); //$NON-NLS-1$
      assertEquals("false", convertTransportCondition("ws")); //$NON-NLS-1$

      System.setProperty(TRANSPORT_PROPERTY, "ws"); //$NON-NLS-1$
      assertConditionalTransportSelected("ws"); //$NON-NLS-1$
      assertEquals("false", convertTransportCondition("tcp")); //$NON-NLS-1$
      assertEquals("true", convertTransportCondition("ws")); //$NON-NLS-1$
    }
    finally
    {
      if (previousValue == null)
      {
        System.clearProperty(TRANSPORT_PROPERTY);
      }
      else
      {
        System.setProperty(TRANSPORT_PROPERTY, previousValue);
      }
    }
  }

  private String convertTransportCondition(String transport)
  {
    String expression = "$$$equalsIgnoreCase($$$system_property(" + TRANSPORT_PROPERTY + ",tcp)$$$," + transport + ")$$$"; //$NON-NLS-1$ //$NON-NLS-2$
    return StringUtil.convert(expression, container);
  }

  private void assertConditionalTransportSelected(String expectedTransport) throws Exception
  {
    Document document = XMLUtil.createDocumentBuilder().newDocument();
    Element root = document.createElement("configuration"); //$NON-NLS-1$
    root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:if", IF_NAMESPACE); //$NON-NLS-1$ //$NON-NLS-2$
    document.appendChild(root);

    String[] transports = { "tcp", "ssl", "ws", "wss" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    for (String transport : transports)
    {
      Element acceptor = document.createElement("acceptor"); //$NON-NLS-1$
      acceptor.setAttribute("type", transport); //$NON-NLS-1$
      String condition = "$$$equalsIgnoreCase($$$system_property(" + TRANSPORT_PROPERTY + ",tcp)$$$," + transport + ")$$$"; //$NON-NLS-1$ //$NON-NLS-2$
      acceptor.setAttributeNS(IF_NAMESPACE, "if:true", condition); //$NON-NLS-1$
      root.appendChild(acceptor);
    }

    XMLUtil.preprocessConditions(document, null, container);
    NodeList acceptors = root.getElementsByTagName("acceptor"); //$NON-NLS-1$
    assertEquals(1, acceptors.getLength());
    assertEquals(expectedTransport, ((Element)acceptors.item(0)).getAttribute("type")); //$NON-NLS-1$
  }
}

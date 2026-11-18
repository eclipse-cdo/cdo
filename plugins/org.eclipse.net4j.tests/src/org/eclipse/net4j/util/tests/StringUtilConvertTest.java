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
import org.eclipse.net4j.util.factory.IFactory;
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

  private static final String FIRST_PROPERTY = "org.eclipse.net4j.tests.stringConverter.first"; //$NON-NLS-1$

  private static final String SECOND_PROPERTY = "org.eclipse.net4j.tests.stringConverter.second"; //$NON-NLS-1$

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
    assertEquals("true", StringUtil.convert("$$$equals_ignore_case($$$upper(ws)$$$,WS)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals_ignore_case($$$upper(ws)$$$, $$$lower(WS)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("VALUE", StringUtil.convert("$$$upper($$$lower($$$upper(value)$$$)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("A-b-C", StringUtil.convert("$$$upper(a)$$$-$$$lower(B)$$$-$$$upper(c)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testBuiltInConverterRegistrations() throws Exception
  {
    String[] names = { "equals_ignore_case", "contains", "contains_ignore_case", "starts_with", "ends_with", "and", "or", "xor", "not" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
    IFactory[] factories = new StringConverter.MetaFactory().create(null);
    boolean legacyNameRegistered = false;
    for (String name : names)
    {
      boolean registered = false;
      for (IFactory factory : factories)
      {
        registered |= name.equals(factory.getKey().getType());
      }

      assertTrue("The standalone MetaFactory registers " + name, registered); //$NON-NLS-1$
      assertNotNull("The initialized container registers " + name, container.getElementOrNull(StringConverter.PRODUCT_GROUP, name)); //$NON-NLS-1$
    }

    for (IFactory factory : factories)
    {
      legacyNameRegistered |= "equalsIgnoreCase".equals(factory.getKey().getType()); //$NON-NLS-1$
    }

    assertFalse("The legacy equalsIgnoreCase name is not registered in the standalone MetaFactory", legacyNameRegistered); //$NON-NLS-1$
    assertNull("The legacy equalsIgnoreCase name is not registered in the initialized container", //$NON-NLS-1$
        container.getElementOrNull(StringConverter.PRODUCT_GROUP, "equalsIgnoreCase")); //$NON-NLS-1$
  }

  public void testStringAndBooleanConverters() throws Exception
  {
    assertEquals("true", StringUtil.convert("$$$contains(Hello World,World)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$contains_ignore_case(Hello World,WORLD)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$starts_with(Hello World,Hello)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$ends_with(Hello World,World)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$not($$$equals(a,b)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$and($$$equals(a,a)$$$,$$$contains(abc,b)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$or(false,$$$equals(a,a)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$xor(true,true,true)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals(,)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$contains(,)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$

    assertEquals("true", StringConverter.CONTAINS.apply("Hello World,World")); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringConverter.EQUALS_IGNORE_CASE.apply("WS,ws")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testWithArgumentsDirectInvocation()
  {
    StringConverter.WithArguments equals = (StringConverter.WithArguments)StringConverter.EQUALS;
    assertEquals("true", equals.apply("a$,b,a$,b")); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", equals.apply(new String[] { "a,b", "a,b" })); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testArgumentEscaping()
  {
    assertEquals("a$,b", StringUtil.convert("a$,b", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals(a$,b,a$,b)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals($$,$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals($$$$$$,$$$$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals($($),$($))$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals($x,x)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("true", StringUtil.convert("$$$equals($$$identity(a$,b)$$$,a$,b)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testNestedResultsWithCommasKeepArgumentBoundaries()
  {
    String previousFirst = System.getProperty(FIRST_PROPERTY);
    String previousSecond = System.getProperty(SECOND_PROPERTY);
    try
    {
      System.setProperty(FIRST_PROPERTY, "a,b"); //$NON-NLS-1$
      System.setProperty(SECOND_PROPERTY, "a,b"); //$NON-NLS-1$
      String expression = "$$$equals($$$system_property(" + FIRST_PROPERTY + ")$$$,$$$system_property(" + SECOND_PROPERTY + ")$$$)$$$"; //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals("true", StringUtil.convert(expression, container)); //$NON-NLS-1$
    }
    finally
    {
      restoreProperty(FIRST_PROPERTY, previousFirst);
      restoreProperty(SECOND_PROPERTY, previousSecond);
    }
  }

  public void testInvalidBooleanValuesAndArgumentCounts()
  {
    assertIllegalArgument("$$$contains(onlyOne)$$$"); //$NON-NLS-1$
    assertIllegalArgument("$$$starts_with(a,b,c)$$$"); //$NON-NLS-1$
    assertIllegalArgument("$$$and(true)$$$"); //$NON-NLS-1$
    assertIllegalArgument("$$$or(true,false,perhaps)$$$"); //$NON-NLS-1$
    assertIllegalArgument("$$$xor(true,)$$$"); //$NON-NLS-1$
    assertIllegalArgument("$$$not(true,false)$$$"); //$NON-NLS-1$
  }

  public void testUnknownAndMalformedExpressions() throws Exception
  {
    assertEquals("$$$unknown(FOO)$$$", StringUtil.convert("$$$unknown($$$upper(foo)$$$)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$unknown(a$,b)$$$", StringUtil.convert("$$$unknown(a$,b)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$unknown(FOO,a$,b)$$$", StringUtil.convert("$$$unknown($$$upper(foo)$$$,a$,b)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo", StringUtil.convert("$$$upper(foo", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo)$$", StringUtil.convert("$$$upper(foo)$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper foo$$$", StringUtil.convert("$$$upper foo$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo) and bar", StringUtil.convert("$$$upper(foo) and $$$lower(BAR)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$upper(foo and bar", StringUtil.convert("$$$upper(foo and $$$lower(BAR)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("$$$equals_ignore_case($$$upper(foo) and bar", //$NON-NLS-1$
        StringUtil.convert("$$$equals_ignore_case($$$upper(foo) and $$$lower(BAR)$$$", container)); //$NON-NLS-1$
  }

  public void testConverterResultsAreNotConvertedAgain() throws Exception
  {
    container.registerFactory(new SingletonFactory(StringConverter.PRODUCT_GROUP, "returnsExpression", //$NON-NLS-1$
        (StringConverter)value -> "$$$upper(foo)$$$")); //$NON-NLS-1$
    container.registerFactory(new SingletonFactory(StringConverter.PRODUCT_GROUP, "returnsEscapedText", //$NON-NLS-1$
        (StringConverter)value -> "a$,b")); //$NON-NLS-1$
    container.registerFactory(new SingletonFactory(StringConverter.PRODUCT_GROUP, "acceptsString", //$NON-NLS-1$
        (StringConverter)value -> value instanceof String ? value.toString() : "not a String")); //$NON-NLS-1$

    assertEquals("$$$upper(foo)$$$", StringUtil.convert("$$$returnsExpression(value)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("a$,b", StringUtil.convert("$$$returnsEscapedText()$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("a,b", StringUtil.convert("$$$acceptsString(a,b)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void testUnknownConverterEscapesSyntacticCharactersFromNestedResults() throws Exception
  {
    container.registerFactory(new SingletonFactory(StringConverter.PRODUCT_GROUP, "returnsSyntax", //$NON-NLS-1$
        (StringConverter)value -> "a,b()$")); //$NON-NLS-1$

    String expression = "$$$unknown($$$returnsSyntax(value)$$$)$$$"; //$NON-NLS-1$
    String converted = StringUtil.convert(expression, container);
    assertEquals("$$$unknown(a$,b$($)$$)$$$", converted); //$NON-NLS-1$
    assertEquals(converted, StringUtil.convert(converted, container));
  }

  public void testSystemPropertyDefaultSemantics() throws Exception
  {
    String previousValue = System.getProperty(TRANSPORT_PROPERTY);
    try
    {
      System.clearProperty(TRANSPORT_PROPERTY);
      assertEquals("fallback", StringUtil.convert("$$$system_property(" + TRANSPORT_PROPERTY + ",fallback)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals("fallback,value", StringUtil.convert("$$$system_property(" + TRANSPORT_PROPERTY + ",fallback,value)$$$", container)); //$NON-NLS-1$ //$NON-NLS-2$

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

  private void assertIllegalArgument(String expression)
  {
    try
    {
      StringUtil.convert(expression, container);
      fail("Expected an IllegalArgumentException for " + expression); //$NON-NLS-1$
    }
    catch (IllegalArgumentException ex)
    {
      assertTrue(ex.getMessage().contains("Converter")); //$NON-NLS-1$
    }
  }

  private static void restoreProperty(String name, String value)
  {
    if (value == null)
    {
      System.clearProperty(name);
    }
    else
    {
      System.setProperty(name, value);
    }
  }

  private String convertTransportCondition(String transport)
  {
    String expression = "$$$equals_ignore_case($$$system_property(" + TRANSPORT_PROPERTY + ",tcp)$$$," + transport + ")$$$"; //$NON-NLS-1$ //$NON-NLS-2$
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
      String condition = "$$$equals_ignore_case($$$system_property(" + TRANSPORT_PROPERTY + ",tcp)$$$," + transport + ")$$$"; //$NON-NLS-1$ //$NON-NLS-2$
      acceptor.setAttributeNS(IF_NAMESPACE, "if:true", condition); //$NON-NLS-1$
      root.appendChild(acceptor);
    }

    XMLUtil.preprocessConditions(document, null, container);
    NodeList acceptors = root.getElementsByTagName("acceptor"); //$NON-NLS-1$
    assertEquals(1, acceptors.getLength());
    assertEquals(expectedTransport, ((Element)acceptors.item(0)).getAttribute("type")); //$NON-NLS-1$
  }
}

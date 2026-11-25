/*
 * Copyright (c) 2014, 2016, 2023, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 * @since 3.4
 */
public final class XMLUtil
{
  private static final String IF_NAMESPACE = "urn:org.eclipse.net4j:xml:if"; //$NON-NLS-1$

  private static final String UNLESS_NAMESPACE = "urn:org.eclipse.net4j:xml:unless"; //$NON-NLS-1$

  private static final Pattern SYSTEM_PROPERTY_CONVERTER = Pattern.compile("\\$\\$\\$system_property\\((.*)\\)\\$\\$\\$"); //$NON-NLS-1$

  private XMLUtil()
  {
  }

  public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException
  {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    documentBuilderFactory.setValidating(false);
    return documentBuilderFactory.newDocumentBuilder();
  }

  /**
   * Creates a namespace-aware parser that rejects DTD declarations and disables external entities, XInclude, and entity
   * expansion.
   *
   * @return a secure non-validating document builder
   * @throws ParserConfigurationException if the parser does not support the required security features
   * @since 3.31
   */
  public static DocumentBuilder createSecureDocumentBuilder() throws ParserConfigurationException
  {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    documentBuilderFactory.setValidating(false);
    documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    documentBuilderFactory.setXIncludeAware(false);
    documentBuilderFactory.setExpandEntityReferences(false);
    return documentBuilderFactory.newDocumentBuilder();
  }

  public static Element loadRootElement(DocumentBuilder documentBuilder, File file) throws Exception
  {
    Document document = loadDocument(documentBuilder, file);
    return document.getDocumentElement();
  }

  public static Document loadDocument(DocumentBuilder documentBuilder, File file) throws SAXException, IOException
  {
    return documentBuilder.parse(file);
  }

  public static int handleElements(NodeList nodeList, ElementHandler handler) throws Exception
  {
    int count = 0;
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node node = nodeList.item(i);
      if (node instanceof Element)
      {
        Element element = (Element)node;
        handler.handleElement(element);
        ++count;
      }
    }

    return count;
  }

  public static int handleChildElements(Element rootElement, ElementHandler handler) throws Exception
  {
    NodeList childNodes = rootElement.getChildNodes();
    return handleElements(childNodes, handler);
  }

  public static int handleElementsByTagName(Element rootElement, String tagName, ElementHandler handler) throws Exception
  {
    int count = 0;

    NodeList nodeList = rootElement.getElementsByTagName(tagName);
    count += handleElements(nodeList, handler);

    int pos = tagName.indexOf(':');
    if (pos != -1)
    {
      tagName = tagName.substring(pos + 1);
      nodeList = rootElement.getElementsByTagName(tagName);
      count += handleElements(nodeList, handler);
    }

    return count;
  }

  /**
   * Preprocesses conditional attributes in a namespace-aware DOM without an explicit container for resolving
   * {@link StringConverter StringConverters}. See
   * {@link #preprocessConditions(Document, Map, IManagedContainer)} for the condition syntax and processing details.
   *
   * @param document the document to preprocess
   * @param parameters the configuration parameters used by {@code true} and {@code blank}, and queried by exact key
   *          for {@code set}
   * @throws IllegalArgumentException if an operator or expanded {@code true} value is invalid, or if the root element
   *           is excluded
   * @since 3.31
   */
  public static void preprocessConditions(Document document, Map<String, String> parameters)
  {
    preprocessConditions(document, parameters, null);
  }

  /**
   * Preprocesses conditional attributes in a namespace-aware DOM.
   * <p>
   * <b>Namespace declarations and example:</b> Declare the conditional namespaces on the document element and use
   * their prefixes on condition attributes:
   *
   * <pre>
   * &lt;configuration xmlns:if="urn:org.eclipse.net4j:xml:if"
   *     xmlns:unless="urn:org.eclipse.net4j:xml:unless"&gt;
   *   &lt;endpoint if:true="@enabled"/&gt;
   *   &lt;option if:blank="@optional"/&gt;
   *   &lt;debug if:set="@debug"/&gt;
   *   &lt;release unless:true="@debug"/&gt;
   * &lt;/configuration&gt;
   * </pre>
   * <p>
   * <b>Operators and semantics:</b> The supported operators are {@code true}, {@code blank}, and {@code set}, each
   * available in both namespaces. {@code unless} negates the corresponding operator.
   * <ul>
   * <li>{@code true} expands its value using the supplied parameters and registered StringConverters, trims the result,
   * and accepts only case-insensitive {@code true} or {@code false}. An empty or whitespace-only value is false.</li>
   * <li>{@code blank} accepts an expanded null, empty, or whitespace-only value.</li>
   * <li>{@code set} tests whether a property exists, not the property's contents. An exact parameter key is set even
   * when its value is empty or {@code false}. Otherwise, the value names a JVM/OMPlatform property, which is set when
   * its value is non-null, including empty and {@code false} values. The exact expression
   * {@code $$$system_property(name)$$$} checks the named system property directly. Ordinary converter results and
   * arbitrary non-empty text do not imply that a property is set.</li>
   * </ul>
   * <p>
   * <b>Parameter and property evaluation:</b> The supplied parameters are used to expand {@code true} and
   * {@code blank} values. Registered StringConverters are resolved through the supplied container for these values.
   * For {@code set}, an exact parameter key is checked first; if it does not match, the value is interpreted as a
   * JVM/OMPlatform property name, except for the exact system-property expression described above.
   * <p>
   * <b>Combining conditions and removing subtrees:</b> Multiple conditions on one element are combined with AND.
   * Nested elements are processed only while their parents remain included. Excluding an element removes its entire
   * subtree, and all conditional attributes are removed from retained elements.
   * <p>
   * <b>Namespace awareness and ordinary attributes:</b> The DOM must have been parsed with namespace awareness enabled
   * for the namespace-qualified conditions to be recognized. This method expands values used to evaluate conditions
   * only; it does not expand ordinary attributes.
   *
   * @param document the document to preprocess
   * @param parameters the configuration parameters used by {@code true} and {@code blank}, and queried by exact key
   *          for {@code set}
   * @param container the container used to resolve StringConverters in {@code true} and {@code blank} values
   * @throws IllegalArgumentException if an operator or expanded {@code true} value is invalid, or if the root element
   *           is excluded
   * @since 3.31
   */
  public static void preprocessConditions(Document document, Map<String, String> parameters, IManagedContainer container)
  {
    Element rootElement = document.getDocumentElement();
    if (rootElement == null || !preprocessElement(rootElement, parameters, container))
    {
      throw new IllegalArgumentException("The XML document root element was excluded by a condition"); //$NON-NLS-1$
    }
  }

  private static boolean preprocessElement(Element element, Map<String, String> parameters, IManagedContainer container)
  {
    NamedNodeMap attributes = element.getAttributes();
    List<Node> conditions = new ArrayList<>();
    boolean include = true;

    for (int i = 0; i < attributes.getLength(); i++)
    {
      Node attribute = attributes.item(i);
      String namespace = attribute.getNamespaceURI();
      String localName = attribute.getLocalName();

      if (IF_NAMESPACE.equals(namespace) || UNLESS_NAMESPACE.equals(namespace))
      {
        if (!"true".equals(localName) && !"blank".equals(localName) && !"set".equals(localName))
        {
          throw new IllegalArgumentException("Unsupported XML condition operator: " + localName);
        }

        String expression = attribute.getNodeValue();
        boolean result;

        if ("true".equals(localName))
        {
          String value = expandConditionValue(expression, parameters, container);

          String trimmed = value == null ? null : value.trim();
          if (trimmed != null && !trimmed.isEmpty() && !"true".equalsIgnoreCase(trimmed) && !"false".equalsIgnoreCase(trimmed))
          {
            throw new IllegalArgumentException("Condition " + attribute.getNodeName() + " must expand to true or false, not: " + value);
          }

          result = "true".equalsIgnoreCase(trimmed);
        }
        else if ("blank".equals(localName))
        {
          String value = expandConditionValue(expression, parameters, container);
          result = value == null || value.trim().isEmpty();
        }
        else
        {
          result = isPropertySet(expression, parameters);
        }

        if (UNLESS_NAMESPACE.equals(namespace))
        {
          result = !result;
        }

        include &= result;
        conditions.add(attribute);
      }
    }

    for (Node condition : conditions)
    {
      element.removeAttributeNode((Attr)condition);
    }

    if (!include)
    {
      Node parent = element.getParentNode();
      if (parent != null)
      {
        parent.removeChild(element);
      }

      return false;
    }

    NodeList children = element.getChildNodes();

    for (int i = children.getLength() - 1; i >= 0; i--)
    {
      Node child = children.item(i);
      if (child instanceof Element)
      {
        preprocessElement((Element)child, parameters, container);
      }
    }

    return true;
  }

  private static String expandConditionValue(String expression, Map<String, String> parameters, IManagedContainer container)
  {
    return StringUtil.convert(StringUtil.replace(expression, parameters), container);
  }

  private static boolean isPropertySet(String expression, Map<String, String> parameters)
  {
    if (parameters != null && parameters.containsKey(expression))
    {
      return true;
    }

    String propertyName = expression;

    Matcher matcher = SYSTEM_PROPERTY_CONVERTER.matcher(expression.trim());
    if (matcher.matches())
    {
      propertyName = matcher.group(1);
    }

    if (propertyName.isEmpty())
    {
      return false;
    }

    return System.getProperty(propertyName) != null || OMPlatform.INSTANCE.getProperty(propertyName) != null;
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface ElementHandler
  {
    public void handleElement(Element element) throws Exception;
  }
}

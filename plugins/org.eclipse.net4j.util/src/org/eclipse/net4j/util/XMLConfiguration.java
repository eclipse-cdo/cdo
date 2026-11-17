/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A finalized, immutable snapshot of an XML document. Set parameters before calling {@link #prepare(File)} or
 * {@link #prepare(Reader)}. Preparation uses a namespace-aware, secure parser, preprocesses conditional elements,
 * expands retained element attributes, and records metadata on the resulting DOM nodes. The prepared document is fixed:
 * later parameter or converter changes do not alter its values.
 * <p>
 * Parameter keys are replaced literally wherever they occur in attribute values. The standard keys are {@code @user},
 * {@code @state}, and {@code @config}; callers may add or replace keys before preparation. Values are expanded with
 * {@link StringUtil#replace(String, Map)} and then passed through registered
 * {@link StringUtil#convert(String, IManagedContainer) StringConverters}. Conditions are evaluated before ordinary
 * attribute expansion. See {@link XMLUtil#preprocessConditions(org.w3c.dom.Document, Map)} for condition syntax.
 * <p>
 * {@link #getDocument()} returns a defensive DOM copy. Its expanded-state metadata is copied with the nodes so that
 * downstream processors can distinguish finalized values from values in an unprocessed document. This allows multiple
 * consumers to inspect the same snapshot without sharing mutable DOM nodes. File-backed configurations retain their
 * source file; Reader-backed configurations do not have a source file.
 * <p>
 * Parsing rejects DTD declarations and disables external entities, XInclude, and entity expansion. Namespace-free XML
 * documents remain valid. For example:
 * <pre>
 * &lt;configuration&gt;
 *   &lt;service endpoint="@endpoint" enabled="true"/&gt;
 * &lt;/configuration&gt;
 * </pre>
 *
 * @author Eike Stepper
 * @since 3.31
 */
public final class XMLConfiguration
{
  private static final String EXPANDED_USER_DATA_KEY = XMLConfiguration.class.getName() + ".expanded"; //$NON-NLS-1$

  private final IManagedContainer container;

  private final Map<String, String> parameters = new HashMap<>();

  private File sourceFile;

  private Document document;

  /**
   * Creates a configuration preparation context.
   *
   * @param container the container used to resolve StringConverter expressions; it may be {@code null}
   */
  public XMLConfiguration(IManagedContainer container)
  {
    this.container = container;

    parameters.put("@user", OMPlatform.INSTANCE.getUserFolder().getAbsolutePath()); //$NON-NLS-1$
    parameters.put("@state", OMPlatform.INSTANCE.getStateFolder().getAbsolutePath()); //$NON-NLS-1$
    parameters.put("@config", OMPlatform.INSTANCE.getConfigFolder().getAbsolutePath()); //$NON-NLS-1$
  }

  /**
   * Adds or replaces a preparation parameter.
   *
   * @param name the parameter name
   * @param value its value; {@code null} removes the parameter
   * @return the previous value
   * @throws IllegalStateException if preparation has completed
   */
  public String setParameter(String name, String value)
  {
    checkMutable();
    return value == null ? parameters.remove(name) : parameters.put(name, value);
  }

  /**
   * Adds or replaces preparation parameters.
   *
   * @param values the values to copy
   * @throws IllegalStateException if preparation has completed
   */
  public void setParameters(Map<String, String> values)
  {
    checkMutable();
    parameters.putAll(values);
  }

  /**
   * Replaces all preparation parameters with an exact supplied snapshot, including removing any default parameter that
   * is absent from the snapshot.
   *
   * @param values the complete parameter snapshot to copy
   * @throws IllegalStateException if preparation has completed
   * @since 3.31
   */
  public void replaceParameters(Map<String, String> values)
  {
    checkMutable();
    parameters.clear();
    parameters.putAll(values);
  }

  /**
   * Parses and prepares a file-backed configuration.
   *
   * @param file the XML source
   * @return this prepared configuration
   * @throws ParserConfigurationException if a secure parser cannot be created
   * @throws SAXException if XML is malformed or unsafe
   * @throws IOException if the file cannot be read
   */
  public XMLConfiguration prepare(File file) throws ParserConfigurationException, SAXException, IOException
  {
    checkMutable();

    try (FileInputStream input = new FileInputStream(file))
    {
      prepareDocument(XMLUtil.createSecureDocumentBuilder().parse(input));
    }

    sourceFile = file;
    return this;
  }

  /**
   * Parses and prepares a reader-backed configuration.
   *
   * @param reader the XML source
   * @return this prepared configuration
   * @throws ParserConfigurationException if a secure parser cannot be created
   * @throws SAXException if XML is malformed or unsafe
   * @throws IOException if the reader cannot be read
   */
  public XMLConfiguration prepare(Reader reader) throws ParserConfigurationException, SAXException, IOException
  {
    checkMutable();
    prepareDocument(XMLUtil.createSecureDocumentBuilder().parse(new InputSource(reader)));
    return this;
  }

  /**
   * Returns a defensive copy of the prepared document.
   *
   * @return an independent DOM copy
   * @throws IllegalStateException if preparation has not completed
   */
  public Document getDocument()
  {
    if (document == null)
    {
      throw new IllegalStateException("XML configuration has not been prepared");
    }

    Document copy = (Document)document.cloneNode(true);
    copyExpandedMetadata(document, copy);
    return copy;
  }

  /**
   * Returns the original file, if the configuration was prepared from one.
   *
   * @return source file or {@code null}
   */
  public File getSourceFile()
  {
    return sourceFile;
  }

  /**
   * Indicates whether preparation has completed.
   *
   * @return {@code true} after successful preparation
   */
  public boolean isPrepared()
  {
    return document != null;
  }

  /**
   * Returns an immutable copy of parameters used for preparation.
   *
   * @return immutable parameter snapshot
   */
  public Map<String, String> getParameters()
  {
    return Collections.unmodifiableMap(new HashMap<>(parameters));
  }

  private void checkMutable()
  {
    if (document != null)
    {
      throw new IllegalStateException("XML configuration has already been prepared");
    }
  }

  private void prepareDocument(Document parsed)
  {
    XMLUtil.preprocessConditions(parsed, parameters, container);
    expandAttributes(parsed.getDocumentElement());
    markExpanded(parsed);
    document = parsed;
  }

  private void expandAttributes(Element element)
  {
    NamedNodeMap attributes = element.getAttributes();

    for (int i = 0; i < attributes.getLength(); i++)
    {
      Node attribute = attributes.item(i);
      if (attribute.getNamespaceURI() == null || !attribute.getNodeName().startsWith("xmlns"))
      {
        String value = StringUtil.replace(attribute.getNodeValue(), parameters);
        attribute.setNodeValue(StringUtil.convert(value, container));
      }
    }

    NodeList children = element.getChildNodes();

    for (int i = 0; i < children.getLength(); i++)
    {
      if (children.item(i) instanceof Element)
      {
        expandAttributes((Element)children.item(i));
      }
    }
  }

  private static void markExpanded(Node node)
  {
    node.setUserData(EXPANDED_USER_DATA_KEY, Boolean.TRUE, null);

    if (node instanceof Element)
    {
      NamedNodeMap attributes = node.getAttributes();

      for (int i = 0; i < attributes.getLength(); i++)
      {
        markExpanded(attributes.item(i));
      }
    }

    NodeList children = node.getChildNodes();

    for (int i = 0; i < children.getLength(); i++)
    {
      markExpanded(children.item(i));
    }
  }

  private static void copyExpandedMetadata(Node source, Node target)
  {
    if (isExpanded(source))
    {
      target.setUserData(EXPANDED_USER_DATA_KEY, Boolean.TRUE, null);
    }

    if (source instanceof Element && target instanceof Element)
    {
      NamedNodeMap sourceAttributes = source.getAttributes();
      NamedNodeMap targetAttributes = target.getAttributes();

      for (int i = 0; i < sourceAttributes.getLength(); i++)
      {
        Node sourceAttribute = sourceAttributes.item(i);

        Node targetAttribute = sourceAttribute.getNamespaceURI() == null //
            ? targetAttributes.getNamedItem(sourceAttribute.getNodeName()) //
            : targetAttributes.getNamedItemNS(sourceAttribute.getNamespaceURI(), sourceAttribute.getLocalName());
        if (targetAttribute != null)
        {
          copyExpandedMetadata(sourceAttribute, targetAttribute);
        }
      }
    }

    NodeList sourceChildren = source.getChildNodes();
    NodeList targetChildren = target.getChildNodes();
    int count = Math.min(sourceChildren.getLength(), targetChildren.getLength());

    for (int i = 0; i < count; i++)
    {
      copyExpandedMetadata(sourceChildren.item(i), targetChildren.item(i));
    }
  }

  /**
   * Indicates whether a DOM node belongs to a configuration whose attributes were fully expanded during preparation.
   * The marker is attached to the document, elements, and attributes, and is preserved by {@link #getDocument()}.
   *
   * @param node the DOM node to inspect
   * @return {@code true} when the node carries finalized expansion metadata
   * @since 3.31
   */
  public static boolean isExpanded(Node node)
  {
    if (node == null)
    {
      return false;
    }

    if (Boolean.TRUE.equals(node.getUserData(EXPANDED_USER_DATA_KEY)))
    {
      return true;
    }

    if (node instanceof Attr)
    {
      Element owner = ((Attr)node).getOwnerElement();
      return owner != null && Boolean.TRUE.equals(owner.getUserData(EXPANDED_USER_DATA_KEY));
    }

    return false;
  }
}

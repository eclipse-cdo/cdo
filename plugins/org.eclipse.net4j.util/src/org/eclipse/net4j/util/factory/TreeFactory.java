/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public abstract class TreeFactory extends Factory implements ITreeFactory
{
  public TreeFactory()
  {
  }

  public TreeFactory(IFactoryKey key)
  {
    super(key);
  }

  public TreeFactory(FactoryKey key)
  {
    super(key);
  }

  public TreeFactory(String productGroup, String type)
  {
    super(productGroup, type);
  }

  public TreeFactory(String productGroup)
  {
    super(productGroup);
  }

  public Tree getTreeFor(Object product)
  {
    String description = getDescriptionFor(product);
    return parseTree(description);
  }

  @Override
  public final Object createWithTree(Tree config) throws ProductCreationException
  {
    try
    {
      return create(config);
    }
    catch (ProductCreationException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      String description = productCreationExceptionDescription(config);
      throw productCreationException(description, ex);
    }
  }

  @Override
  public Object create(String description) throws ProductCreationException
  {
    Tree tree = parseTree(description);
    return createWithTree(tree);
  }

  protected abstract Object create(Tree config) throws ProductCreationException;

  /**
   * @since 3.23
   */
  protected final ProductCreationException productCreationException(Tree config, Throwable cause)
  {
    return productCreationException(productCreationExceptionDescription(config), cause);
  }

  /**
   * @since 3.23
   */
  protected final ProductCreationException productCreationException(Tree config)
  {
    return productCreationException(productCreationExceptionDescription(config));
  }

  private String productCreationExceptionDescription(Tree config)
  {
    return "\n" + Tree.Dumper.toString(config);
  }

  public static String createDescription(Tree tree)
  {
    try
    {
      Tree.XMLConverter converter = new Tree.XMLConverter();
      tree.visit(converter, null);

      Document document = converter.document();
      return xmlSerialize(document);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Tree parseTree(String description)
  {
    Document document = xmlDeserialize(description);
    return Tree.XMLConverter.convertDocumentToTree(document);
  }

  public static Document xmlDeserialize(String description)
  {
    try
    {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(false);
      documentBuilderFactory.setValidating(false);

      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      return documentBuilder.parse(new InputSource(new StringReader(description)));
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static String xmlSerialize(Document document)
  {
    try
    {
      StringWriter writer = new StringWriter();

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      transformer.transform(new DOMSource(document), new StreamResult(writer));

      return writer.toString();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class ContainerAware extends TreeFactory implements IManagedContainerProvider, IManagedContainer.ContainerAware
  {
    private IManagedContainer container;

    public ContainerAware(IFactoryKey key)
    {
      super(key);
    }

    public ContainerAware(String productGroup, String type)
    {
      super(productGroup, type);
    }

    @Override
    public final IManagedContainer getContainer()
    {
      return container;
    }

    @Override
    public final void setManagedContainer(IManagedContainer container)
    {
      this.container = container;
    }
  }
}

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
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public final class Tree implements Comparable<Tree>
{
  public static final Tree EMPTY = new Tree(null, null, null);

  private static final String DEFAULT_CHILD_NAME = "property";

  private static final String DEFAULT_NAME_ATTRIBUTE = "name";

  private static final String DEFAULT_VALUE_ATTRIBUTE = "value";

  private final String name;

  private final Map<String, String> attributes;

  private final Tree parent;

  private List<Tree> children;

  private Tree(String name, Map<String, String> attributes, Tree parent)
  {
    this.name = name;
    this.attributes = ObjectUtil.isEmpty(attributes) ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
    this.parent = parent;
  }

  private void setChildren(List<Tree> children)
  {
    this.children = ObjectUtil.isEmpty(children) ? Collections.emptyList() : Collections.unmodifiableList(children);
  }

  public String name()
  {
    return name;
  }

  public Map<String, String> attributes()
  {
    return attributes;
  }

  public String attribute(String name)
  {
    return attributes.get(name);
  }

  public Tree parent()
  {
    return parent;
  }

  public List<Tree> children()
  {
    return children;
  }

  public void children(Consumer<Tree> consumer)
  {
    for (Tree child : children)
    {
      consumer.accept(child);
    }
  }

  public List<Tree> children(String name)
  {
    List<Tree> result = new ArrayList<>();
    children(name, result::add);
    return result;
  }

  public void children(String name, Consumer<Tree> consumer)
  {
    for (Tree child : children)
    {
      if (Objects.equals(child.name, name))
      {
        consumer.accept(child);
      }
    }
  }

  public List<Tree> allChildren()
  {
    List<Tree> result = new ArrayList<>();
    allChildren(result::add);
    return result;
  }

  public void allChildren(Consumer<Tree> consumer)
  {
    for (Tree child : children)
    {
      consumer.accept(child);
      child.allChildren(name, consumer);
    }
  }

  public List<Tree> allChildren(String name)
  {
    List<Tree> result = new ArrayList<>();
    allChildren(name, result::add);
    return result;
  }

  public void allChildren(String name, Consumer<Tree> consumer)
  {
    for (Tree child : children)
    {
      if (Objects.equals(child.name, name))
      {
        consumer.accept(child);
      }

      child.allChildren(name, consumer);
    }
  }

  public Tree child(String name)
  {
    for (Tree child : children)
    {
      if (Objects.equals(child.name, name))
      {
        return child;
      }
    }

    return null;
  }

  public Tree child(int index)
  {
    return children.get(index);
  }

  public int indexInParent()
  {
    if (parent == null)
    {
      return -1;
    }

    return parent.indexOfChild(this);
  }

  public int indexOfChild(Tree child)
  {
    if (children != null)
    {
      return children.indexOf(child);
    }

    return -1;
  }

  public Map<String, String> properties()
  {
    return properties(DEFAULT_CHILD_NAME);
  }

  public Map<String, String> properties(String childName)
  {
    return properties(childName, DEFAULT_NAME_ATTRIBUTE, DEFAULT_VALUE_ATTRIBUTE);
  }

  public Map<String, String> properties(String childName, String nameAttribute, String valueAttribute)
  {
    Map<String, String> properties = new HashMap<>();

    for (Tree child : children(childName))
    {
      String name = child.attribute(nameAttribute);
      if (name != null)
      {
        String value = child.attribute(valueAttribute);
        properties.put(name, value);
      }
    }

    return properties;
  }

  public <DATA> DATA visit(BiFunction<Tree, DATA, DATA> visitor, DATA data)
  {
    data = visitor.apply(this, data);
    if (data != null)
    {
      for (Tree child : children)
      {
        if (child.visit(visitor, data) == null)
        {
          return null;
        }
      }
    }

    return data;
  }

  @Override
  public int compareTo(Tree o)
  {
    return StringUtil.compare(name, o.name);
  }

  @Override
  public String toString()
  {
    return "Tree[" + name + "]";
  }

  public static Builder builder()
  {
    return new Builder();
  }

  public static Builder builder(Tree source)
  {
    Builder builder = builder().setName(source.name).setAttributes(source.attributes);
    source.children.forEach(child -> builder.addChild(builder(child)));
    return builder;
  }

  // public static void main(String[] args) throws Exception
  // {
  // Tree tree = Tree.builder().setName("ldap").setAttribute("ldap1", "val").setAttribute("ldap2", "val").addChild(c ->
  // {
  // c.setName("env").setAttribute("env1", "v\"a\"l").setAttribute("env2", "val");
  // }).addChild(c -> {
  // c.setName("auth").setAttribute("auth1", "val").setAttribute("auth2", "val");
  // }).build();
  //
  // tree.visit(new Tree.Dumper(), null);
  //
  // // ---
  //
  // Builder copy = Tree.builder(tree);
  // Builder auth = copy.child(1);
  // auth.setAttribute("auth2", "modified");
  // copy.build().visit(new Tree.Dumper(), null);
  //
  // // ---
  //
  // Document document = XMLConverter.convertTreeToDocument(tree);
  //
  // StringWriter writer = new StringWriter();
  // Transformer transformer = TransformerFactory.newInstance().newTransformer();
  // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
  // transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
  // transformer.setOutputProperty(OutputKeys.INDENT, "no");
  // transformer.transform(new DOMSource(document), new StreamResult(writer));
  // System.out.println(writer);
  // }

  /**
   * @author Eike Stepper
   */
  public static final class Builder implements Comparable<Builder>
  {
    private String name;

    private final Map<String, String> attributes = new HashMap<>();

    private Builder parent;

    private final List<Builder> children = new ArrayList<>();

    private Builder()
    {
    }

    public String name()
    {
      return name;
    }

    public Builder setName(String name)
    {
      this.name = name;
      return this;
    }

    public Map<String, String> attributes()
    {
      return Collections.unmodifiableMap(attributes);
    }

    public String attribute(String name)
    {
      return attributes.get(name);
    }

    public Builder setAttribute(String name, String value)
    {
      if (value != null)
      {
        attributes.put(name, value);
      }
      else
      {
        attributes.remove(name);
      }

      return this;
    }

    public Builder setAttributes(Map<String, String> attributes)
    {
      this.attributes.clear();
      return addAttributes(attributes);
    }

    public Builder addAttributes(Map<String, String> attributes)
    {
      this.attributes.putAll(attributes);
      return this;
    }

    public Builder unsetAttribute(String name)
    {
      return setAttribute(name, null);
    }

    public Builder unsetAttributes(Set<String> names)
    {
      names.forEach(attributes::remove);
      return this;
    }

    public Builder unsetAttributes()
    {
      attributes.clear();
      return this;
    }

    public Builder parent()
    {
      return parent;
    }

    public Builder setParent(Builder parent)
    {
      if (parent != this.parent)
      {
        if (this.parent != null)
        {
          this.parent.inverseRemoveChild(this);
        }

        inverseSetParent(parent);

        if (this.parent != null)
        {
          this.parent.inverseAddChild(this);
        }
      }

      return this;
    }

    public List<Builder> children()
    {
      return Collections.unmodifiableList(children);
    }

    public void children(Consumer<Builder> consumer)
    {
      for (Builder child : children)
      {
        consumer.accept(child);
      }
    }

    public List<Builder> children(String name)
    {
      List<Builder> result = new ArrayList<>();
      children(name, result::add);
      return result;
    }

    public void children(String name, Consumer<Builder> consumer)
    {
      for (Builder child : children)
      {
        if (Objects.equals(child.name, name))
        {
          consumer.accept(child);
        }
      }
    }

    public List<Builder> allChildren()
    {
      List<Builder> result = new ArrayList<>();
      allChildren(result::add);
      return result;
    }

    public void allChildren(Consumer<Builder> consumer)
    {
      for (Builder child : children)
      {
        consumer.accept(child);
        child.allChildren(name, consumer);
      }
    }

    public List<Builder> allChildren(String name)
    {
      List<Builder> result = new ArrayList<>();
      allChildren(name, result::add);
      return result;
    }

    public void allChildren(String name, Consumer<Builder> consumer)
    {
      for (Builder child : children)
      {
        if (Objects.equals(child.name, name))
        {
          consumer.accept(child);
        }

        child.allChildren(name, consumer);
      }
    }

    public Builder child(String name)
    {
      for (Builder child : children)
      {
        if (Objects.equals(child.name, name))
        {
          return child;
        }
      }

      return null;
    }

    public Builder child(int index)
    {
      return children.get(index);
    }

    public Builder addChild(Consumer<Builder> childInitializer)
    {
      Builder child = builder();
      addChild(child);
      childInitializer.accept(child);
      return this;
    }

    public Builder addChild(Tree child)
    {
      return addChild(builder(child));
    }

    public Builder addChild(Builder child)
    {
      if (child.parent != this)
      {
        if (child.parent != null)
        {
          child.parent.inverseRemoveChild(child);
        }

        child.inverseSetParent(this);
        inverseAddChild(child);
      }

      return this;
    }

    public Builder addChildren(Builder... children)
    {
      for (Builder child : children)
      {
        addChild(child);
      }

      return this;
    }

    public Builder removeChild(Builder child)
    {
      if (child.parent == this)
      {
        child.inverseSetParent(null);
        inverseRemoveChild(child);
      }

      return this;
    }

    public Builder removeChildren(Builder... children)
    {
      for (Builder child : children)
      {
        removeChild(child);
      }

      return this;
    }

    public int indexInParent()
    {
      if (parent == null)
      {
        return -1;
      }

      return parent.indexOfChild(this);
    }

    public int indexOfChild(Builder child)
    {
      return children.indexOf(child);
    }

    public Map<String, String> properties()
    {
      return properties(DEFAULT_CHILD_NAME);
    }

    public Map<String, String> properties(String childName)
    {
      return properties(childName, DEFAULT_NAME_ATTRIBUTE, DEFAULT_VALUE_ATTRIBUTE);
    }

    public Map<String, String> properties(String childName, String nameAttribute, String valueAttribute)
    {
      Map<String, String> properties = new HashMap<>();

      for (Builder child : children(childName))
      {
        String name = child.attribute(nameAttribute);
        if (name != null)
        {
          String value = child.attribute(valueAttribute);
          properties.put(name, value);
        }
      }

      return properties;
    }

    public <DATA> DATA visit(BiFunction<Builder, DATA, DATA> visitor, DATA data)
    {
      data = visitor.apply(this, data);
      if (data != null)
      {
        for (Builder child : children)
        {
          if (child.visit(visitor, data) == null)
          {
            return null;
          }
        }
      }

      return data;
    }

    @Override
    public int compareTo(Builder o)
    {
      return StringUtil.compare(name, o.name);
    }

    @Override
    public String toString()
    {
      return "Tree.Builder[" + name + "]";
    }

    public Tree build()
    {
      return build(null);
    }

    private Tree build(Tree parent)
    {
      Tree tree = new Tree(name, attributes, parent);

      if (children.isEmpty())
      {
        tree.setChildren(Collections.emptyList());
      }
      else
      {
        List<Tree> childTrees = new ArrayList<>();

        for (Builder child : children)
        {
          Tree childTree = child.build(tree);
          childTrees.add(childTree);
        }

        tree.setChildren(childTrees);
      }

      return tree;
    }

    private void inverseSetParent(Builder parent)
    {
      this.parent = parent;
    }

    private void inverseAddChild(Builder child)
    {
      if (!children.contains(child))
      {
        children.add(child);
      }
    }

    private void inverseRemoveChild(Builder child)
    {
      children.remove(child);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Dumper implements BiFunction<Tree, String, String>
  {
    private final PrintStream out;

    public Dumper(PrintStream out)
    {
      this.out = out;
    }

    public Dumper()
    {
      this(System.out);
    }

    public final PrintStream out()
    {
      return out;
    }

    @Override
    public String apply(Tree tree, String indent)
    {
      indent = StringUtil.safe(indent);
      String name = StringUtil.safe(tree.name());

      StringBuilder builder = new StringBuilder();

      tree.attributes().forEach((n, v) -> {
        StringUtil.appendSeparator(builder, ", ");
        builder.append(n);
        builder.append('=');
        builder.append(v);
      });

      builder.insert(0, '[');
      builder.insert(0, name);
      builder.insert(0, indent);
      builder.append(']');
      out.println(builder);

      return nextLevelIndent(tree, indent);
    }

    protected String nextLevelIndent(Tree tree, String indent)
    {
      return indent + "  ";
    }

    public static String toString(Tree tree)
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      tree.visit(new Dumper(new PrintStream(baos)), "");

      try
      {
        return baos.toString(StandardCharsets.UTF_8.name());
      }
      catch (UnsupportedEncodingException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class XMLConverter implements BiFunction<Tree, Element, Element>
  {
    private final Document document;

    public XMLConverter(Document document)
    {
      this.document = document;
    }

    public XMLConverter(DocumentBuilder documentBuilder)
    {
      this(documentBuilder.newDocument());
    }

    public XMLConverter(DocumentBuilderFactory documentBuilderFactory) throws ParserConfigurationException
    {
      this(documentBuilderFactory.newDocumentBuilder());
    }

    public XMLConverter() throws ParserConfigurationException
    {
      this(DocumentBuilderFactory.newInstance());
    }

    public final Document document()
    {
      return document;
    }

    @Override
    public Element apply(Tree tree, Element parentElement)
    {
      Element element = createElement(tree);
      tree.attributes().forEach((n, v) -> setAttribute(tree, element, n, v));
      appendElement(tree, element, parentElement);
      return element;
    }

    protected Element createElement(Tree tree)
    {
      String tagName = getTagName(tree);
      return document.createElement(tagName);
    }

    protected void appendElement(Tree tree, Element element, Element parentElement)
    {
      if (parentElement == null)
      {
        document.appendChild(element);
      }
      else
      {
        parentElement.appendChild(element);
      }
    }

    protected void setAttribute(Tree tree, Element element, String name, String value)
    {
      element.setAttribute(name, value);
    }

    protected String getTagName(Tree tree)
    {
      return StringUtil.safe(tree.name(), "element");
    }

    public static Document convertTreeToDocument(Tree tree) throws ParserConfigurationException
    {
      XMLConverter converter = new XMLConverter();
      tree.visit(converter, null);
      return converter.document();
    }

    public static Element convertTreeToElement(Tree tree) throws ParserConfigurationException
    {
      Document document = convertTreeToDocument(tree);
      return document.getDocumentElement();
    }

    public static Tree convertDocumentToTree(Document document)
    {
      return convertDocumentToTree(document, null);
    }

    public static Tree convertDocumentToTree(Document document, Map<String, String> parameters)
    {
      Element element = document.getDocumentElement();
      return convertElementToTree(element, parameters);
    }

    public static Tree convertElementToTree(Element element)
    {
      return convertElementToTree(element, null);
    }

    public static Tree convertElementToTree(Element element, Map<String, String> parameters)
    {
      Builder builder = builder().setName(element.getTagName());

      NamedNodeMap attributes = element.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++)
      {
        Node attrNode = attributes.item(i);
        if (attrNode.getNodeType() == Node.ATTRIBUTE_NODE)
        {
          Attr attr = (Attr)attrNode;
          String name = attr.getName();
          String value = attr.getValue();

          if (parameters != null)
          {
            value = StringUtil.replace(value, parameters);
          }

          builder.setAttribute(name, value);
        }
      }

      NodeList childNodes = element.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++)
      {
        Node childNode = childNodes.item(i);
        if (childNode.getNodeType() == Node.ELEMENT_NODE)
        {
          Element childElement = (Element)childNode;
          builder.addChild(convertElementToTree(childElement, parameters));
        }
      }

      return builder.build();
    }
  }
}

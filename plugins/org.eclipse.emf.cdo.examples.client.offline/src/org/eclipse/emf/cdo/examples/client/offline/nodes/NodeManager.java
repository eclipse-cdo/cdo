/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline.nodes;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class NodeManager extends Container<Object>
{
  private static final String NODE_PROPERTIES = "node.properties";

  private final Map<String, Node> nodes = new HashMap<>();

  private final NodeType.Client client = new NodeType.Client(this);

  private final NodeType.NormalRepository normalRepository = new NodeType.NormalRepository(this);

  private final NodeType.FailoverRepository failoverRepository = new NodeType.FailoverRepository(this);

  private final NodeType.FailoverMonitor failoverMonitor = new NodeType.FailoverMonitor(this);

  private final NodeType[] elements = { client, normalRepository, failoverRepository, failoverMonitor };

  private final File root;

  public NodeManager(File root)
  {
    this.root = root;

    if (!root.exists())
    {
      root.mkdirs();
    }

    if (!root.isDirectory())
    {
      throw new IllegalStateException("Problem with root folder: " + root);
    }

    List<Node> result = new ArrayList<>();
    for (File folder : root.listFiles())
    {
      if (folder.isDirectory())
      {
        Node node = loadNode(folder);
        if (node != null)
        {
          result.add(node);
        }
      }
    }

    activate();
  }

  private Node loadNode(File folder)
  {
    Properties properties = loadProperties(folder);
    if (properties != null)
    {
      String typeName = properties.getProperty(NodeType.TYPE_PROPERTY);
      for (NodeType nodeType : elements)
      {
        if (nodeType.getTypeName().equals(typeName))
        {
          Node node = new Node(nodeType, properties);
          return addNode(node);
        }
      }
    }

    return null;
  }

  private Properties loadProperties(File folder)
  {
    File file = new File(folder, NODE_PROPERTIES);
    if (file.isFile())
    {
      InputStream in = null;

      try
      {
        in = new FileInputStream(file);

        Properties properties = new Properties();
        properties.load(in);
        return properties;
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
      finally
      {
        IOUtil.close(in);
      }
    }

    return null;
  }

  public File getRoot()
  {
    return root;
  }

  public Node getNode(String name)
  {
    return nodes.get(name);
  }

  public Node[] getNodes()
  {
    Node[] array = nodes.values().toArray(new Node[nodes.size()]);
    Arrays.sort(array);
    return array;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  @Override
  public NodeType[] getElements()
  {
    return elements;
  }

  Node createNode(NodeType nodeType)
  {
    Node node = new Node(nodeType);
    saveNode(node);

    return addNode(node);
  }

  void saveNode(Node node)
  {
    OutputStream out = null;

    try
    {
      File folder = node.getFolder();
      folder.mkdirs();

      File file = new File(folder, NODE_PROPERTIES);
      out = new FileOutputStream(file);
      node.getSettings().store(out, "Node Settings");
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  private Node addNode(Node node)
  {
    nodes.put(node.getName(), node);
    node.getType().addElement(node);
    return node;
  }
}

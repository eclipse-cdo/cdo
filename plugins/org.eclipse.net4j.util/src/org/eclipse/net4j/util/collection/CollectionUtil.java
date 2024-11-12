/*
 * Copyright (c) 2015, 2018, 2019, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Various static helper methods.
 *
 * @author Eike Stepper
 * @since 3.5
 */
public final class CollectionUtil
{
  private CollectionUtil()
  {
  }

  public static <T> Iterator<T> dump(Iterator<T> it)
  {
    List<T> list = new ArrayList<>();
    while (it.hasNext())
    {
      list.add(it.next());
    }

    System.out.println(list);
    return list.iterator();
  }

  /**
   * @since 3.25
   */
  public static <NODE> List<NODE> topologicalSort(Collection<NODE> nodes, Function<NODE, Collection<NODE>> edgeProvider)
  {
    return topologicalSort(nodes, edgeProvider, false);
  }

  /**
   * @since 3.25
   */
  public static <NODE> List<NODE> topologicalSort(Collection<NODE> nodes, Function<NODE, Collection<NODE>> edgeProvider, boolean reverse)
  {
    final class NodeInfo<N>
    {
      N node;

      List<NodeInfo<N>> edgeInfos = new ArrayList<>();

      int indegree; // Number of incoming edges.

      NodeInfo(N node)
      {
        this.node = node;
      }
    }

    Map<NODE, NodeInfo<NODE>> infos = new HashMap<>();

    // Populate nodes.
    for (NODE node : nodes)
    {
      infos.put(node, new NodeInfo<>(node));
    }

    // Populate edges and increase indegrees.
    for (NODE node : nodes)
    {
      NodeInfo<NODE> info = infos.get(node);

      Collection<NODE> edges = edgeProvider.apply(node);
      if (edges != null)
      {
        for (NODE edge : edges)
        {
          NodeInfo<NODE> edgeInfo = infos.get(edge);
          if (edgeInfo == null)
          {
            throw new IllegalStateException("Edge is not a node: " + edge);
          }

          info.edgeInfos.add(edgeInfo);
          ++edgeInfo.indegree;
        }
      }
    }

    Queue<NodeInfo<NODE>> queue = new LinkedList<>();

    // Initialize queue with nodes that have no incoming edges.
    for (NodeInfo<NODE> info : infos.values())
    {
      if (info.indegree == 0)
      {
        queue.add(info);
      }
    }

    int size = nodes.size();
    List<NODE> result = new ArrayList<>(size);

    // Move nodes from the queue to the result list while decreasing the indegree of their edges.
    while (!queue.isEmpty())
    {
      NodeInfo<NODE> info = queue.poll();

      if (reverse)
      {
        result.add(0, info.node);
      }
      else
      {
        result.add(info.node);
      }

      for (NodeInfo<NODE> edgeInfo : info.edgeInfos)
      {
        if (--edgeInfo.indegree == 0)
        {
          queue.add(edgeInfo);
        }
      }
    }

    if (result.size() != size)
    {
      throw new IllegalStateException("Cycle detected");
    }

    return result;
  }

  /**
   * @since 3.16
   */
  public static <T> boolean addNotNull(Collection<? super T> c, T e)
  {
    if (e != null)
    {
      return c.add(e);
    }

    return false;
  }

  /**
   * @since 3.16
   */
  @SuppressWarnings("unchecked")
  public static <T> Set<T> setOf(Collection<? extends T> c)
  {
    if (c instanceof Set)
    {
      return (Set<T>)c;
    }

    return new HashSet<>(c);
  }

  /**
   * @since 3.16
   */
  public static <T> T first(Collection<? extends T> c)
  {
    Iterator<? extends T> it = c.iterator();
    return it.hasNext() ? it.next() : null;
  }

  /**
   * @since 3.16
   */
  public static <K, V> List<K> removeAll(Map<K, V> map, BiPredicate<K, V> predicate)
  {
    List<K> keys = new ArrayList<>();

    for (Map.Entry<K, V> entry : map.entrySet())
    {
      K key = entry.getKey();
      V value = entry.getValue();

      if (predicate.test(key, value))
      {
        keys.add(key);
      }
    }

    for (K key : keys)
    {
      map.remove(key);
    }

    return keys;
  }

  /**
   * @since 3.16
   */
  public static <K, V> V compute(Map<K, V> map, K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
  {
    try
    {
      return map.compute(key, remappingFunction);
    }
    catch (KeepMappedValue ex)
    {
      return ex.mappedValue();
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.16
   */
  public static final class KeepMappedValue extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    private final transient Object mappedValue;

    public KeepMappedValue(Object mappedValue)
    {
      this.mappedValue = mappedValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T mappedValue()
    {
      return (T)mappedValue;
    }
  }
}

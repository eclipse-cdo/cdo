/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Eike Stepper
 */
public class Graph
{
  private Graph()
  {
  }

  public static <E> List<E> topologicalSort(List<E> elements, Function<E, Collection<E>> adjacence)
  {
    List<E> result = new ArrayList<>();
    Set<E> visited = new HashSet<>();

    for (E element : elements)
    {
      visit(element, adjacence, result, visited);
    }

    return result;
  }

  private static <E> void visit(E element, Function<E, Collection<E>> adjacence, List<E> result, Set<E> visited)
  {
    if (visited.add(element))
    {
      Collection<E> adjacentElements = adjacence.apply(element);
      if (adjacentElements != null)
      {
        for (E adjacentElement : adjacentElements)
        {
          visit(adjacentElement, adjacence, result, visited);
        }
      }

      result.add(element);
    }
  }
}

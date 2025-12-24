/*
 * Copyright (c) 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.table;

import org.eclipse.net4j.util.collection.AbstractFilteredIterator;
import org.eclipse.net4j.util.collection.AbstractIterator;
import org.eclipse.net4j.util.collection.ComposedIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
final class ComposedRange extends AbstractRange
{
  final Table table;

  final List<Range> inclusions = new ArrayList<>();

  final List<Range> exclusions = new ArrayList<>();

  public ComposedRange(ComposedRange source)
  {
    table = source.table;
    inclusions.addAll(source.inclusions);
    exclusions.addAll(source.exclusions);
  }

  public ComposedRange(Table table, Range... ranges)
  {
    this.table = table;
    addRanges(ranges);
  }

  @Override
  public Range addRanges(Range... ranges)
  {
    addRanges(inclusions, ranges);
    return this;
  }

  @Override
  public Range subtractRanges(Range... ranges)
  {
    addRanges(exclusions, ranges);
    return this;
  }

  @Override
  public Table table()
  {
    return table;
  }

  @Override
  public Iterator<Cell> iterator()
  {
    if (inclusions.isEmpty())
    {
      return AbstractIterator.empty();
    }

    Iterator<Cell> iterator = ComposedIterator.fromIterables(inclusions);
    if (!exclusions.isEmpty())
    {
      Set<Cell> excludedCells = new HashSet<>();
      for (Range range : exclusions)
      {
        for (Cell cell : range)
        {
          excludedCells.add(cell);
        }
      }

      iterator = new AbstractFilteredIterator.Predicated<>(t -> !excludedCells.contains(t), iterator);
    }

    Set<Cell> tested = new HashSet<>();
    return new AbstractFilteredIterator.Predicated<>(t -> tested.add(t), iterator);
  }

  private static void addRanges(List<Range> list, Range... ranges)
  {
    for (int i = 0; i < ranges.length; i++)
    {
      Range range = ranges[i];
      list.add(range);
    }
  }
}

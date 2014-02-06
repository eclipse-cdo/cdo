/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.table;

import org.eclipse.net4j.util.collection.AbstractIterator;

import java.text.Format;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class RectangularRange extends AbstractRange
{
  final Table table;

  Coordinate topLeft;

  Coordinate bottomRight;

  RectangularRange(Table table, Coordinate coordinate1, Coordinate coordinate2)
  {
    this.table = table;

    int row1, row2;
    if (coordinate1.row <= coordinate2.row)
    {
      row1 = coordinate1.row;
      row2 = coordinate2.row;
    }
    else
    {
      row1 = coordinate2.row;
      row2 = coordinate1.row;
    }

    int col1, col2;
    if (coordinate1.col <= coordinate2.col)
    {
      col1 = coordinate1.col;
      col2 = coordinate2.col;
    }
    else
    {
      col1 = coordinate2.col;
      col2 = coordinate1.col;
    }

    topLeft = new Coordinate(col1, row1);
    bottomRight = new Coordinate(col2, row2);

    if (table != null)
    {
      // Make sure that the table is large enough
      table.cell(bottomRight);
    }
  }

  @Override
  public Table table()
  {
    return table;
  }

  public Coordinate topLeft()
  {
    return topLeft;
  }

  public Coordinate bottomRight()
  {
    return bottomRight;
  }

  public int cols()
  {
    return bottomRight.col - topLeft.col + 1;
  }

  public int rows()
  {
    return bottomRight.row - topLeft.row + 1;
  }

  @Override
  public boolean contains(int col, int row)
  {
    return topLeft.col <= col && col <= bottomRight.col && topLeft.row <= row && row <= bottomRight.row;
  }

  @Override
  public boolean contains(Range range)
  {
    if (range instanceof RectangularRange)
    {
      RectangularRange rect = (RectangularRange)range;
      return topLeft.col <= rect.topLeft.col && rect.bottomRight.col <= bottomRight.col
          && topLeft.row <= rect.topLeft.row && rect.bottomRight.row <= bottomRight.row;
    }

    return super.contains(range);
  }

  @Override
  public Iterator<Cell> iterator()
  {
    return new CellIterator();
  }

  @Override
  public RectangularRange value(Object value)
  {
    return (RectangularRange)super.value(value);
  }

  @Override
  public RectangularRange format(Format format)
  {
    return (RectangularRange)super.format(format);
  }

  @Override
  public RectangularRange alignment(Alignment alignment)
  {
    return (RectangularRange)super.alignment(alignment);
  }

  public String dump(int... rowSeparators)
  {
    StringBuilder builder = new StringBuilder();
    dump(builder, rowSeparators);
    return builder.toString();
  }

  public void dump(StringBuilder builder, int... rowSeparators)
  {
    Table table = table();
    int cols = cols();
    int rows = rows();

    String[][] strings = new String[cols][];
    Alignment[][] alignments = new Alignment[cols][];
    int[] widths = new int[cols];

    for (int col = 0; col < cols; col++)
    {
      strings[col] = new String[rows];
      alignments[col] = new Alignment[rows];

      for (int row = 0; row < rows; row++)
      {
        Cell cell = table.cell(topLeft.col + col, topLeft.row + row);
        Object value = cell.value();
        String string = cell.applyFormat(value);

        strings[col][row] = string;
        alignments[col][row] = cell.alignmentFor(value);

        widths[col] = Math.max(widths[col], string.length());
      }
    }

    dumpSeparator(builder, widths);
    for (int row = 0; row < rows; row++)
    {
      builder.append("| ");
      for (int col = 0; col < cols; col++)
      {
        if (col != 0)
        {
          builder.append(" | ");
        }

        String value = strings[col][row];
        Alignment alignment = alignments[col][row];
        int width = widths[col];

        builder.append(alignment.apply(value, width));
      }

      builder.append(" |\n");
      if (row < rows - 1 && needsSeparator(row, rowSeparators))
      {
        dumpSeparator(builder, widths);
      }
    }

    dumpSeparator(builder, widths);
  }

  private static void dumpSeparator(StringBuilder builder, int[] widths)
  {
    builder.append("+-");
    for (int c = 0; c < widths.length; c++)
    {
      if (c > 0)
      {
        builder.append("-+-");
      }

      for (int i = 0; i < widths[c]; i++)
      {
        builder.append('-');
      }
    }

    builder.append("-+\n");
  }

  private static boolean needsSeparator(int row, int[] rowSeparators)
  {
    for (int i = 0; i < rowSeparators.length; i++)
    {
      if (rowSeparators[i] == row)
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  private final class CellIterator extends AbstractIterator<Cell>
  {
    private int col = topLeft.col;

    private int row = topLeft.row;

    @Override
    protected Object computeNextElement()
    {
      try
      {
        if (col > bottomRight.col)
        {
          if (++row > bottomRight.row)
          {
            return END_OF_DATA;
          }

          col = topLeft.col;
        }

        return table().cell(col, row);
      }
      finally
      {
        ++col;
      }
    }
  }
}

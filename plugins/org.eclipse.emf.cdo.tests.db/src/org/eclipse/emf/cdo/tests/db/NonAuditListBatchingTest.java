/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;

import java.util.ArrayList;
import java.util.List;

/**
 * Focused non-audit ListTable batching tests.
 *
 * @author Eike Stepper
 */
@Skips({ IRepositoryConfig.CAPABILITY_AUDITING, IRepositoryConfig.CAPABILITY_BRANCHING })
public class NonAuditListBatchingTest extends AbstractCDOTest
{
  public void testFullRevisionListsWithDuplicateValues() throws Exception
  {
    final int polygonCount = 8;
    final int pointsPerPolygon = 150;

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res-full")); //$NON-NLS-1$

    for (int polygonIndex = 0; polygonIndex < polygonCount; polygonIndex++)
    {
      PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
      for (int pointIndex = 0; pointIndex < pointsPerPolygon; pointIndex++)
      {
        polygon.getPoints().add(new Point(pointIndex % 3, pointIndex % 5));
      }

      resource.getContents().add(polygon);
    }

    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();

    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/res-full")); //$NON-NLS-1$
      assertEquals(polygonCount, persisted.getContents().size());

      for (int polygonIndex = 0; polygonIndex < polygonCount; polygonIndex++)
      {
        PolygonWithDuplicates polygon = (PolygonWithDuplicates)persisted.getContents().get(polygonIndex);
        assertEquals(pointsPerPolygon, polygon.getPoints().size());
        assertEquals(new Point(0, 0), polygon.getPoints().get(0));
        assertEquals(new Point(1, 1), polygon.getPoints().get(1));
        assertEquals(new Point(2, 4), polygon.getPoints().get(pointsPerPolygon - 1));
      }
    }
    finally
    {
      view.close();
      session.close();
    }
  }

  public void testIndependentAndSequentialShifts() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res-shifts")); //$NON-NLS-1$

    List<PolygonWithDuplicates> polygons = new ArrayList<>();
    List<List<Point>> expected = new ArrayList<>();

    for (int polygonIndex = 0; polygonIndex < 4; polygonIndex++)
    {
      PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
      List<Point> points = new ArrayList<>();

      for (int pointIndex = 0; pointIndex < 10; pointIndex++)
      {
        Point point = new Point(pointIndex % 2, pointIndex);
        polygon.getPoints().add(point);
        points.add(point);
      }

      resource.getContents().add(polygon);
      polygons.add(polygon);
      expected.add(points);
    }

    transaction.commit();

    for (int polygonIndex = 0; polygonIndex < polygons.size(); polygonIndex++)
    {
      move(polygons.get(polygonIndex).getPoints(), expected.get(polygonIndex), 7, 1);
    }

    move(polygons.get(0).getPoints(), expected.get(0), 2, 8);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();

    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/res-shifts")); //$NON-NLS-1$
      for (int polygonIndex = 0; polygonIndex < polygons.size(); polygonIndex++)
      {
        PolygonWithDuplicates polygon = (PolygonWithDuplicates)persisted.getContents().get(polygonIndex);
        assertEquals(expected.get(polygonIndex), polygon.getPoints());
      }
    }
    finally
    {
      view.close();
      session.close();
    }
  }

  public void testGroupedDeltasWithDuplicateValues() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1")); //$NON-NLS-1$

    List<PolygonWithDuplicates> polygons = new ArrayList<>();

    for (int i = 0; i < 5; i++)
    {
      PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
      polygon.getPoints().add(new Point(1, 1));
      polygon.getPoints().add(new Point(2, 2));
      polygon.getPoints().add(new Point(1, 1));
      polygon.getPoints().add(new Point(3, 3));
      resource.getContents().add(polygon);
      polygons.add(polygon);
    }

    transaction.commit();

    for (PolygonWithDuplicates polygon : polygons)
    {
      polygon.getPoints().add(1, new Point(1, 1));
      polygon.getPoints().set(2, new Point(1, 1));
    }

    polygons.get(0).getPoints().remove(0);
    polygons.get(1).getPoints().move(4, 0);
    polygons.get(2).getPoints().move(0, 4);
    polygons.get(3).getPoints().clear();
    transaction.commit();

    assertPoints(polygons.get(0), 1, 1, 1, 1, 1, 1, 3, 3);
    assertPoints(polygons.get(1), 1, 1, 1, 1, 1, 1, 3, 3, 1, 1);
    assertPoints(polygons.get(2), 3, 3, 1, 1, 1, 1, 1, 1, 1, 1);
    assertEquals(0, polygons.get(3).getPoints().size());
    assertPoints(polygons.get(4), 1, 1, 1, 1, 1, 1, 1, 1, 3, 3);

    session.close();
    session = openSession();
    CDOView view = session.openView();

    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/res1")); //$NON-NLS-1$
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(0), 1, 1, 1, 1, 1, 1, 3, 3);
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(1), 1, 1, 1, 1, 1, 1, 3, 3, 1, 1);
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(2), 3, 3, 1, 1, 1, 1, 1, 1, 1, 1);
      assertEquals(0, ((PolygonWithDuplicates)persisted.getContents().get(3)).getPoints().size());
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(4), 1, 1, 1, 1, 1, 1, 1, 1, 3, 3);
    }
    finally
    {
      view.close();
      session.close();
    }
  }

  public void testRepeatedBidirectionalShiftsWithDuplicates() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res-repeated-shifts")); //$NON-NLS-1$

    PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
    List<Point> expected = new ArrayList<>();

    for (int i = 0; i < 24; i++)
    {
      Point point = new Point(i % 3, i % 2);
      polygon.getPoints().add(point);
      expected.add(point);
    }

    resource.getContents().add(polygon);
    transaction.commit();

    move(polygon.getPoints(), expected, 18, 2);
    move(polygon.getPoints(), expected, 4, 20);
    move(polygon.getPoints(), expected, 17, 5);
    move(polygon.getPoints(), expected, 1, 19);
    move(polygon.getPoints(), expected, 21, 3);
    move(polygon.getPoints(), expected, 2, 22);
    transaction.commit();

    session.close();
    session = openSession();
    CDOView view = session.openView();

    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/res-repeated-shifts")); //$NON-NLS-1$
      assertEquals(expected, ((PolygonWithDuplicates)persisted.getContents().get(0)).getPoints());
    }
    finally
    {
      view.close();
      session.close();
    }
  }

  public void testCommitWideCrossListDeltas() throws Exception
  {
    final int polygonCount = 6;
    final int initialSize = 80;

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res-cross-list")); //$NON-NLS-1$

    List<PolygonWithDuplicates> polygons = new ArrayList<>();
    List<List<Point>> expected = new ArrayList<>();

    for (int polygonIndex = 0; polygonIndex < polygonCount; polygonIndex++)
    {
      PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
      List<Point> points = new ArrayList<>();

      for (int pointIndex = 0; pointIndex < initialSize; pointIndex++)
      {
        Point point = new Point(pointIndex % 4, pointIndex % 3);
        polygon.getPoints().add(point);
        points.add(point);
      }

      resource.getContents().add(polygon);
      polygons.add(polygon);
      expected.add(points);
    }

    transaction.commit();

    for (int polygonIndex = 0; polygonIndex < polygonCount; polygonIndex++)
    {
      EList<Point> actual = polygons.get(polygonIndex).getPoints();
      List<Point> points = expected.get(polygonIndex);

      for (int change = 0; change < 32; change++)
      {
        Point point = new Point(polygonIndex, change % 3);
        int index = change * 3 % (points.size() + 1);
        actual.add(index, point);
        points.add(index, point);
      }

      for (int change = 0; change < 16; change++)
      {
        int index = change * 5 % points.size();
        actual.remove(index);
        points.remove(index);
      }

      move(actual, points, 60, 7);
      move(actual, points, 12, 65);
      move(actual, points, 70, 15);
    }

    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();

    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/res-cross-list")); //$NON-NLS-1$
      assertEquals(polygonCount, persisted.getContents().size());

      for (int polygonIndex = 0; polygonIndex < polygonCount; polygonIndex++)
      {
        PolygonWithDuplicates polygon = (PolygonWithDuplicates)persisted.getContents().get(polygonIndex);
        assertEquals(expected.get(polygonIndex), polygon.getPoints());
      }
    }
    finally
    {
      view.close();
      session.close();
    }
  }

  private void move(EList<Point> actual, List<Point> expected, int targetIndex, int sourceIndex)
  {
    actual.move(targetIndex, sourceIndex);
    expected.add(targetIndex, expected.remove(sourceIndex));
  }

  private void assertPoints(PolygonWithDuplicates polygon, int... coordinates)
  {
    EList<Point> points = polygon.getPoints();
    assertEquals(coordinates.length / 2, points.size());

    for (int i = 0; i < points.size(); i++)
    {
      assertEquals(new Point(coordinates[2 * i], coordinates[2 * i + 1]), points.get(i));
    }
  }
}

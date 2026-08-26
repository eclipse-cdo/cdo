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
    assertPoints(polygons.get(1), 1, 1, 1, 1, 3, 3, 1, 1);
    assertPoints(polygons.get(2), 3, 3, 1, 1, 1, 1, 1, 1);
    assertEquals(0, polygons.get(3).getPoints().size());
    assertPoints(polygons.get(4), 1, 1, 1, 1, 1, 1, 1, 1, 3, 3);

    session.close();
    session = openSession();
    org.eclipse.emf.cdo.view.CDOView view = session.openView();
    try
    {
      CDOResource persisted = view.getResource(getResourcePath("/res1")); //$NON-NLS-1$
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(0), 1, 1, 1, 1, 1, 1, 3, 3);
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(1), 1, 1, 1, 1, 3, 3, 1, 1);
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(2), 3, 3, 1, 1, 1, 1, 1, 1);
      assertEquals(0, ((PolygonWithDuplicates)persisted.getContents().get(3)).getPoints().size());
      assertPoints((PolygonWithDuplicates)persisted.getContents().get(4), 1, 1, 1, 1, 1, 1, 1, 1, 3, 3);
    }
    finally
    {
      view.close();
      session.close();
    }
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

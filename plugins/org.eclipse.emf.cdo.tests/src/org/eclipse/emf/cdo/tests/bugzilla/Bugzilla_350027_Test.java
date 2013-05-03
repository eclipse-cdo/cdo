/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

/**
 * Fix index for moves from left to right.
 * <p>
 * See bug 350027
 * 
 * @author Cyril Jaquier
 */
@CleanRepositoriesBefore
public class Bugzilla_350027_Test extends AbstractCDOTest
{
  public void testMoveFromLeftToRight() throws Exception
  {
    // ABCD (before move)
    // ACDB (after move from 1 => 3)
    CDOMoveFeatureDeltaImpl move = new CDOMoveFeatureDeltaImpl(getModel1Package().getAddress_Name(), 3, 1);

    // This simulates the removal of D which is at position 3 before the move
    move.adjustAfterRemoval(3);

    // We should end up with ACB which correspond to a move from 1 => 2
    assertEquals(1, move.getOldPosition());
    assertEquals(2, move.getNewPosition());
  }
}

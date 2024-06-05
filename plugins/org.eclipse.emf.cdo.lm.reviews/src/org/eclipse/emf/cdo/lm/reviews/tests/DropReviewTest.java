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
package org.eclipse.emf.cdo.lm.reviews.tests;

import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Drop Review</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.FixedBaseline#getBasedChanges() <em>Get Based Changes</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class DropReviewTest extends ReviewTest
{

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static void main(String[] args)
  {
    TestRunner.run(DropReviewTest.class);
  }

  /**
   * Constructs a new Drop Review test case with the given name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DropReviewTest(String name)
  {
    super(name);
  }

  /**
   * Returns the fixture for this Drop Review test case.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected DropReview getFixture()
  {
    return (DropReview)fixture;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected void setUp() throws Exception
  {
    setFixture(ReviewsFactory.eINSTANCE.createDropReview());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected void tearDown() throws Exception
  {
    setFixture(null);
  }

  /**
   * Tests the '{@link org.eclipse.emf.cdo.lm.FixedBaseline#getBasedChanges() <em>Get Based Changes</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.FixedBaseline#getBasedChanges()
   * @generated
   */
  public void testGetBasedChanges()
  {
    fail();
  }

} // DropReviewTest

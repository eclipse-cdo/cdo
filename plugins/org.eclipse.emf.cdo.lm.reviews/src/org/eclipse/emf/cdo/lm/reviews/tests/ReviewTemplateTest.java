/**
 */
package org.eclipse.emf.cdo.lm.reviews.tests;

import org.eclipse.emf.cdo.lm.reviews.ReviewTemplate;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Review Template</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReviewTemplateTest extends CommentableTest
{

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static void main(String[] args)
  {
    TestRunner.run(ReviewTemplateTest.class);
  }

  /**
   * Constructs a new Review Template test case with the given name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewTemplateTest(String name)
  {
    super(name);
  }

  /**
   * Returns the fixture for this Review Template test case.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected ReviewTemplate getFixture()
  {
    return (ReviewTemplate)fixture;
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
    setFixture(ReviewsFactory.eINSTANCE.createReviewTemplate());
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

} // ReviewTemplateTest

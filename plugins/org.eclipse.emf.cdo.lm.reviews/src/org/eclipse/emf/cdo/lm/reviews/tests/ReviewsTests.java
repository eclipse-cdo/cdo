/**
 */
package org.eclipse.emf.cdo.lm.reviews.tests;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>reviews</b></em>' package.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReviewsTests extends TestSuite
{

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static void main(String[] args)
  {
    TestRunner.run(suite());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static TestSuite suite()
  {
    TestSuite suite = new ReviewsTests("reviews Tests");
    suite.addTestSuite(CommentTest.class);
    suite.addTestSuite(ReviewTemplateTest.class);
    suite.addTestSuite(DeliveryReviewTest.class);
    suite.addTestSuite(DropReviewTest.class);
    return suite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewsTests(String name)
  {
    super(name);
  }

} // ReviewsTests

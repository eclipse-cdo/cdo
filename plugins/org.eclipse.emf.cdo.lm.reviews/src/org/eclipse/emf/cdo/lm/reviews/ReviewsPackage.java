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
package org.eclipse.emf.cdo.lm.reviews;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.LMPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsFactory
 * @model kind="package"
 * @generated
 */
public interface ReviewsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "reviews";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/lm/reviews/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "reviews";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  ReviewsPackage eINSTANCE = org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl.init();

  public static final String ANNOTATION_SOURCE = "http://www.eclipse.org/CDO/LM/Reviews";

  public static Annotation getAnnotation(ModelElement modelElement, boolean createOnDemand)
  {
    if (modelElement != null)
    {
      Annotation annotation = modelElement.getAnnotation(ANNOTATION_SOURCE);
      if (annotation == null && createOnDemand)
      {
        annotation = EtypesFactory.eINSTANCE.createAnnotation(ANNOTATION_SOURCE);
        modelElement.getAnnotations().add(annotation);
      }

      return annotation;
    }

    return null;
  }

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl <em>Commentable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getCommentable()
   * @generated
   */
  int COMMENTABLE = 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE__ANNOTATIONS = LMPackage.SYSTEM_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE__REVIEW = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE__COMMENTS = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE__COMMENT_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE__UNRESOLVED_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE__RESOLVED_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Commentable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE_FEATURE_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 5;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE___GET_ANNOTATION__STRING = LMPackage.SYSTEM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE___GET_SYSTEM = LMPackage.SYSTEM_ELEMENT___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Commentable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE_OPERATION_COUNT = LMPackage.SYSTEM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl <em>Comment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getComment()
   * @generated
   */
  int COMMENT = 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__ANNOTATIONS = COMMENTABLE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__REVIEW = COMMENTABLE__REVIEW;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__COMMENTS = COMMENTABLE__COMMENTS;

  /**
   * The feature id for the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__COMMENT_COUNT = COMMENTABLE__COMMENT_COUNT;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__UNRESOLVED_COUNT = COMMENTABLE__UNRESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__RESOLVED_COUNT = COMMENTABLE__RESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Commentable</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__COMMENTABLE = COMMENTABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Text</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__TEXT = COMMENTABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Status</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__STATUS = COMMENTABLE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Comment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT_FEATURE_COUNT = COMMENTABLE_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT___GET_ANNOTATION__STRING = COMMENTABLE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT___GET_SYSTEM = COMMENTABLE___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Comment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT_OPERATION_COUNT = COMMENTABLE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl <em>Heading</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getHeading()
   * @generated
   */
  int HEADING = 2;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__ANNOTATIONS = COMMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__REVIEW = COMMENT__REVIEW;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__COMMENTS = COMMENT__COMMENTS;

  /**
   * The feature id for the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__COMMENT_COUNT = COMMENT__COMMENT_COUNT;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__UNRESOLVED_COUNT = COMMENT__UNRESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__RESOLVED_COUNT = COMMENT__RESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Commentable</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__COMMENTABLE = COMMENT__COMMENTABLE;

  /**
   * The feature id for the '<em><b>Text</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__TEXT = COMMENT__TEXT;

  /**
   * The feature id for the '<em><b>Status</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING__STATUS = COMMENT__STATUS;

  /**
   * The number of structural features of the '<em>Heading</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING_FEATURE_COUNT = COMMENT_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING___GET_ANNOTATION__STRING = COMMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING___GET_SYSTEM = COMMENT___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Heading</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HEADING_OPERATION_COUNT = COMMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl <em>Review Template</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewTemplate()
   * @generated
   */
  int REVIEW_TEMPLATE = 3;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__ANNOTATIONS = COMMENTABLE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__REVIEW = COMMENTABLE__REVIEW;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__COMMENTS = COMMENTABLE__COMMENTS;

  /**
   * The feature id for the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__COMMENT_COUNT = COMMENTABLE__COMMENT_COUNT;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__UNRESOLVED_COUNT = COMMENTABLE__UNRESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__RESOLVED_COUNT = COMMENTABLE__RESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__REVIEWERS = COMMENTABLE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Review Template</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE_FEATURE_COUNT = COMMENTABLE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE___GET_ANNOTATION__STRING = COMMENTABLE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE___GET_SYSTEM = COMMENTABLE___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Review Template</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE_OPERATION_COUNT = COMMENTABLE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl <em>Review</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReview()
   * @generated
   */
  int REVIEW = 4;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__ANNOTATIONS = LMPackage.BASELINE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__STREAM = LMPackage.BASELINE__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__FLOATING = LMPackage.BASELINE__FLOATING;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__REVIEW = LMPackage.BASELINE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__COMMENTS = LMPackage.BASELINE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__COMMENT_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__UNRESOLVED_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__RESOLVED_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__ID = LMPackage.BASELINE_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__AUTHOR = LMPackage.BASELINE_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__REVIEWERS = LMPackage.BASELINE_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Status</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__STATUS = LMPackage.BASELINE_FEATURE_COUNT + 8;

  /**
   * The number of structural features of the '<em>Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_FEATURE_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 9;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW___GET_ANNOTATION__STRING = LMPackage.BASELINE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW___GET_SYSTEM = LMPackage.BASELINE___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW___GET_MODULE = LMPackage.BASELINE___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW___GET_STREAM = LMPackage.BASELINE___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW___GET_NAME = LMPackage.BASELINE___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW___GET_BRANCH_POINT = LMPackage.BASELINE___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW___GET_BASE_TIME_STAMP = LMPackage.BASELINE___GET_BASE_TIME_STAMP;

  /**
   * The number of operations of the '<em>Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_OPERATION_COUNT = LMPackage.BASELINE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl <em>Delivery Review</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getDeliveryReview()
   * @generated
   */
  int DELIVERY_REVIEW = 5;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__ANNOTATIONS = REVIEW__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__STREAM = REVIEW__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__FLOATING = REVIEW__FLOATING;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__REVIEW = REVIEW__REVIEW;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__COMMENTS = REVIEW__COMMENTS;

  /**
   * The feature id for the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__COMMENT_COUNT = REVIEW__COMMENT_COUNT;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__UNRESOLVED_COUNT = REVIEW__UNRESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__RESOLVED_COUNT = REVIEW__RESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__ID = REVIEW__ID;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__AUTHOR = REVIEW__AUTHOR;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__REVIEWERS = REVIEW__REVIEWERS;

  /**
   * The feature id for the '<em><b>Status</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__STATUS = REVIEW__STATUS;

  /**
   * The feature id for the '<em><b>Closed</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__CLOSED = REVIEW_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Base</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__BASE = REVIEW_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Impact</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__IMPACT = REVIEW_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Branch</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__BRANCH = REVIEW_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Deliveries</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__DELIVERIES = REVIEW_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Source Change</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__SOURCE_CHANGE = REVIEW_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Source Commit</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__SOURCE_COMMIT = REVIEW_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Target Commit</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__TARGET_COMMIT = REVIEW_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Rebase Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__REBASE_COUNT = REVIEW_FEATURE_COUNT + 8;

  /**
   * The number of structural features of the '<em>Delivery Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW_FEATURE_COUNT = REVIEW_FEATURE_COUNT + 9;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_ANNOTATION__STRING = REVIEW___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_SYSTEM = REVIEW___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_MODULE = REVIEW___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_STREAM = REVIEW___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_NAME = REVIEW___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_BRANCH_POINT = REVIEW___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_BASE_TIME_STAMP = REVIEW___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Base</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_BASE = REVIEW_OPERATION_COUNT + 0;

  /**
   * The operation id for the '<em>Get Deliveries</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_DELIVERIES = REVIEW_OPERATION_COUNT + 1;

  /**
   * The operation id for the '<em>Get Branch</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW___GET_BRANCH = REVIEW_OPERATION_COUNT + 2;

  /**
   * The number of operations of the '<em>Delivery Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW_OPERATION_COUNT = REVIEW_OPERATION_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl <em>Drop Review</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getDropReview()
   * @generated
   */
  int DROP_REVIEW = 6;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__ANNOTATIONS = REVIEW__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__STREAM = REVIEW__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__FLOATING = REVIEW__FLOATING;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__REVIEW = REVIEW__REVIEW;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__COMMENTS = REVIEW__COMMENTS;

  /**
   * The feature id for the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__COMMENT_COUNT = REVIEW__COMMENT_COUNT;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__UNRESOLVED_COUNT = REVIEW__UNRESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__RESOLVED_COUNT = REVIEW__RESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__ID = REVIEW__ID;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__AUTHOR = REVIEW__AUTHOR;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__REVIEWERS = REVIEW__REVIEWERS;

  /**
   * The feature id for the '<em><b>Status</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__STATUS = REVIEW__STATUS;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__VERSION = REVIEW_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__DEPENDENCIES = REVIEW_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Delivery</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__DELIVERY = REVIEW_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Target Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__TARGET_TIME_STAMP = REVIEW_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Drop Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__DROP_TYPE = REVIEW_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Drop Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__DROP_LABEL = REVIEW_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Drop Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW_FEATURE_COUNT = REVIEW_FEATURE_COUNT + 6;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_ANNOTATION__STRING = REVIEW___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_SYSTEM = REVIEW___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_MODULE = REVIEW___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_STREAM = REVIEW___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_NAME = REVIEW___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_BRANCH_POINT = REVIEW___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_BASE_TIME_STAMP = REVIEW___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Based Changes</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW___GET_BASED_CHANGES = REVIEW_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Drop Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW_OPERATION_COUNT = REVIEW_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.CommentStatus <em>Comment Status</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.CommentStatus
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getCommentStatus()
   * @generated
   */
  int COMMENT_STATUS = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.ReviewStatus <em>Review Status</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewStatus
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewStatus()
   * @generated
   */
  int REVIEW_STATUS = 8;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.Commentable <em>Commentable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Commentable</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Commentable
   * @generated
   */
  EClass getCommentable();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getReview <em>Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Commentable#getReview()
   * @see #getCommentable()
   * @generated
   */
  EReference getCommentable_Review();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getComments <em>Comments</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Comments</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Commentable#getComments()
   * @see #getCommentable()
   * @generated
   */
  EReference getCommentable_Comments();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getCommentCount <em>Comment Count</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Comment Count</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Commentable#getCommentCount()
   * @see #getCommentable()
   * @generated
   */
  EAttribute getCommentable_CommentCount();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getUnresolvedCount <em>Unresolved Count</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Unresolved Count</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Commentable#getUnresolvedCount()
   * @see #getCommentable()
   * @generated
   */
  EAttribute getCommentable_UnresolvedCount();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getResolvedCount <em>Resolved Count</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Resolved Count</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Commentable#getResolvedCount()
   * @see #getCommentable()
   * @generated
   */
  EAttribute getCommentable_ResolvedCount();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.Comment <em>Comment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Comment</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Comment
   * @generated
   */
  EClass getComment();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable <em>Commentable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Commentable</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable()
   * @see #getComment()
   * @generated
   */
  EReference getComment_Commentable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getText <em>Text</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Text</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getText()
   * @see #getComment()
   * @generated
   */
  EAttribute getComment_Text();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getStatus <em>Status</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Status</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getStatus()
   * @see #getComment()
   * @generated
   */
  EAttribute getComment_Status();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.Heading <em>Heading</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Heading</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Heading
   * @generated
   */
  EClass getHeading();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.ReviewTemplate <em>Review Template</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Review Template</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewTemplate
   * @generated
   */
  EClass getReviewTemplate();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.lm.reviews.ReviewTemplate#getReviewers <em>Reviewers</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Reviewers</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewTemplate#getReviewers()
   * @see #getReviewTemplate()
   * @generated
   */
  EAttribute getReviewTemplate_Reviewers();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.Review <em>Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Review
   * @generated
   */
  EClass getReview();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Review#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Review#getId()
   * @see #getReview()
   * @generated
   */
  EAttribute getReview_Id();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Review#getAuthor <em>Author</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Author</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Review#getAuthor()
   * @see #getReview()
   * @generated
   */
  EAttribute getReview_Author();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.lm.reviews.Review#getReviewers <em>Reviewers</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Reviewers</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Review#getReviewers()
   * @see #getReview()
   * @generated
   */
  EAttribute getReview_Reviewers();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Review#getStatus <em>Status</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Status</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Review#getStatus()
   * @see #getReview()
   * @generated
   */
  EAttribute getReview_Status();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview <em>Delivery Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Delivery Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview
   * @generated
   */
  EClass getDeliveryReview();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBase <em>Base</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Base</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBase()
   * @see #getDeliveryReview()
   * @generated
   */
  EReference getDeliveryReview_Base();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getImpact <em>Impact</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Impact</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getImpact()
   * @see #getDeliveryReview()
   * @generated
   */
  EAttribute getDeliveryReview_Impact();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBranch <em>Branch</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Branch</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBranch()
   * @see #getDeliveryReview()
   * @generated
   */
  EAttribute getDeliveryReview_Branch();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getDeliveries <em>Deliveries</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Deliveries</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getDeliveries()
   * @see #getDeliveryReview()
   * @generated
   */
  EReference getDeliveryReview_Deliveries();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceChange <em>Source Change</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Source Change</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceChange()
   * @see #getDeliveryReview()
   * @generated
   */
  EReference getDeliveryReview_SourceChange();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceCommit <em>Source Commit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Source Commit</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceCommit()
   * @see #getDeliveryReview()
   * @generated
   */
  EAttribute getDeliveryReview_SourceCommit();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getTargetCommit <em>Target Commit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Commit</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getTargetCommit()
   * @see #getDeliveryReview()
   * @generated
   */
  EAttribute getDeliveryReview_TargetCommit();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getRebaseCount <em>Rebase Count</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Rebase Count</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getRebaseCount()
   * @see #getDeliveryReview()
   * @generated
   */
  EAttribute getDeliveryReview_RebaseCount();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.DropReview <em>Drop Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Drop Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DropReview
   * @generated
   */
  EClass getDropReview();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDelivery <em>Delivery</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Delivery</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DropReview#getDelivery()
   * @see #getDropReview()
   * @generated
   */
  EReference getDropReview_Delivery();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getTargetTimeStamp <em>Target Time Stamp</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Time Stamp</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DropReview#getTargetTimeStamp()
   * @see #getDropReview()
   * @generated
   */
  EAttribute getDropReview_TargetTimeStamp();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropType <em>Drop Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Drop Type</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DropReview#getDropType()
   * @see #getDropReview()
   * @generated
   */
  EReference getDropReview_DropType();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropLabel <em>Drop Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Drop Label</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DropReview#getDropLabel()
   * @see #getDropReview()
   * @generated
   */
  EAttribute getDropReview_DropLabel();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.lm.reviews.CommentStatus <em>Comment Status</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Comment Status</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.CommentStatus
   * @generated
   */
  EEnum getCommentStatus();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.lm.reviews.ReviewStatus <em>Review Status</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Review Status</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewStatus
   * @generated
   */
  EEnum getReviewStatus();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  ReviewsFactory getReviewsFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl <em>Commentable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getCommentable()
     * @generated
     */
    EClass COMMENTABLE = eINSTANCE.getCommentable();

    /**
     * The meta object literal for the '<em><b>Review</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMMENTABLE__REVIEW = eINSTANCE.getCommentable_Review();

    /**
     * The meta object literal for the '<em><b>Comments</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMMENTABLE__COMMENTS = eINSTANCE.getCommentable_Comments();

    /**
     * The meta object literal for the '<em><b>Comment Count</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMMENTABLE__COMMENT_COUNT = eINSTANCE.getCommentable_CommentCount();

    /**
     * The meta object literal for the '<em><b>Unresolved Count</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMMENTABLE__UNRESOLVED_COUNT = eINSTANCE.getCommentable_UnresolvedCount();

    /**
     * The meta object literal for the '<em><b>Resolved Count</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMMENTABLE__RESOLVED_COUNT = eINSTANCE.getCommentable_ResolvedCount();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl <em>Comment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getComment()
     * @generated
     */
    EClass COMMENT = eINSTANCE.getComment();

    /**
     * The meta object literal for the '<em><b>Commentable</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMMENT__COMMENTABLE = eINSTANCE.getComment_Commentable();

    /**
     * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMMENT__TEXT = eINSTANCE.getComment_Text();

    /**
     * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMMENT__STATUS = eINSTANCE.getComment_Status();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl <em>Heading</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getHeading()
     * @generated
     */
    EClass HEADING = eINSTANCE.getHeading();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl <em>Review Template</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewTemplate()
     * @generated
     */
    EClass REVIEW_TEMPLATE = eINSTANCE.getReviewTemplate();

    /**
     * The meta object literal for the '<em><b>Reviewers</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW_TEMPLATE__REVIEWERS = eINSTANCE.getReviewTemplate_Reviewers();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl <em>Review</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReview()
     * @generated
     */
    EClass REVIEW = eINSTANCE.getReview();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW__ID = eINSTANCE.getReview_Id();

    /**
     * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW__AUTHOR = eINSTANCE.getReview_Author();

    /**
     * The meta object literal for the '<em><b>Reviewers</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW__REVIEWERS = eINSTANCE.getReview_Reviewers();

    /**
     * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW__STATUS = eINSTANCE.getReview_Status();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl <em>Delivery Review</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getDeliveryReview()
     * @generated
     */
    EClass DELIVERY_REVIEW = eINSTANCE.getDeliveryReview();

    /**
     * The meta object literal for the '<em><b>Base</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DELIVERY_REVIEW__BASE = eINSTANCE.getDeliveryReview_Base();

    /**
     * The meta object literal for the '<em><b>Impact</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DELIVERY_REVIEW__IMPACT = eINSTANCE.getDeliveryReview_Impact();

    /**
     * The meta object literal for the '<em><b>Branch</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DELIVERY_REVIEW__BRANCH = eINSTANCE.getDeliveryReview_Branch();

    /**
     * The meta object literal for the '<em><b>Deliveries</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DELIVERY_REVIEW__DELIVERIES = eINSTANCE.getDeliveryReview_Deliveries();

    /**
     * The meta object literal for the '<em><b>Source Change</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DELIVERY_REVIEW__SOURCE_CHANGE = eINSTANCE.getDeliveryReview_SourceChange();

    /**
     * The meta object literal for the '<em><b>Source Commit</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DELIVERY_REVIEW__SOURCE_COMMIT = eINSTANCE.getDeliveryReview_SourceCommit();

    /**
     * The meta object literal for the '<em><b>Target Commit</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DELIVERY_REVIEW__TARGET_COMMIT = eINSTANCE.getDeliveryReview_TargetCommit();

    /**
     * The meta object literal for the '<em><b>Rebase Count</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DELIVERY_REVIEW__REBASE_COUNT = eINSTANCE.getDeliveryReview_RebaseCount();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl <em>Drop Review</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getDropReview()
     * @generated
     */
    EClass DROP_REVIEW = eINSTANCE.getDropReview();

    /**
     * The meta object literal for the '<em><b>Delivery</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DROP_REVIEW__DELIVERY = eINSTANCE.getDropReview_Delivery();

    /**
     * The meta object literal for the '<em><b>Target Time Stamp</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DROP_REVIEW__TARGET_TIME_STAMP = eINSTANCE.getDropReview_TargetTimeStamp();

    /**
     * The meta object literal for the '<em><b>Drop Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DROP_REVIEW__DROP_TYPE = eINSTANCE.getDropReview_DropType();

    /**
     * The meta object literal for the '<em><b>Drop Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DROP_REVIEW__DROP_LABEL = eINSTANCE.getDropReview_DropLabel();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.CommentStatus <em>Comment Status</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.CommentStatus
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getCommentStatus()
     * @generated
     */
    EEnum COMMENT_STATUS = eINSTANCE.getCommentStatus();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.ReviewStatus <em>Review Status</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.ReviewStatus
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewStatus()
     * @generated
     */
    EEnum REVIEW_STATUS = eINSTANCE.getReviewStatus();

  }

} // ReviewsPackage

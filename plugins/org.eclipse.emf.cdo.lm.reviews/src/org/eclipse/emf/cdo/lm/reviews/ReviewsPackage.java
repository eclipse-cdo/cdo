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

import org.eclipse.emf.cdo.etypes.EtypesPackage;
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
  int COMMENTABLE__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE__COMMENTS = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Commentable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE___GET_ANNOTATION__STRING = EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The number of operations of the '<em>Commentable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENTABLE_OPERATION_COUNT = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 0;

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
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__COMMENTS = COMMENTABLE__COMMENTS;

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
   * The number of operations of the '<em>Comment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT_OPERATION_COUNT = COMMENTABLE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl <em>Review Template</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewTemplate()
   * @generated
   */
  int REVIEW_TEMPLATE = 2;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__ANNOTATIONS = COMMENTABLE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__COMMENTS = COMMENTABLE__COMMENTS;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__TYPE = COMMENTABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__REVIEWERS = COMMENTABLE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Review Template</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE_FEATURE_COUNT = COMMENTABLE_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE___GET_ANNOTATION__STRING = COMMENTABLE___GET_ANNOTATION__STRING;

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
  int REVIEW = 3;

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
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__COMMENTS = LMPackage.BASELINE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__TYPE = LMPackage.BASELINE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__AUTHOR = LMPackage.BASELINE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__REVIEWERS = LMPackage.BASELINE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_FEATURE_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 4;

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
  int DELIVERY_REVIEW = 4;

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
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__COMMENTS = REVIEW__COMMENTS;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__TYPE = REVIEW__TYPE;

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
   * The feature id for the '<em><b>Closed</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__CLOSED = REVIEW_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Source Change</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__SOURCE_CHANGE = REVIEW_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Source Commit</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__SOURCE_COMMIT = REVIEW_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Target Commit</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__TARGET_COMMIT = REVIEW_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Delivery Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW_FEATURE_COUNT = REVIEW_FEATURE_COUNT + 4;

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
  int DROP_REVIEW = 5;

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
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__COMMENTS = REVIEW__COMMENTS;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__TYPE = REVIEW__TYPE;

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
   * The feature id for the '<em><b>Target Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__TARGET_TIME_STAMP = REVIEW_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Drop Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__DROP_TYPE = REVIEW_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Drop Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW_FEATURE_COUNT = REVIEW_FEATURE_COUNT + 4;

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
  int COMMENT_STATUS = 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.ReviewType <em>Review Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewType
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewType()
   * @generated
   */
  int REVIEW_TYPE = 7;

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
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.ReviewTemplate <em>Review Template</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Review Template</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewTemplate
   * @generated
   */
  EClass getReviewTemplate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.ReviewTemplate#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewTemplate#getType()
   * @see #getReviewTemplate()
   * @generated
   */
  EAttribute getReviewTemplate_Type();

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
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Review#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Review#getType()
   * @see #getReview()
   * @generated
   */
  EAttribute getReview_Type();

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
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview <em>Delivery Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Delivery Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DeliveryReview
   * @generated
   */
  EClass getDeliveryReview();

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
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.DropReview <em>Drop Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Drop Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.DropReview
   * @generated
   */
  EClass getDropReview();

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
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.lm.reviews.CommentStatus <em>Comment Status</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Comment Status</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.CommentStatus
   * @generated
   */
  EEnum getCommentStatus();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.lm.reviews.ReviewType <em>Review Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Review Type</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewType
   * @generated
   */
  EEnum getReviewType();

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
     * The meta object literal for the '<em><b>Comments</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMMENTABLE__COMMENTS = eINSTANCE.getCommentable_Comments();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl <em>Review Template</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewTemplate()
     * @generated
     */
    EClass REVIEW_TEMPLATE = eINSTANCE.getReviewTemplate();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW_TEMPLATE__TYPE = eINSTANCE.getReviewTemplate_Type();

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
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REVIEW__TYPE = eINSTANCE.getReview_Type();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl <em>Delivery Review</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getDeliveryReview()
     * @generated
     */
    EClass DELIVERY_REVIEW = eINSTANCE.getDeliveryReview();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl <em>Drop Review</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getDropReview()
     * @generated
     */
    EClass DROP_REVIEW = eINSTANCE.getDropReview();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.CommentStatus <em>Comment Status</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.CommentStatus
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getCommentStatus()
     * @generated
     */
    EEnum COMMENT_STATUS = eINSTANCE.getCommentStatus();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.ReviewType <em>Review Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.ReviewType
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewType()
     * @generated
     */
    EEnum REVIEW_TYPE = eINSTANCE.getReviewType();

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

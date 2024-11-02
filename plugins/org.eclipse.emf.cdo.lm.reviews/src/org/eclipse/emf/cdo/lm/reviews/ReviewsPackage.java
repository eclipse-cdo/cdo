/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.ecore.EDataType;
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

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.Authorable <em>Authorable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.Authorable
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getAuthorable()
   * @generated
   */
  int AUTHORABLE = 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE__ANNOTATIONS = LMPackage.SYSTEM_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Text</b></em>' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE__TEXT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE__AUTHOR = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Creation Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE__CREATION_TIME = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Edit Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE__EDIT_TIME = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Authorable</em>' class.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE_FEATURE_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 4;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE___GET_ANNOTATION__STRING = LMPackage.SYSTEM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE___GET_SYSTEM = LMPackage.SYSTEM_ELEMENT___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Authorable</em>' class.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AUTHORABLE_OPERATION_COUNT = LMPackage.SYSTEM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl <em>Topic Container</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getTopicContainer()
   * @generated
   */
  int TOPIC_CONTAINER = 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER__ANNOTATIONS = LMPackage.SYSTEM_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER__REVIEW = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Topics</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER__TOPICS = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER__COMMENTS = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Topic Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER__TOPIC_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER__UNRESOLVED_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER__RESOLVED_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Topic Container</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER_FEATURE_COUNT = LMPackage.SYSTEM_ELEMENT_FEATURE_COUNT + 6;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER___GET_ANNOTATION__STRING = LMPackage.SYSTEM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER___GET_SYSTEM = LMPackage.SYSTEM_ELEMENT___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Topic Container</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_CONTAINER_OPERATION_COUNT = LMPackage.SYSTEM_ELEMENT_OPERATION_COUNT + 0;

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
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl <em>Comment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getComment()
   * @generated
   */
  int COMMENT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl <em>Topic</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getTopic()
   * @generated
   */
  int TOPIC = 2;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__ANNOTATIONS = TOPIC_CONTAINER__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__REVIEW = TOPIC_CONTAINER__REVIEW;

  /**
   * The feature id for the '<em><b>Topics</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__TOPICS = TOPIC_CONTAINER__TOPICS;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__COMMENTS = TOPIC_CONTAINER__COMMENTS;

  /**
   * The feature id for the '<em><b>Topic Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__TOPIC_COUNT = TOPIC_CONTAINER__TOPIC_COUNT;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__UNRESOLVED_COUNT = TOPIC_CONTAINER__UNRESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__RESOLVED_COUNT = TOPIC_CONTAINER__RESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Text</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__TEXT = TOPIC_CONTAINER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__AUTHOR = TOPIC_CONTAINER_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Creation Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__CREATION_TIME = TOPIC_CONTAINER_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Edit Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__EDIT_TIME = TOPIC_CONTAINER_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Heading</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__HEADING = TOPIC_CONTAINER_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Model Reference</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__MODEL_REFERENCE = TOPIC_CONTAINER_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Status</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__STATUS = TOPIC_CONTAINER_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Container</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__CONTAINER = TOPIC_CONTAINER_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Parent Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__PARENT_HEADING = TOPIC_CONTAINER_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>Previous Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__PREVIOUS_HEADING = TOPIC_CONTAINER_FEATURE_COUNT + 9;

  /**
   * The feature id for the '<em><b>Next Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__NEXT_HEADING = TOPIC_CONTAINER_FEATURE_COUNT + 10;

  /**
   * The feature id for the '<em><b>Outline Number</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__OUTLINE_NUMBER = TOPIC_CONTAINER_FEATURE_COUNT + 11;

  /**
   * The feature id for the '<em><b>Parent Index</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC__PARENT_INDEX = TOPIC_CONTAINER_FEATURE_COUNT + 12;

  /**
   * The number of structural features of the '<em>Topic</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_FEATURE_COUNT = TOPIC_CONTAINER_FEATURE_COUNT + 13;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC___GET_ANNOTATION__STRING = TOPIC_CONTAINER___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC___GET_SYSTEM = TOPIC_CONTAINER___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Topic</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOPIC_OPERATION_COUNT = TOPIC_CONTAINER_OPERATION_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__ANNOTATIONS = AUTHORABLE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Text</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__TEXT = AUTHORABLE__TEXT;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__AUTHOR = AUTHORABLE__AUTHOR;

  /**
   * The feature id for the '<em><b>Creation Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__CREATION_TIME = AUTHORABLE__CREATION_TIME;

  /**
   * The feature id for the '<em><b>Edit Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__EDIT_TIME = AUTHORABLE__EDIT_TIME;

  /**
   * The feature id for the '<em><b>Container</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__CONTAINER = AUTHORABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__REVIEW = AUTHORABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Reply To</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT__REPLY_TO = AUTHORABLE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Comment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT_FEATURE_COUNT = AUTHORABLE_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT___GET_ANNOTATION__STRING = AUTHORABLE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT___GET_SYSTEM = AUTHORABLE___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Comment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMMENT_OPERATION_COUNT = AUTHORABLE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl <em>Review Template</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewTemplate()
   * @generated
   */
  int REVIEW_TEMPLATE = 4;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__ANNOTATIONS = TOPIC_CONTAINER__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__REVIEW = TOPIC_CONTAINER__REVIEW;

  /**
   * The feature id for the '<em><b>Topics</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__TOPICS = TOPIC_CONTAINER__TOPICS;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__COMMENTS = TOPIC_CONTAINER__COMMENTS;

  /**
   * The feature id for the '<em><b>Topic Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__TOPIC_COUNT = TOPIC_CONTAINER__TOPIC_COUNT;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__UNRESOLVED_COUNT = TOPIC_CONTAINER__UNRESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__RESOLVED_COUNT = TOPIC_CONTAINER__RESOLVED_COUNT;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE__REVIEWERS = TOPIC_CONTAINER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Review Template</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE_FEATURE_COUNT = TOPIC_CONTAINER_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE___GET_ANNOTATION__STRING = TOPIC_CONTAINER___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE___GET_SYSTEM = TOPIC_CONTAINER___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Review Template</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_TEMPLATE_OPERATION_COUNT = TOPIC_CONTAINER_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl <em>Review</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReview()
   * @generated
   */
  int REVIEW = 5;

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
   * The feature id for the '<em><b>Topics</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__TOPICS = LMPackage.BASELINE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__COMMENTS = LMPackage.BASELINE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Topic Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__TOPIC_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__UNRESOLVED_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__RESOLVED_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__ID = LMPackage.BASELINE_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__AUTHOR = LMPackage.BASELINE_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Reviewers</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__REVIEWERS = LMPackage.BASELINE_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>Status</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW__STATUS = LMPackage.BASELINE_FEATURE_COUNT + 9;

  /**
   * The number of structural features of the '<em>Review</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REVIEW_FEATURE_COUNT = LMPackage.BASELINE_FEATURE_COUNT + 10;

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
  int DELIVERY_REVIEW = 6;

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
   * The feature id for the '<em><b>Topics</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__TOPICS = REVIEW__TOPICS;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__COMMENTS = REVIEW__COMMENTS;

  /**
   * The feature id for the '<em><b>Topic Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY_REVIEW__TOPIC_COUNT = REVIEW__TOPIC_COUNT;

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
  int DROP_REVIEW = 7;

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
   * The feature id for the '<em><b>Topics</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__TOPICS = REVIEW__TOPICS;

  /**
   * The feature id for the '<em><b>Comments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__COMMENTS = REVIEW__COMMENTS;

  /**
   * The feature id for the '<em><b>Topic Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_REVIEW__TOPIC_COUNT = REVIEW__TOPIC_COUNT;

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
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.reviews.TopicStatus <em>Topic Status</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.TopicStatus
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getTopicStatus()
   * @generated
   */
  int TOPIC_STATUS = 9;

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
   * The meta object id for the '<em>Model Reference</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.reviews.ModelReference
   * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getModelReference()
   * @generated
   */
  int MODEL_REFERENCE = 10;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.Authorable <em>Authorable</em>}'.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Authorable</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Authorable
   * @generated
   */
  EClass getAuthorable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getText <em>Text</em>}'.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Text</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Authorable#getText()
   * @see #getAuthorable()
   * @generated
   */
  EAttribute getAuthorable_Text();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getAuthor <em>Author</em>}'.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Author</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Authorable#getAuthor()
   * @see #getAuthorable()
   * @generated
   */
  EAttribute getAuthorable_Author();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getCreationTime <em>Creation Time</em>}'.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Creation Time</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Authorable#getCreationTime()
   * @see #getAuthorable()
   * @generated
   */
  EAttribute getAuthorable_CreationTime();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getEditTime <em>Edit Time</em>}'.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Edit Time</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Authorable#getEditTime()
   * @see #getAuthorable()
   * @generated
   */
  EAttribute getAuthorable_EditTime();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer <em>Topic Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Topic Container</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer
   * @generated
   */
  EClass getTopicContainer();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getComments <em>Comments</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Comments</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getComments()
   * @see #getTopicContainer()
   * @generated
   */
  EReference getTopicContainer_Comments();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopicCount <em>Topic Count</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Topic Count</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopicCount()
   * @see #getTopicContainer()
   * @generated
   */
  EAttribute getTopicContainer_TopicCount();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getUnresolvedCount <em>Unresolved Count</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Unresolved Count</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getUnresolvedCount()
   * @see #getTopicContainer()
   * @generated
   */
  EAttribute getTopicContainer_UnresolvedCount();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getResolvedCount <em>Resolved Count</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Resolved Count</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getResolvedCount()
   * @see #getTopicContainer()
   * @generated
   */
  EAttribute getTopicContainer_ResolvedCount();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopics <em>Topics</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Topics</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopics()
   * @see #getTopicContainer()
   * @generated
   */
  EReference getTopicContainer_Topics();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getReview <em>Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getReview()
   * @see #getTopicContainer()
   * @generated
   */
  EReference getTopicContainer_Review();

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
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getContainer <em>Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Container</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getContainer()
   * @see #getComment()
   * @generated
   */
  EReference getComment_Container();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getReplyTo <em>Reply To</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Reply To</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getReplyTo()
   * @see #getComment()
   * @generated
   */
  EReference getComment_ReplyTo();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getReview <em>Review</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Review</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getReview()
   * @see #getComment()
   * @generated
   */
  EReference getComment_Review();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.reviews.Topic <em>Topic</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Topic</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic
   * @generated
   */
  EClass getTopic();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Topic#isHeading <em>Heading</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Heading</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#isHeading()
   * @see #getTopic()
   * @generated
   */
  EAttribute getTopic_Heading();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getModelReference <em>Model Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Model Reference</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getModelReference()
   * @see #getTopic()
   * @generated
   */
  EAttribute getTopic_ModelReference();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getStatus <em>Status</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Status</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getStatus()
   * @see #getTopic()
   * @generated
   */
  EAttribute getTopic_Status();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getContainer <em>Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Container</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getContainer()
   * @see #getTopic()
   * @generated
   */
  EReference getTopic_Container();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getParentHeading <em>Parent Heading</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Parent Heading</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getParentHeading()
   * @see #getTopic()
   * @generated
   */
  EReference getTopic_ParentHeading();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getPreviousHeading <em>Previous Heading</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Previous Heading</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getPreviousHeading()
   * @see #getTopic()
   * @generated
   */
  EReference getTopic_PreviousHeading();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getNextHeading <em>Next Heading</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Next Heading</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getNextHeading()
   * @see #getTopic()
   * @generated
   */
  EReference getTopic_NextHeading();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getOutlineNumber <em>Outline Number</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Outline Number</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getOutlineNumber()
   * @see #getTopic()
   * @generated
   */
  EAttribute getTopic_OutlineNumber();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getParentIndex <em>Parent Index</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Parent Index</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getParentIndex()
   * @see #getTopic()
   * @generated
   */
  EAttribute getTopic_ParentIndex();

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
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.lm.reviews.TopicStatus <em>Topic Status</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Topic Status</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicStatus
   * @generated
   */
  EEnum getTopicStatus();

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
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.lm.reviews.ModelReference <em>Model Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Model Reference</em>'.
   * @see org.eclipse.emf.cdo.lm.reviews.ModelReference
   * @model instanceClass="org.eclipse.emf.cdo.lm.reviews.ModelReference"
   * @generated
   */
  EDataType getModelReference();

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
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.Authorable <em>Authorable</em>}' class.
     * <!-- begin-user-doc -->
     * @since 1.2
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.Authorable
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getAuthorable()
     * @generated
     */
    EClass AUTHORABLE = eINSTANCE.getAuthorable();

    /**
     * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * @since 1.2
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AUTHORABLE__TEXT = eINSTANCE.getAuthorable_Text();

    /**
     * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * @since 1.2
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AUTHORABLE__AUTHOR = eINSTANCE.getAuthorable_Author();

    /**
     * The meta object literal for the '<em><b>Creation Time</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * @since 1.2
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AUTHORABLE__CREATION_TIME = eINSTANCE.getAuthorable_CreationTime();

    /**
     * The meta object literal for the '<em><b>Edit Time</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * @since 1.2
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AUTHORABLE__EDIT_TIME = eINSTANCE.getAuthorable_EditTime();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl <em>Topic Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getTopicContainer()
     * @generated
     */
    EClass TOPIC_CONTAINER = eINSTANCE.getTopicContainer();

    /**
     * The meta object literal for the '<em><b>Comments</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOPIC_CONTAINER__COMMENTS = eINSTANCE.getTopicContainer_Comments();

    /**
     * The meta object literal for the '<em><b>Topic Count</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC_CONTAINER__TOPIC_COUNT = eINSTANCE.getTopicContainer_TopicCount();

    /**
     * The meta object literal for the '<em><b>Unresolved Count</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC_CONTAINER__UNRESOLVED_COUNT = eINSTANCE.getTopicContainer_UnresolvedCount();

    /**
     * The meta object literal for the '<em><b>Resolved Count</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC_CONTAINER__RESOLVED_COUNT = eINSTANCE.getTopicContainer_ResolvedCount();

    /**
     * The meta object literal for the '<em><b>Topics</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOPIC_CONTAINER__TOPICS = eINSTANCE.getTopicContainer_Topics();

    /**
     * The meta object literal for the '<em><b>Review</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOPIC_CONTAINER__REVIEW = eINSTANCE.getTopicContainer_Review();

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
     * The meta object literal for the '<em><b>Container</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMMENT__CONTAINER = eINSTANCE.getComment_Container();

    /**
     * The meta object literal for the '<em><b>Reply To</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMMENT__REPLY_TO = eINSTANCE.getComment_ReplyTo();

    /**
     * The meta object literal for the '<em><b>Review</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMMENT__REVIEW = eINSTANCE.getComment_Review();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl <em>Topic</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getTopic()
     * @generated
     */
    EClass TOPIC = eINSTANCE.getTopic();

    /**
     * The meta object literal for the '<em><b>Heading</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC__HEADING = eINSTANCE.getTopic_Heading();

    /**
     * The meta object literal for the '<em><b>Model Reference</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC__MODEL_REFERENCE = eINSTANCE.getTopic_ModelReference();

    /**
     * The meta object literal for the '<em><b>Status</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC__STATUS = eINSTANCE.getTopic_Status();

    /**
     * The meta object literal for the '<em><b>Container</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOPIC__CONTAINER = eINSTANCE.getTopic_Container();

    /**
     * The meta object literal for the '<em><b>Parent Heading</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOPIC__PARENT_HEADING = eINSTANCE.getTopic_ParentHeading();

    /**
     * The meta object literal for the '<em><b>Previous Heading</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOPIC__PREVIOUS_HEADING = eINSTANCE.getTopic_PreviousHeading();

    /**
     * The meta object literal for the '<em><b>Next Heading</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TOPIC__NEXT_HEADING = eINSTANCE.getTopic_NextHeading();

    /**
     * The meta object literal for the '<em><b>Outline Number</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC__OUTLINE_NUMBER = eINSTANCE.getTopic_OutlineNumber();

    /**
     * The meta object literal for the '<em><b>Parent Index</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOPIC__PARENT_INDEX = eINSTANCE.getTopic_ParentIndex();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.TopicStatus <em>Topic Status</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.TopicStatus
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getTopicStatus()
     * @generated
     */
    EEnum TOPIC_STATUS = eINSTANCE.getTopicStatus();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.reviews.ReviewStatus <em>Review Status</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.ReviewStatus
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getReviewStatus()
     * @generated
     */
    EEnum REVIEW_STATUS = eINSTANCE.getReviewStatus();

    /**
     * The meta object literal for the '<em>Model Reference</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.reviews.ModelReference
     * @see org.eclipse.emf.cdo.lm.reviews.impl.ReviewsPackageImpl#getModelReference()
     * @generated
     */
    EDataType MODEL_REFERENCE = eINSTANCE.getModelReference();

  }

} // ReviewsPackage

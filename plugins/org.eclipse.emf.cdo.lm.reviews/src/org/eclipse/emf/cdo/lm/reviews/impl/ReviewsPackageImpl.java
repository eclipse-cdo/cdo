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
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.CommentStatus;
import org.eclipse.emf.cdo.lm.reviews.Commentable;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.Heading;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.ReviewTemplate;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReviewsPackageImpl extends EPackageImpl implements ReviewsPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass commentableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass commentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass headingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass reviewTemplateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass reviewEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass deliveryReviewEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dropReviewEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum commentStatusEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum reviewStatusEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private ReviewsPackageImpl()
  {
    super(eNS_URI, ReviewsFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link ReviewsPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static ReviewsPackage init()
  {
    if (isInited)
    {
      return (ReviewsPackage)EPackage.Registry.INSTANCE.getEPackage(ReviewsPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredReviewsPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    ReviewsPackageImpl theReviewsPackage = registeredReviewsPackage instanceof ReviewsPackageImpl ? (ReviewsPackageImpl)registeredReviewsPackage
        : new ReviewsPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EtypesPackage.eINSTANCE.eClass();
    LMPackage.eINSTANCE.eClass();
    ModulesPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theReviewsPackage.createPackageContents();

    // Initialize created meta-data
    theReviewsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theReviewsPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(ReviewsPackage.eNS_URI, theReviewsPackage);
    return theReviewsPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCommentable()
  {
    return commentableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCommentable_Review()
  {
    return (EReference)commentableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCommentable_Comments()
  {
    return (EReference)commentableEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getCommentable_CommentCount()
  {
    return (EAttribute)commentableEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getCommentable_UnresolvedCount()
  {
    return (EAttribute)commentableEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getCommentable_ResolvedCount()
  {
    return (EAttribute)commentableEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getComment()
  {
    return commentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getComment_Commentable()
  {
    return (EReference)commentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getComment_ParentHeading()
  {
    return (EReference)commentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getComment_Author()
  {
    return (EAttribute)commentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getComment_Text()
  {
    return (EAttribute)commentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getComment_Status()
  {
    return (EAttribute)commentEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getHeading()
  {
    return headingEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getHeading_PreviousHeading()
  {
    return (EReference)headingEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getHeading_NextHeading()
  {
    return (EReference)headingEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getHeading_ParentIndex()
  {
    return (EAttribute)headingEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getHeading_OutlineNumber()
  {
    return (EAttribute)headingEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getReviewTemplate()
  {
    return reviewTemplateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getReviewTemplate_Reviewers()
  {
    return (EAttribute)reviewTemplateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getReview()
  {
    return reviewEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getReview_Id()
  {
    return (EAttribute)reviewEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getReview_Author()
  {
    return (EAttribute)reviewEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getReview_Reviewers()
  {
    return (EAttribute)reviewEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getReview_Status()
  {
    return (EAttribute)reviewEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDeliveryReview()
  {
    return deliveryReviewEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDeliveryReview_Base()
  {
    return (EReference)deliveryReviewEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDeliveryReview_Impact()
  {
    return (EAttribute)deliveryReviewEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDeliveryReview_Branch()
  {
    return (EAttribute)deliveryReviewEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDeliveryReview_Deliveries()
  {
    return (EReference)deliveryReviewEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDeliveryReview_SourceChange()
  {
    return (EReference)deliveryReviewEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDeliveryReview_SourceCommit()
  {
    return (EAttribute)deliveryReviewEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDeliveryReview_TargetCommit()
  {
    return (EAttribute)deliveryReviewEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDeliveryReview_RebaseCount()
  {
    return (EAttribute)deliveryReviewEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDropReview()
  {
    return dropReviewEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDropReview_Delivery()
  {
    return (EReference)dropReviewEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDropReview_TargetTimeStamp()
  {
    return (EAttribute)dropReviewEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDropReview_DropType()
  {
    return (EReference)dropReviewEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDropReview_DropLabel()
  {
    return (EAttribute)dropReviewEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getCommentStatus()
  {
    return commentStatusEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getReviewStatus()
  {
    return reviewStatusEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ReviewsFactory getReviewsFactory()
  {
    return (ReviewsFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    commentableEClass = createEClass(COMMENTABLE);
    createEReference(commentableEClass, COMMENTABLE__REVIEW);
    createEReference(commentableEClass, COMMENTABLE__COMMENTS);
    createEAttribute(commentableEClass, COMMENTABLE__COMMENT_COUNT);
    createEAttribute(commentableEClass, COMMENTABLE__UNRESOLVED_COUNT);
    createEAttribute(commentableEClass, COMMENTABLE__RESOLVED_COUNT);

    commentEClass = createEClass(COMMENT);
    createEReference(commentEClass, COMMENT__COMMENTABLE);
    createEReference(commentEClass, COMMENT__PARENT_HEADING);
    createEAttribute(commentEClass, COMMENT__AUTHOR);
    createEAttribute(commentEClass, COMMENT__TEXT);
    createEAttribute(commentEClass, COMMENT__STATUS);

    headingEClass = createEClass(HEADING);
    createEReference(headingEClass, HEADING__PREVIOUS_HEADING);
    createEReference(headingEClass, HEADING__NEXT_HEADING);
    createEAttribute(headingEClass, HEADING__PARENT_INDEX);
    createEAttribute(headingEClass, HEADING__OUTLINE_NUMBER);

    reviewTemplateEClass = createEClass(REVIEW_TEMPLATE);
    createEAttribute(reviewTemplateEClass, REVIEW_TEMPLATE__REVIEWERS);

    reviewEClass = createEClass(REVIEW);
    createEAttribute(reviewEClass, REVIEW__ID);
    createEAttribute(reviewEClass, REVIEW__AUTHOR);
    createEAttribute(reviewEClass, REVIEW__REVIEWERS);
    createEAttribute(reviewEClass, REVIEW__STATUS);

    deliveryReviewEClass = createEClass(DELIVERY_REVIEW);
    createEReference(deliveryReviewEClass, DELIVERY_REVIEW__BASE);
    createEAttribute(deliveryReviewEClass, DELIVERY_REVIEW__IMPACT);
    createEAttribute(deliveryReviewEClass, DELIVERY_REVIEW__BRANCH);
    createEReference(deliveryReviewEClass, DELIVERY_REVIEW__DELIVERIES);
    createEReference(deliveryReviewEClass, DELIVERY_REVIEW__SOURCE_CHANGE);
    createEAttribute(deliveryReviewEClass, DELIVERY_REVIEW__SOURCE_COMMIT);
    createEAttribute(deliveryReviewEClass, DELIVERY_REVIEW__TARGET_COMMIT);
    createEAttribute(deliveryReviewEClass, DELIVERY_REVIEW__REBASE_COUNT);

    dropReviewEClass = createEClass(DROP_REVIEW);
    createEReference(dropReviewEClass, DROP_REVIEW__DELIVERY);
    createEAttribute(dropReviewEClass, DROP_REVIEW__TARGET_TIME_STAMP);
    createEReference(dropReviewEClass, DROP_REVIEW__DROP_TYPE);
    createEAttribute(dropReviewEClass, DROP_REVIEW__DROP_LABEL);

    // Create enums
    commentStatusEEnum = createEEnum(COMMENT_STATUS);
    reviewStatusEEnum = createEEnum(REVIEW_STATUS);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    LMPackage theLMPackage = (LMPackage)EPackage.Registry.INSTANCE.getEPackage(LMPackage.eNS_URI);
    EtypesPackage theEtypesPackage = (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    commentableEClass.getESuperTypes().add(theLMPackage.getSystemElement());
    commentEClass.getESuperTypes().add(getCommentable());
    headingEClass.getESuperTypes().add(getComment());
    reviewTemplateEClass.getESuperTypes().add(getCommentable());
    reviewEClass.getESuperTypes().add(theLMPackage.getBaseline());
    reviewEClass.getESuperTypes().add(getCommentable());
    deliveryReviewEClass.getESuperTypes().add(getReview());
    deliveryReviewEClass.getESuperTypes().add(theLMPackage.getFloatingBaseline());
    dropReviewEClass.getESuperTypes().add(getReview());
    dropReviewEClass.getESuperTypes().add(theLMPackage.getFixedBaseline());

    // Initialize classes, features, and operations; add parameters
    initEClass(commentableEClass, Commentable.class, "Commentable", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCommentable_Review(), getReview(), null, "review", null, 1, 1, Commentable.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getCommentable_Comments(), getComment(), getComment_Commentable(), "comments", null, 0, -1, Commentable.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCommentable_CommentCount(), ecorePackage.getEInt(), "commentCount", null, 0, 1, Commentable.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getCommentable_UnresolvedCount(), ecorePackage.getEInt(), "unresolvedCount", null, 0, 1, Commentable.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getCommentable_ResolvedCount(), ecorePackage.getEInt(), "resolvedCount", null, 0, 1, Commentable.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(commentEClass, Comment.class, "Comment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getComment_Commentable(), getCommentable(), getCommentable_Comments(), "commentable", null, 1, 1, Comment.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComment_ParentHeading(), getHeading(), null, "parentHeading", null, 0, 1, Comment.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getComment_Author(), ecorePackage.getEString(), "author", null, 0, 1, Comment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComment_Text(), ecorePackage.getEString(), "text", null, 0, 1, Comment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComment_Status(), getCommentStatus(), "status", "None", 0, 1, Comment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(headingEClass, Heading.class, "Heading", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getHeading_PreviousHeading(), getHeading(), null, "previousHeading", null, 0, 1, Heading.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getHeading_NextHeading(), getHeading(), null, "nextHeading", null, 0, 1, Heading.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getHeading_ParentIndex(), ecorePackage.getEInt(), "parentIndex", null, 0, 1, Heading.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getHeading_OutlineNumber(), ecorePackage.getEString(), "outlineNumber", null, 0, 1, Heading.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(reviewTemplateEClass, ReviewTemplate.class, "ReviewTemplate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getReviewTemplate_Reviewers(), ecorePackage.getEString(), "reviewers", null, 0, -1, ReviewTemplate.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(reviewEClass, Review.class, "Review", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getReview_Id(), ecorePackage.getEInt(), "id", null, 1, 1, Review.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReview_Author(), ecorePackage.getEString(), "author", null, 1, 1, Review.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReview_Reviewers(), ecorePackage.getEString(), "reviewers", null, 1, -1, Review.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getReview_Status(), getReviewStatus(), "status", null, 1, 1, Review.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(deliveryReviewEClass, DeliveryReview.class, "DeliveryReview", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDeliveryReview_Base(), theLMPackage.getFixedBaseline(), null, "base", null, 1, 1, DeliveryReview.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDeliveryReview_Impact(), theLMPackage.getImpact(), "impact", null, 1, 1, DeliveryReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDeliveryReview_Branch(), theEtypesPackage.getBranchRef(), "branch", "", 1, 1, DeliveryReview.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDeliveryReview_Deliveries(), theLMPackage.getDelivery(), null, "deliveries", null, 0, -1, DeliveryReview.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDeliveryReview_SourceChange(), theLMPackage.getChange(), null, "sourceChange", null, 1, 1, DeliveryReview.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDeliveryReview_SourceCommit(), ecorePackage.getELong(), "sourceCommit", null, 1, 1, DeliveryReview.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDeliveryReview_TargetCommit(), ecorePackage.getELong(), "targetCommit", null, 1, 1, DeliveryReview.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDeliveryReview_RebaseCount(), ecorePackage.getEInt(), "rebaseCount", null, 0, 1, DeliveryReview.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dropReviewEClass, DropReview.class, "DropReview", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDropReview_Delivery(), theLMPackage.getDelivery(), null, "delivery", null, 1, 1, DropReview.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDropReview_TargetTimeStamp(), ecorePackage.getELong(), "targetTimeStamp", null, 1, 1, DropReview.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getDropReview_DropType(), theLMPackage.getDropType(), null, "dropType", null, 1, 1, DropReview.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDropReview_DropLabel(), ecorePackage.getEString(), "dropLabel", null, 0, 1, DropReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(commentStatusEEnum, CommentStatus.class, "CommentStatus");
    addEEnumLiteral(commentStatusEEnum, CommentStatus.NONE);
    addEEnumLiteral(commentStatusEEnum, CommentStatus.UNRESOLVED);
    addEEnumLiteral(commentStatusEEnum, CommentStatus.RESOLVED);

    initEEnum(reviewStatusEEnum, ReviewStatus.class, "ReviewStatus");
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.NEW);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.SOURCE_OUTDATED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.TARGET_OUTDATED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.OUTDATED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.SUBMITTED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.ABANDONED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.RESTORING);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.DELETED);

    // Create resource
    createResource(eNS_URI);
  }

} // ReviewsPackageImpl

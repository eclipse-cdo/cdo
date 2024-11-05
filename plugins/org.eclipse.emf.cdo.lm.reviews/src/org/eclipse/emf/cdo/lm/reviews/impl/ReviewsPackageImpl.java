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
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;
import org.eclipse.emf.cdo.lm.reviews.Authorable;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.ModelReference;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.ReviewTemplate;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;
import org.eclipse.emf.cdo.lm.reviews.TopicStatus;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
  private EClass authorableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass topicContainerEClass = null;

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
  private EClass topicEClass = null;

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
  private EEnum topicStatusEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum reviewStatusEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType modelReferenceEDataType = null;

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
  public EClass getAuthorable()
  {
    return authorableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAuthorable_Id()
  {
    return (EAttribute)authorableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAuthorable_Text()
  {
    return (EAttribute)authorableEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAuthorable_Author()
  {
    return (EAttribute)authorableEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAuthorable_CreationTime()
  {
    return (EAttribute)authorableEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getAuthorable_EditTime()
  {
    return (EAttribute)authorableEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTopicContainer()
  {
    return topicContainerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTopicContainer_Comments()
  {
    return (EReference)topicContainerEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopicContainer_TopicCount()
  {
    return (EAttribute)topicContainerEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopicContainer_UnresolvedCount()
  {
    return (EAttribute)topicContainerEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopicContainer_ResolvedCount()
  {
    return (EAttribute)topicContainerEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTopicContainer_Topics()
  {
    return (EReference)topicContainerEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTopicContainer_Review()
  {
    return (EReference)topicContainerEClass.getEStructuralFeatures().get(0);
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
  public EReference getComment_Container()
  {
    return (EReference)commentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getComment_ReplyTo()
  {
    return (EReference)commentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getComment_Review()
  {
    return (EReference)commentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTopic()
  {
    return topicEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopic_Heading()
  {
    return (EAttribute)topicEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopic_ModelReference()
  {
    return (EAttribute)topicEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopic_Status()
  {
    return (EAttribute)topicEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTopic_Container()
  {
    return (EReference)topicEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTopic_ParentHeading()
  {
    return (EReference)topicEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTopic_PreviousHeading()
  {
    return (EReference)topicEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTopic_NextHeading()
  {
    return (EReference)topicEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopic_OutlineNumber()
  {
    return (EAttribute)topicEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTopic_ParentIndex()
  {
    return (EAttribute)topicEClass.getEStructuralFeatures().get(8);
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
  public EEnum getTopicStatus()
  {
    return topicStatusEEnum;
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
  public EDataType getModelReference()
  {
    return modelReferenceEDataType;
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
    authorableEClass = createEClass(AUTHORABLE);
    createEAttribute(authorableEClass, AUTHORABLE__ID);
    createEAttribute(authorableEClass, AUTHORABLE__TEXT);
    createEAttribute(authorableEClass, AUTHORABLE__AUTHOR);
    createEAttribute(authorableEClass, AUTHORABLE__CREATION_TIME);
    createEAttribute(authorableEClass, AUTHORABLE__EDIT_TIME);

    topicContainerEClass = createEClass(TOPIC_CONTAINER);
    createEReference(topicContainerEClass, TOPIC_CONTAINER__REVIEW);
    createEReference(topicContainerEClass, TOPIC_CONTAINER__TOPICS);
    createEReference(topicContainerEClass, TOPIC_CONTAINER__COMMENTS);
    createEAttribute(topicContainerEClass, TOPIC_CONTAINER__TOPIC_COUNT);
    createEAttribute(topicContainerEClass, TOPIC_CONTAINER__UNRESOLVED_COUNT);
    createEAttribute(topicContainerEClass, TOPIC_CONTAINER__RESOLVED_COUNT);

    topicEClass = createEClass(TOPIC);
    createEAttribute(topicEClass, TOPIC__HEADING);
    createEAttribute(topicEClass, TOPIC__MODEL_REFERENCE);
    createEAttribute(topicEClass, TOPIC__STATUS);
    createEReference(topicEClass, TOPIC__CONTAINER);
    createEReference(topicEClass, TOPIC__PARENT_HEADING);
    createEReference(topicEClass, TOPIC__PREVIOUS_HEADING);
    createEReference(topicEClass, TOPIC__NEXT_HEADING);
    createEAttribute(topicEClass, TOPIC__OUTLINE_NUMBER);
    createEAttribute(topicEClass, TOPIC__PARENT_INDEX);

    commentEClass = createEClass(COMMENT);
    createEReference(commentEClass, COMMENT__CONTAINER);
    createEReference(commentEClass, COMMENT__REVIEW);
    createEReference(commentEClass, COMMENT__REPLY_TO);

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
    reviewStatusEEnum = createEEnum(REVIEW_STATUS);
    topicStatusEEnum = createEEnum(TOPIC_STATUS);

    // Create data types
    modelReferenceEDataType = createEDataType(MODEL_REFERENCE);
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
    authorableEClass.getESuperTypes().add(theLMPackage.getSystemElement());
    topicContainerEClass.getESuperTypes().add(theLMPackage.getSystemElement());
    topicEClass.getESuperTypes().add(getTopicContainer());
    topicEClass.getESuperTypes().add(getAuthorable());
    commentEClass.getESuperTypes().add(getAuthorable());
    reviewTemplateEClass.getESuperTypes().add(getTopicContainer());
    reviewEClass.getESuperTypes().add(theLMPackage.getBaseline());
    reviewEClass.getESuperTypes().add(getTopicContainer());
    deliveryReviewEClass.getESuperTypes().add(getReview());
    deliveryReviewEClass.getESuperTypes().add(theLMPackage.getFloatingBaseline());
    dropReviewEClass.getESuperTypes().add(getReview());
    dropReviewEClass.getESuperTypes().add(theLMPackage.getFixedBaseline());

    // Initialize classes, features, and operations; add parameters
    initEClass(authorableEClass, Authorable.class, "Authorable", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAuthorable_Id(), ecorePackage.getEInt(), "id", null, 1, 1, Authorable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAuthorable_Text(), ecorePackage.getEString(), "text", null, 1, 1, Authorable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAuthorable_Author(), ecorePackage.getEString(), "author", null, 1, 1, Authorable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAuthorable_CreationTime(), ecorePackage.getELong(), "creationTime", null, 0, 1, Authorable.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAuthorable_EditTime(), ecorePackage.getELong(), "editTime", null, 0, 1, Authorable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(topicContainerEClass, TopicContainer.class, "TopicContainer", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTopicContainer_Review(), getReview(), null, "review", null, 0, 1, TopicContainer.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getTopicContainer_Topics(), getTopic(), getTopic_Container(), "topics", null, 0, -1, TopicContainer.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTopicContainer_Comments(), getComment(), getComment_Container(), "comments", null, 0, -1, TopicContainer.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTopicContainer_TopicCount(), ecorePackage.getEInt(), "topicCount", null, 0, 1, TopicContainer.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getTopicContainer_UnresolvedCount(), ecorePackage.getEInt(), "unresolvedCount", null, 0, 1, TopicContainer.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getTopicContainer_ResolvedCount(), ecorePackage.getEInt(), "resolvedCount", null, 0, 1, TopicContainer.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(topicEClass, Topic.class, "Topic", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTopic_Heading(), ecorePackage.getEBoolean(), "heading", null, 0, 1, Topic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTopic_ModelReference(), getModelReference(), "modelReference", null, 0, 1, Topic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTopic_Status(), getTopicStatus(), "status", null, 0, 1, Topic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTopic_Container(), getTopicContainer(), getTopicContainer_Topics(), "container", null, 1, 1, Topic.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTopic_ParentHeading(), getTopic(), null, "parentHeading", null, 0, 1, Topic.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getTopic_PreviousHeading(), getTopic(), null, "previousHeading", null, 0, 1, Topic.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getTopic_NextHeading(), getTopic(), null, "nextHeading", null, 0, 1, Topic.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getTopic_OutlineNumber(), ecorePackage.getEString(), "outlineNumber", null, 0, 1, Topic.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getTopic_ParentIndex(), ecorePackage.getEInt(), "parentIndex", null, 0, 1, Topic.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(commentEClass, Comment.class, "Comment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getComment_Container(), getTopicContainer(), getTopicContainer_Comments(), "container", null, 1, 1, Comment.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComment_Review(), getReview(), null, "review", null, 0, 1, Comment.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getComment_ReplyTo(), getComment(), null, "replyTo", null, 0, 1, Comment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

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
    initEEnum(reviewStatusEEnum, ReviewStatus.class, "ReviewStatus");
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.NEW);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.SOURCE_OUTDATED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.TARGET_OUTDATED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.OUTDATED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.SUBMITTED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.ABANDONED);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.RESTORING);
    addEEnumLiteral(reviewStatusEEnum, ReviewStatus.DELETED);

    initEEnum(topicStatusEEnum, TopicStatus.class, "TopicStatus");
    addEEnumLiteral(topicStatusEEnum, TopicStatus.NONE);
    addEEnumLiteral(topicStatusEEnum, TopicStatus.UNRESOLVED);
    addEEnumLiteral(topicStatusEEnum, TopicStatus.RESOLVED);

    // Initialize data types
    initEDataType(modelReferenceEDataType, ModelReference.class, "ModelReference", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // ReviewsPackageImpl

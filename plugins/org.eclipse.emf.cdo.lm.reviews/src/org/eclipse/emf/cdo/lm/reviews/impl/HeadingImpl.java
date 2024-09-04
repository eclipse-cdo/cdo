/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.Commentable;
import org.eclipse.emf.cdo.lm.reviews.Heading;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Heading</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl#getPreviousHeading <em>Previous Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl#getNextHeading <em>Next Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl#getParentIndex <em>Parent Index</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.HeadingImpl#getOutlineNumber <em>Outline Number</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HeadingImpl extends CommentImpl implements Heading
{
  /**
   * The default value of the '{@link #getParentIndex() <em>Parent Index</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getParentIndex()
   * @generated
   * @ordered
   */
  protected static final int PARENT_INDEX_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getOutlineNumber() <em>Outline Number</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutlineNumber()
   * @generated
   * @ordered
   */
  protected static final String OUTLINE_NUMBER_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HeadingImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ReviewsPackage.Literals.HEADING;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Heading getPreviousHeading()
  {
    Commentable commentable = getCommentable();
    if (commentable == null)
    {
      return null;
    }

    EList<Comment> parentComments = commentable.getComments();
    int index = parentComments.indexOf(this);
    if (index == -1)
    {
      return null;
    }

    for (int i = index - 1; i >= 0; --i)
    {
      Comment comment = parentComments.get(i);
      if (comment instanceof Heading)
      {
        return (Heading)comment;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Heading getNextHeading()
  {
    Commentable commentable = getCommentable();
    if (commentable == null)
    {
      return null;
    }

    EList<Comment> parentComments = commentable.getComments();
    int index = parentComments.indexOf(this);
    if (index == -1)
    {
      return null;
    }

    for (int i = index + 1, size = parentComments.size(); i < size; ++i)
    {
      Comment comment = parentComments.get(i);
      if (comment instanceof Heading)
      {
        return (Heading)comment;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getParentIndex()
  {
    Commentable commentable = getCommentable();
    if (commentable == null)
    {
      return 1;
    }

    EList<Comment> parentComments = commentable.getComments();
    int index = parentComments.indexOf(this);
    if (index == -1)
    {
      return 1;
    }

    int count = 1;

    for (int i = 0; i < index; ++i)
    {
      Comment comment = parentComments.get(i);
      if (comment instanceof Heading)
      {
        ++count;
      }
    }

    return count;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String getOutlineNumber()
  {
    StringBuilder builder = new StringBuilder();
    formatOutlineNumber(builder, this);
    return builder.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ReviewsPackage.HEADING__PREVIOUS_HEADING:
      return getPreviousHeading();
    case ReviewsPackage.HEADING__NEXT_HEADING:
      return getNextHeading();
    case ReviewsPackage.HEADING__PARENT_INDEX:
      return getParentIndex();
    case ReviewsPackage.HEADING__OUTLINE_NUMBER:
      return getOutlineNumber();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ReviewsPackage.HEADING__PREVIOUS_HEADING:
      return getPreviousHeading() != null;
    case ReviewsPackage.HEADING__NEXT_HEADING:
      return getNextHeading() != null;
    case ReviewsPackage.HEADING__PARENT_INDEX:
      return getParentIndex() != PARENT_INDEX_EDEFAULT;
    case ReviewsPackage.HEADING__OUTLINE_NUMBER:
      return OUTLINE_NUMBER_EDEFAULT == null ? getOutlineNumber() != null : !OUTLINE_NUMBER_EDEFAULT.equals(getOutlineNumber());
    }
    return super.eIsSet(featureID);
  }

  private static void formatOutlineNumber(StringBuilder builder, Heading heading)
  {
    Heading parentHeading = heading.getParentHeading();
    if (parentHeading != null)
    {
      formatOutlineNumber(builder, parentHeading);
    }

    StringUtil.appendSeparator(builder, '.');
    builder.append(heading.getParentIndex());
  }

} // HeadingImpl

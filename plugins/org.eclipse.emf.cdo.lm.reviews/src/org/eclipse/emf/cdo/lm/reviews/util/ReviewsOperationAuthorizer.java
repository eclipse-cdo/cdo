/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.util;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.lm.reviews.Authorable;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.util.LMOperationAuthorizer;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.ContextOperationAuthorization;

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import org.eclipse.emf.ecore.EObject;

import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 1.3
 */
public abstract class ReviewsOperationAuthorizer extends LMOperationAuthorizer implements ReviewsOperations
{
  public ReviewsOperationAuthorizer()
  {
  }

  protected Object authorizeAuthor(CDOSession session, AuthorizableOperation operation)
  {
    EObject context = ContextOperationAuthorization.getContext(operation);
    if (context instanceof Authorable)
    {
      String author = ((Authorable)context).getAuthor();
      return Objects.equals(author, session.getUserID()) ? GRANTED : "Not the author";
    }

    return null;
  }

  public static Review getContextReview(AuthorizableOperation operation)
  {
    EObject object = ContextOperationAuthorization.getContext(operation);
    return EMFUtil.getNearestObject(object, Review.class);
  }

  public static Topic getContextTopic(AuthorizableOperation operation)
  {
    EObject object = ContextOperationAuthorization.getContext(operation);
    return EMFUtil.getNearestObject(object, Topic.class);
  }

  public static Comment getContextComment(AuthorizableOperation operation)
  {
    EObject object = ContextOperationAuthorization.getContext(operation);
    return EMFUtil.getNearestObject(object, Comment.class);
  }
}

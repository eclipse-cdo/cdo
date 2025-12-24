/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.util;

/**
 * @author Eike Stepper
 * @since 1.3
 */
public interface ReviewsOperations
{
  /*
   * Review operations.
   */

  public String CREATE_DELIVERY_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.CreateDeliveryReview".intern();

  public String CREATE_DROP_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.CreateDropReview".intern();

  public String REBASE_TO_TARGET_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.RebaseToTargetReview".intern();

  public String MERGE_FROM_SOURCE_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.MergeFromSourceReview".intern();

  public String SUBMIT_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.SubmitReview".intern();

  public String ABANDON_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.AbandonReview".intern();

  public String RESTORE_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.RestoreReview".intern();

  public String DELETE_REVIEW = "org.eclipse.emf.cdo.lm.reviews.ui.DeleteReview".intern();

  /*
   * Topic operations.
   */

  public String CREATE_TOPIC = "org.eclipse.emf.cdo.lm.reviews.ui.CreateTopic".intern();

  public String MODIFY_TOPIC = "org.eclipse.emf.cdo.lm.reviews.ui.ModifyTopic".intern();

  public String RESOLVE_TOPIC = "org.eclipse.emf.cdo.lm.reviews.ui.ResolveTopic".intern();

  public String UNRESOLVE_TOPIC = "org.eclipse.emf.cdo.lm.reviews.ui.UnresolveTopic".intern();

  public String DELETE_TOPIC = "org.eclipse.emf.cdo.lm.reviews.ui.DeleteTopic".intern();

  /*
   * Comment operations.
   */

  public String CREATE_COMMENT = "org.eclipse.emf.cdo.lm.reviews.ui.CreateComment".intern();

  public String DELETE_COMMENT = "org.eclipse.emf.cdo.lm.reviews.ui.DeleteComment".intern();
}

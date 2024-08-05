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
package org.eclipse.emf.cdo.lm.reviews.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.lm.reviews.Review;

import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Set;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IReviewManagerEvent extends IEvent
{
  @Override
  public IReviewManager getSource();

  public Type getType();

  public CDOID getCDOID();

  public Review getOldReview();

  public Review getNewReview();

  public Set<EStructuralFeature> getChangedFeatures();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    ReviewCreated, ReviewChanged, ReviewDeleted;
  }
}

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

import org.eclipse.emf.cdo.lm.reviews.internal.server.ReviewManager;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.event.INotifier.INotifier2;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IReviewManager extends INotifier2
{
  public AbstractLifecycleManager getLifecycleManager();

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Registry
  {
    public static final Registry INSTANCE = ReviewManager.ReviewManagerRegistry.INSTANCE;

    public IReviewManager getReviewManager(IRepository repository);
  }
}

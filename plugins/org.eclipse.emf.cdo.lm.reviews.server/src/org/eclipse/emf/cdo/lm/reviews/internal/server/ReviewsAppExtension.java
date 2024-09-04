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
package org.eclipse.emf.cdo.lm.reviews.internal.server;

import org.eclipse.emf.cdo.lm.reviews.internal.server.bundle.OM;
import org.eclipse.emf.cdo.lm.reviews.server.IReviewManager;
import org.eclipse.emf.cdo.lm.server.LMAppExtension;
import org.eclipse.emf.cdo.lm.server.XMLLifecycleManager;
import org.eclipse.emf.cdo.spi.server.AppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension4;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Eike Stepper
 */
public class ReviewsAppExtension extends AppExtension implements IAppExtension4
{
  public static final int PRIORITY = PRIORITY_DEFAULT + 100;

  private static final String DEFAULT_REVIEW_MANAGER_TYPE = OMPlatform.INSTANCE.getProperty( //
      "org.eclipse.emf.cdo.lm.reviews.internal.server.ReviewsAppExtension.DEFAULT_REVIEW_MANAGER_TYPE", //
      ReviewManager.DEFAULT_TYPE);

  public ReviewsAppExtension()
  {
  }

  @Override
  public String getName()
  {
    return "Reviews";
  }

  @Override
  public int getPriority()
  {
    return PRIORITY;
  }

  @Override
  protected void start(InternalRepository repository, Element repositoryConfig) throws Exception
  {
    NodeList rmElements = repositoryConfig.getElementsByTagName("reviewManager"); //$NON-NLS-1$
    int length = rmElements.getLength();
    if (length > 1)
    {
      throw new IllegalStateException("At most one review manager must be configured for repository " + repository.getName()); //$NON-NLS-1$
    }

    if (length == 1)
    {
      Element rmElement = (Element)rmElements.item(0);
      configureReviewManager(repository, rmElement);
    }
  }

  @Override
  protected void stop(InternalRepository repository) throws Exception
  {
    IReviewManager reviewManager = ReviewManager.ReviewManagerRegistry.INSTANCE.removeReviewManager(repository);
    if (reviewManager != null)
    {
      OM.LOG.info("Deactivating review manager of repository " + repository.getName());
      LifecycleUtil.deactivate(reviewManager);
    }
  }

  private void configureReviewManager(InternalRepository repository, Element rmElement)
  {
    XMLLifecycleManager lifecycleManager = LMAppExtension.getLifecycleManager(repository);
    if (lifecycleManager == null)
    {
      throw new IllegalStateException("A lifecycle manager must exist for the review manager of repository " + repository.getName()); //$NON-NLS-1$
    }

    ReviewManager reviewManager = createReviewManager(repository, rmElement);
    reviewManager.setLifecycleManager(lifecycleManager);

    OM.LOG.info("Activating review manager of repository " + repository.getName());
    reviewManager.activate();

    ReviewManager.ReviewManagerRegistry.INSTANCE.addReviewManager(repository, reviewManager);
  }

  /**
   * @since 1.2
   */
  protected String getDefaultReviewManagerType()
  {
    return DEFAULT_REVIEW_MANAGER_TYPE;
  }

  protected ReviewManager createReviewManager(InternalRepository repository, Element rmElement)
  {
    IManagedContainer container = repository.getContainer();
    String reviewManagerType = getDefaultReviewManagerType();
    return getContainerElement(rmElement, reviewManagerType, container);
  }

  private <T> T getContainerElement(Element element, String defaultType, IManagedContainer container)
  {
    String type = getAttribute(element, "type"); //$NON-NLS-1$
    if (StringUtil.isEmpty(type))
    {
      type = defaultType;
    }

    Tree config = Tree.XMLConverter.convertElementToTree(element);

    @SuppressWarnings("unchecked")
    T containerElement = (T)container.createElement(ReviewManager.PRODUCT_GROUP, type, config);

    return containerElement;
  }
}

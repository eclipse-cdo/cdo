/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.util;

import org.eclipse.emf.cdo.releng.doc.article.ArticleElement;

import com.sun.javadoc.SeeTag;

/**
 * @author Eike Stepper
 */
public class RefTag extends DelegatingTag
{
  private final ArticleElement target;

  public RefTag(SeeTag delegate, ArticleElement target)
  {
    super(delegate);
    this.target = target;
  }

  @Override
  public SeeTag getDelegate()
  {
    return (SeeTag)super.getDelegate();
  }

  public final ArticleElement getTarget()
  {
    return target;
  }

  @Override
  public String text()
  {
    return target.getTitle();
  }
}

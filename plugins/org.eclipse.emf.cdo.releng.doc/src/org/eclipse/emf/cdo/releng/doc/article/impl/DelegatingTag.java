/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

/**
 * @author Eike Stepper
 */
public class DelegatingTag implements Tag
{
  private final Tag delegate;

  public DelegatingTag(Tag delegate)
  {
    this.delegate = delegate;
  }

  public Tag getDelegate()
  {
    return delegate;
  }

  public String name()
  {
    return delegate.name();
  }

  public Doc holder()
  {
    return delegate.holder();
  }

  public String kind()
  {
    return delegate.kind();
  }

  public String text()
  {
    return delegate.text();
  }

  @Override
  public String toString()
  {
    return text();
  }

  public Tag[] inlineTags()
  {
    return delegate.inlineTags();
  }

  public Tag[] firstSentenceTags()
  {
    return delegate.firstSentenceTags();
  }

  public SourcePosition position()
  {
    return delegate.position();
  }
}

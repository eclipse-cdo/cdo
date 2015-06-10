/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import com.sun.javadoc.Tag;

/**
 * @author Eike Stepper
 */
public class TextTag extends DelegatingTag
{
  private String text;

  public TextTag(Tag delegate, String text)
  {
    super(delegate);
    this.text = text;
  }

  @Override
  public String kind()
  {
    return "Text";
  }

  @Override
  public String name()
  {
    return "Text";
  }

  @Override
  public String text()
  {
    return text;
  }
}

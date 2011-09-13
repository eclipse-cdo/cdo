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
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.Body;

import com.sun.javadoc.Tag;

/**
 * @author Eike Stepper
 */
public class UnresolvedBodyElementImpl extends BodyElementImpl
{
  private final String text;

  UnresolvedBodyElementImpl(Body body, Tag tag, String text)
  {
    super(body, tag);
    this.text = text;
  }

  public final String getText()
  {
    return text;
  }

  @Override
  public String getHtml()
  {
    return "<b><i>UNRESOLVED</i></b>";
  }
}

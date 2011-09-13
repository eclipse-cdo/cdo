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
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Context;

import org.eclipse.emf.common.util.EList;

import com.sun.javadoc.Tag;

/**
 * @author Eike Stepper
 */
public class UnresolvedBodyElement extends BodyElementImpl
{
  UnresolvedBodyElement(Body body, Tag tag)
  {
    super(body, tag);
  }

  public final String getText()
  {
    return "<b><i>UNRESOLVED</i></b>";
  }

  @Override
  public String getHtml()
  {
    return getText();
  }

  public BodyElement resolve(Context context)
  {
    return null;
  }

  public static void resolve(Context context, EList<BodyElement> elements)
  {
    for (int i = 0; i < elements.size(); i++)
    {
      BodyElement element = elements.get(i);
      if (element instanceof UnresolvedBodyElement)
      {
        UnresolvedBodyElement unresolved = (UnresolvedBodyElement)element;
        BodyElement resolved = unresolved.resolve(context);
        elements.set(i, resolved);
      }
    }
  }
}

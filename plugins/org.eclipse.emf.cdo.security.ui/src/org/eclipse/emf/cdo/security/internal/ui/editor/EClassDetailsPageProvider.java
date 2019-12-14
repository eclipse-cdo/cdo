/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import java.util.Map;

/**
 * A master/details-block details page provider that maps details pages
 * simply by {@link EClass}.
 *
 * @author Christian W. Damus (CEA LIST)
 *
 * @see #builder(IActionBars)
 */
public class EClassDetailsPageProvider implements IDetailsPageProvider
{
  private final Map<EClass, IDetailsPage> pages = new java.util.HashMap<>();

  private EClassDetailsPageProvider()
  {
  }

  public static Builder builder(IActionBars editorActionBars)
  {
    return new Builder(editorActionBars);
  }

  @Override
  public Object getPageKey(Object object)
  {
    return object instanceof EObject ? ((EObject)object).eClass() : null;
  }

  @Override
  public IDetailsPage getPage(Object key)
  {
    return pages.get(key);
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   */
  public static class Builder
  {
    private final Map<EClass, IDetailsPage> pages = new java.util.HashMap<>();

    private final IActionBars actionBars;

    private Builder(IActionBars actionBars)
    {
      this.actionBars = actionBars;
    }

    public Builder page(EClass eclass, IDetailsPage page)
    {
      if (page instanceof AbstractSectionPart<?>)
      {
        ((AbstractSectionPart<?>)page).setEditorActionBars(actionBars);
      }

      pages.put(eclass, page);
      return this;
    }

    public EClassDetailsPageProvider build()
    {
      EClassDetailsPageProvider result = new EClassDetailsPageProvider();
      result.pages.putAll(pages);
      return result;
    }
  }
}

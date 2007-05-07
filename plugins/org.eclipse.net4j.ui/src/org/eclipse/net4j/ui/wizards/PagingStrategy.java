/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class PagingStrategy implements IStepVisitor
{
  private List<SteppingWizardPage> pages = new ArrayList(0);

  private SteppingWizardPage currentPage;

  public PagingStrategy()
  {
  }

  public final List<SteppingWizardPage> getPages()
  {
    return pages;
  }

  public final void visit(Step step)
  {
    if (currentPage == null || !currentPage.isEmpty() && isNewPage(step))
    {
      currentPage = createNewPage();
      pages.add(currentPage);
    }

    currentPage.addWizardStep(step);
    step.setWizardPage(currentPage);
  }

  protected boolean isNewPage(Step step)
  {
    CompoundStep parent = step.getParent();
    if (parent instanceof SequentialStep)
    {
      return true;
    }

    return false;
  }

  protected SteppingWizardPage createNewPage()
  {
    return new SteppingWizardPage(pages.size());
  }
}

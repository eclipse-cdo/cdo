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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DecisionStep extends CompoundStep implements IDecisionProvider
{
  private int decisionIndex;

  private Map<Step, String> labels = new HashMap();

  public DecisionStep()
  {
  }

  public int getDecisionIndex()
  {
    return decisionIndex;
  }

  public Step getDecision()
  {
    return 0 <= decisionIndex && decisionIndex < size() ? get(decisionIndex) : null;
  }

  @Override
  public boolean isReady()
  {
    Step step = getDecision();
    return step == null ? false : step.isReady();
  }

  @Override
  public boolean isFinished()
  {
    Step step = getDecision();
    return step == null ? false : step.isFinished();
  }

  public Step addDecision(String label, Step step)
  {
    add(step);
    labels.put(step, label);
    return step;
  }

  void setDecisionIndex(int decisionIndex)
  {
    this.decisionIndex = decisionIndex;
  }

  @Override
  void setWizard(SteppingWizard wizard)
  {
    if (size() < 2)
    {
      throw new IllegalStateException("size() < 2");
    }

    super.setWizard(wizard);
  }
}

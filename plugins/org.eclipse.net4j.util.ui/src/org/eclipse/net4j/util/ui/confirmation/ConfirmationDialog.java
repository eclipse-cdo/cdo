/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.util.ui.confirmation;

import org.eclipse.net4j.util.confirmation.Confirmation;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 3.4
 */
public class ConfirmationDialog extends MessageDialog
{
  public ConfirmationDialog(Shell shell, String title, String message, Set<Confirmation> acceptableResponses, Confirmation suggestedResponse)
  {
    this(shell, title, message, getButtonLabels(inOrder(acceptableResponses)), inOrder(acceptableResponses).indexOf(suggestedResponse));
  }

  private ConfirmationDialog(Shell shell, String title, String message, String[] buttonLabels, int defaultIndex)
  {
    super(shell, title, null, message, MessageDialog.CONFIRM, buttonLabels, defaultIndex);
  }

  public static Confirmation openConfirm(Shell shell, String title, String message, Set<Confirmation> acceptableResponses, Confirmation suggestedResponse)
  {
    List<Confirmation> inOrder = inOrder(acceptableResponses);
    String[] buttonLabels = getButtonLabels(inOrder);
    int defaultIndex = inOrder.indexOf(suggestedResponse);

    ConfirmationDialog dialog = new ConfirmationDialog(shell, title, message, buttonLabels, defaultIndex);
    int index = dialog.open();
    return index == SWT.DEFAULT ? suggestedResponse : inOrder.get(index);
  }

  private static String[] getButtonLabels(List<Confirmation> acceptableResponses)
  {
    List<String> result = new ArrayList<>(acceptableResponses.size());

    for (Confirmation confirmation : acceptableResponses)
    {
      result.add(getLabel(confirmation));
    }

    return result.toArray(new String[result.size()]);
  }

  private static List<Confirmation> inOrder(Collection<Confirmation> confirmations)
  {
    List<Confirmation> result = new ArrayList<>(confirmations);
    Collections.sort(result);
    return result;
  }

  private static String getLabel(Confirmation confirmation)
  {
    switch (confirmation)
    {
    case OK:
      return IDialogConstants.OK_LABEL;
    case CANCEL:
      return IDialogConstants.CANCEL_LABEL;
    case YES:
      return IDialogConstants.YES_LABEL;
    case NO:
      return IDialogConstants.NO_LABEL;
    }

    throw new IllegalArgumentException(confirmation.name());
  }
}

/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.util.CoreOperations;
import org.eclipse.emf.cdo.ui.AbstractAuthorizingDialog;
import org.eclipse.emf.cdo.ui.Authorizer;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.collection.MapEntry;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DeleteBranchDialog extends AbstractAuthorizingDialog<CDOBranch>
{
  public static final String TITLE = "Delete Branch";

  private final CDOBranch rootBranch;

  private final CDOBranch viewerInput;

  private TreeViewer branchViewer;

  public DeleteBranchDialog(Shell parentShell, CDOBranch branch)
  {
    super(parentShell, CDOUtil.getSession(branch));
    rootBranch = branch;
    viewerInput = branch.getBase().getBranch();
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 300);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(TITLE);
    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_DELETE));
    setMessage("Confirm to delete the following branches permanently.");

    return super.createDialogArea(parent);
  }

  @Override
  protected void doCreateUI(Composite container)
  {
    CDOItemProvider itemProvider = new CDOItemProvider(null)
    {
      @Override
      protected boolean hasChildren(CDOBranch branch)
      {
        return branch == viewerInput ? true : super.hasChildren(branch);
      }

      @Override
      protected Object[] getChildren(CDOBranch branch)
      {
        return branch == viewerInput ? new Object[] { rootBranch } : super.getChildren(branch);
      }

      @Override
      public String getText(Object obj)
      {
        CDOBranch branch = (CDOBranch)obj;
        String name = branch.getName();

        if (authorizer.isAuthorizing())
        {
          String authorization = authorizer.getAuthorization(branch);
          if (authorization == null)
          {
            authorization = Authorizer.AUTHORIZATION_PENDING;
          }

          name += "  (" + authorization + ")";
        }

        return name;
      }
    };

    branchViewer = new TreeViewer(container, SWT.BORDER | SWT.SINGLE);
    branchViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    branchViewer.setLabelProvider(itemProvider);
    branchViewer.setContentProvider(itemProvider);
    branchViewer.setInput(viewerInput);

    UIUtil.asyncExec(() -> branchViewer.expandAll());
  }

  @Override
  public void updateUIAfterAuthorization()
  {
    branchViewer.refresh();
  }

  @Override
  public void collectElementOperations(List<Map.Entry<CDOBranch, AuthorizableOperation>> operations)
  {
    CDOBranchUtil.forEachBranchInTree(rootBranch, b -> operations.add(new MapEntry<>(b, CoreOperations.deleteBranch(b.getID()))));
  }
}

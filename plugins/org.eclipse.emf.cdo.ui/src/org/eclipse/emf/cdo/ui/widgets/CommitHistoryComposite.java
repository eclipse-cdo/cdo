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
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.internal.ui.history.NetRenderer;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.StructuredContentProvider;
import org.eclipse.net4j.util.ui.TableLabelProvider;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CommitHistoryComposite extends Composite
{
  private CDOCommitHistory history;

  private TableViewer tableViewer;

  private LabelProvider labelProvider;

  private NetRenderer netRenderer;

  private Input input;

  public CommitHistoryComposite(Composite parent, int style)
  {
    super(parent, style);

    setLayout(new FillLayout(SWT.HORIZONTAL));

    tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
    tableViewer.setContentProvider(new ContentProvider());
    tableViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
        CDOCommitInfo commitInfo = (CDOCommitInfo)selection.getFirstElement();
        if (commitInfo != null)
        {
          doubleClicked(commitInfo);
        }
      }
    });

    labelProvider = new LabelProvider();
    labelProvider.support(tableViewer);

    netRenderer = new NetRenderer(tableViewer);

    Table table = tableViewer.getTable();
    table.addListener(SWT.PaintItem, netRenderer);
    table.addListener(SWT.EraseItem, new Listener()
    {
      public void handleEvent(Event event)
      {
        event.detail &= ~SWT.FOREGROUND;
      }
    });
  }

  public final TableViewer getTableViewer()
  {
    return tableViewer;
  }

  public final Input getInput()
  {
    return input;
  }

  public final void setInput(Input input)
  {
    if (!ObjectUtil.equals(this.input, input))
    {
      this.input = input;

      CDOSession session = input.getSession();
      CDOBranch branch = input.getBranch();

      labelProvider.setLocalUserID(session.getUserID());
      labelProvider.setInputBranch(branch);

      setHistory(session, branch, input.getObject());
      netRenderer.setInput(input);
      tableViewer.setInput(history);
    }
  }

  public final CDOCommitHistory getHistory()
  {
    return history;
  }

  protected void setHistory(CDOSession session, CDOBranch branch, CDOObject object)
  {
    CDOCommitHistory oldHistory = history;

    if (object == null)
    {
      CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
      history = commitInfoManager.getHistory(branch);
    }
    else
    {
      history = object.cdoHistory();
    }

    if (oldHistory != null && oldHistory != history)
    {
      LifecycleUtil.deactivate(oldHistory);
    }
  }

  @Override
  public boolean setFocus()
  {
    return tableViewer.getTable().setFocus();
  }

  @Override
  public void dispose()
  {

    input = null;
    history = null;

    super.dispose();
  }

  protected void commitInfoChanged(CDOCommitInfo newCommitInfo)
  {
  }

  protected void doubleClicked(CDOCommitInfo commitInfo)
  {
  }

  /**
   * @author Eike Stepper
   */
  public static class Input
  {
    private final CDOSession session;

    private final CDOBranch branch;

    private final CDOObject object;

    public Input(Object delegate)
    {
      if (delegate instanceof CDOSession)
      {
        session = (CDOSession)delegate;
        branch = null;
        object = null;
        return;
      }

      if (delegate instanceof CDOView)
      {
        CDOView view = (CDOView)delegate;
        session = view.getSession();
        branch = view.getBranch();
        object = null;
        return;
      }

      if (delegate instanceof EObject)
      {
        EObject eObject = (EObject)delegate;
        CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
        if (cdoObject != null)
        {
          CDOView view = cdoObject.cdoView();
          if (view != null && cdoObject.cdoState() != CDOState.NEW)
          {
            session = view.getSession();
            branch = view.getBranch();
            object = cdoObject;
            return;
          }
        }
      }

      throw new IllegalStateException("Illegal input: " + delegate);
    }

    public Input(CDOSession session, CDOBranch branch, CDOObject object)
    {
      this.session = session;
      this.branch = branch;
      this.object = object;
    }

    public final CDOSession getSession()
    {
      return session;
    }

    public final CDOBranch getBranch()
    {
      return branch;
    }

    public final CDOObject getObject()
    {
      return object;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (session == null ? 0 : session.hashCode());
      result = prime * result + (branch == null ? 0 : branch.hashCode());
      result = prime * result + (object == null ? 0 : object.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (!(obj instanceof Input))
      {
        return false;
      }

      Input other = (Input)obj;
      if (session == null)
      {
        if (other.session != null)
        {
          return false;
        }
      }
      else if (!session.equals(other.session))
      {
        return false;
      }

      if (branch == null)
      {
        if (other.branch != null)
        {
          return false;
        }
      }
      else if (!branch.equals(other.branch))
      {
        return false;
      }

      return object == other.object;
    }

    @Override
    public String toString()
    {
      String str = "Repostory: " + session.getRepositoryInfo().getName();
      if (branch != null)
      {
        str += ", Branch: " + branch.getPathName();
      }

      if (object != null)
      {
        str += ", Object: " + object;
      }

      return str;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ContentProvider extends StructuredContentProvider<CDOCommitHistory>
  {
    public Object[] getElements(Object inputElement)
    {
      return ((CDOCommitHistory)inputElement).getElements();
    }

    @Override
    protected void connectInput(CDOCommitHistory history)
    {
      history.addListener(this);
    }

    @Override
    protected void disconnectInput(CDOCommitHistory history)
    {
      history.removeListener(this);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class LabelProvider extends TableLabelProvider<CDOCommitInfo>
  {
    private static final ImageDescriptor COMMIT = SharedIcons.getDescriptor(SharedIcons.OBJ_COMMIT);

    private static final ImageDescriptor PERSON = SharedIcons.getDescriptor(SharedIcons.OBJ_PERSON);

    private static final ImageDescriptor PERSON_ME = SharedIcons.getDescriptor(SharedIcons.OBJ_PERSON_ME);

    private static final ImageDescriptor BRANCH = SharedIcons.getDescriptor(SharedIcons.OBJ_BRANCH);

    private static final ImageDescriptor BRANCH_GRAY = SharedIcons.getDescriptor(SharedIcons.OBJ_BRANCH_GRAY);

    private String localUserID;

    private CDOBranch inputBranch;

    public LabelProvider()
    {
      addColumn(new Column<CDOCommitInfo>("Time", 160)
      {
        @Override
        public String getText(CDOCommitInfo commitInfo)
        {
          // return CDOCommonUtil.formatTimeStamp(commitInfo.getTimeStamp());
          return "" + commitInfo.getTimeStamp();
        }

        @Override
        public Image getImage(CDOCommitInfo commitInfo)
        {
          return (Image)getResource(COMMIT);
        }
      });

      addColumn(new Column<CDOCommitInfo>("Comment", 250)
      {
        @Override
        public String getText(CDOCommitInfo commitInfo)
        {
          return commitInfo.getComment();
        }
      });

      addColumn(new Column<CDOCommitInfo>("User", 120)
      {
        @Override
        public String getText(CDOCommitInfo commitInfo)
        {
          return commitInfo.getUserID();
        }

        @Override
        public Image getImage(CDOCommitInfo commitInfo)
        {
          String userID = commitInfo.getUserID();
          if (userID != null)
          {
            if (userID.equals(localUserID))
            {
              return (Image)getResource(PERSON_ME);
            }

            return (Image)getResource(PERSON);
          }

          return null;
        }
      });

      addColumn(new Column<CDOCommitInfo>("Branch", 160)
      {
        @Override
        public String getText(CDOCommitInfo commitInfo)
        {
          return commitInfo.getBranch().getPathName();
        }

        @Override
        public Image getImage(CDOCommitInfo commitInfo)
        {
          if (inputBranch == null || inputBranch == commitInfo.getBranch())
          {
            return (Image)getResource(BRANCH);
          }

          return (Image)getResource(BRANCH_GRAY);
        }
      });
    }

    public String getLocalUserID()
    {
      return localUserID;
    }

    public void setLocalUserID(String localUserID)
    {
      this.localUserID = localUserID;
    }

    public CDOBranch getInputBranch()
    {
      return inputBranch;
    }

    public void setInputBranch(CDOBranch inputBranch)
    {
      this.inputBranch = inputBranch;
    }
  }
}

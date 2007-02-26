package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOConstants;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.container.CDOContainerAdapter;
import org.eclipse.emf.cdo.internal.ui.bundle.CDOUI;
import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;

import org.eclipse.net4j.container.Container;
import org.eclipse.net4j.container.ContainerManager;
import org.eclipse.net4j.transport.ConnectorException;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CDOSessionsView extends StructuredView
{
  private static final Container CONTAINER = ContainerManager.INSTANCE.getContainer();

  private static final CDOContainerAdapter CDO_ADAPTER = (CDOContainerAdapter)CONTAINER.getAdapter(CDOConstants.TYPE);

  private static final ItemProvider ITEM_PROVIDER = new CDOSessionsItemProvider();

  private TreeViewer viewer;

  private OpenSessionAction openSessionAction = new OpenSessionAction();

  private OpenEditorAction openEditorAction = new OpenEditorAction();

  private OpenEditorAction openEditorReadOnlyAction = new OpenEditorReadOnlyAction();

  private OpenEditorAction openEditorHistoricalAction = new OpenEditorHistoricalAction();

  public CDOSessionsView()
  {
  }

  @Override
  protected void doCreatePartControl(Composite parent)
  {
    viewer = new TreeViewer(parent, (SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
    viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

    viewer.setContentProvider(ITEM_PROVIDER);
    viewer.setLabelProvider(ITEM_PROVIDER);
    viewer.setSorter(new NameSorter());
    viewer.setInput(CDO_ADAPTER);

    getSite().setSelectionProvider(viewer);
  }

  @Override
  protected StructuredViewer getCurrentViewer()
  {
    return viewer;
  }

  @Override
  protected void fillContextMenu(IMenuManager manager)
  {
    IStructuredSelection selection = (IStructuredSelection)getCurrentViewer().getSelection();
    if (selection.size() == 1)
    {
      Object element = selection.getFirstElement();
      if (element instanceof CDOSession)
      {
        openEditorAction.setSession((CDOSession)element);
        addContribution(manager, openEditorAction);

        openEditorReadOnlyAction.setSession((CDOSession)element);
        addContribution(manager, openEditorReadOnlyAction);

        openEditorHistoricalAction.setSession((CDOSession)element);
        addContribution(manager, openEditorHistoricalAction);
      }
    }

    super.fillContextMenu(manager);
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    addContribution(manager, openSessionAction);
    super.fillLocalPullDown(manager);
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    addContribution(manager, openSessionAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void onDoubleClick(Object selectedElement)
  {
    if (selectedElement instanceof CDOSession && !viewer.isExpandable(selectedElement))
    {
      openEditorAction.setSession((CDOSession)selectedElement);
      openEditorAction.run();
    }
    else
    {
      super.onDoubleClick(selectedElement);
    }
  }

  private static final class NameSorter extends ViewerSorter
  {
  }

  /**
   * @author Eike Stepper
   */
  private final class OpenSessionAction extends Action
  {
    public OpenSessionAction()
    {
      setText("Open Session");
      setToolTipText("Open a CDO session");
      setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_SESSION));
    }

    public void run()
    {
      String description = showInputDialog("Enter a session description");
      if (description != null)
      {
        try
        {
          CDO_ADAPTER.getSession(description);
        }
        catch (ConnectorException ex)
        {
          CDOUI.LOG.error(ex);
          showMessage("Error while creating session for description " + description);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private class OpenEditorAction extends Action
  {
    private CDOSession session;

    public OpenEditorAction()
    {
      this("Open Editor", "Open a CDO editor", SharedIcons.ETOOL_OPEN_EDITOR);
    }

    protected OpenEditorAction(String text, String tooltip, String iconKey)
    {
      setText(text);
      setToolTipText(tooltip);
      setImageDescriptor(SharedIcons.getDescriptor(iconKey));
    }

    public CDOSession getSession()
    {
      return session;
    }

    public void setSession(CDOSession session)
    {
      this.session = session;
      setEnabled(session != null);
    }

    public void run()
    {
      try
      {
        openEditor(session);
      }
      catch (Exception ex)
      {
        CDOUI.LOG.error(ex);
        showMessage("Error while opening editor on session " + session);
      }
    }

    protected void openEditor(CDOSession session)
    {
      session.attach(new ResourceSetImpl());
    }
  }

  /**
   * @author Eike Stepper
   */
  private class OpenEditorReadOnlyAction extends OpenEditorAction
  {
    public OpenEditorReadOnlyAction()
    {
      super("Open Editor in Read Only Mode", "Open a CDO editor in read only mode", null);
    }

    @Override
    protected void openEditor(CDOSession session)
    {
      session.attach(new ResourceSetImpl(), true);
    }
  }

  /**
   * @author Eike Stepper
   */
  private class OpenEditorHistoricalAction extends OpenEditorAction
  {
    public OpenEditorHistoricalAction()
    {
      super("Open Editor in Historical Mode", "Open a CDO editor in historical mode", null);
    }

    @Override
    protected void openEditor(CDOSession session)
    {
      DateFormat formatter = SimpleDateFormat.getInstance();
      String value = formatter.format(new Date());
      String str = showInputDialog("Enter a date", value);
      if (str != null)
      {
        try
        {
          Date date = formatter.parse(str);
          session.attach(new ResourceSetImpl(), date.getTime());
        }
        catch (ParseException ex)
        {
          CDOUI.LOG.error(ex);
        }
      }
    }
  }
}
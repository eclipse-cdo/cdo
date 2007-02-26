package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOConstants;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.container.CDOContainerAdapter;
import org.eclipse.emf.cdo.internal.ui.bundle.CDOUI;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CDOSessionsView extends StructuredView
{
  private static final Container CONTAINER = ContainerManager.INSTANCE.getContainer();

  private static final CDOContainerAdapter CDO_ADAPTER = (CDOContainerAdapter)CONTAINER.getAdapter(CDOConstants.TYPE);

  private static final ItemProvider ITEM_PROVIDER = new CDOSessionsItemProvider();

  private TreeViewer viewer;

  private OpenSessionAction openSessionAction = new OpenSessionAction();

  private AttachAdapterAction attachAdapterAction = new AttachAdapterAction();

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
    viewer.setSorter(new CDOSessionsNameSorter());
    viewer.setInput(CDO_ADAPTER);
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
        attachAdapterAction.setSession((CDOSession)element);
        addContribution(manager, attachAdapterAction);
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
      attachAdapterAction.setSession((CDOSession)selectedElement);
      attachAdapterAction.run();
    }
    else
    {
      super.onDoubleClick(selectedElement);
    }
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
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
          ISharedImages.IMG_TOOL_NEW_WIZARD));
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
  private final class AttachAdapterAction extends Action
  {
    private CDOSession session;

    public AttachAdapterAction()
    {
      setText("Attach Adapter");
      setToolTipText("Attach a CDO adapter");
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
          ISharedImages.IMG_TOOL_NEW_WIZARD));
    }

    public AttachAdapterAction(boolean readOnly)
    {
      this();
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
        session.attach(new ResourceSetImpl());
      }
      catch (Exception ex)
      {
        CDOUI.LOG.error(ex);
        showMessage("Error while attaching adapter to session " + session);
      }
    }
  }
}
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IPluginTransportContainer;
import org.eclipse.net4j.ITransportContainer;
import org.eclipse.net4j.ui.actions.SafeAction;
import org.eclipse.net4j.ui.views.ContainerItemProvider;
import org.eclipse.net4j.ui.views.ContainerView;
import org.eclipse.net4j.ui.views.IElementFilter;

import org.eclipse.emf.internal.cdo.CDOSessionFactory;

import org.eclipse.jface.action.IToolBarManager;

public class CDOSessionsView extends ContainerView
{
  private OpenSessionAction openSessionAction = new OpenSessionAction();

  public CDOSessionsView()
  {
  }

  @Override
  protected ITransportContainer getContainer()
  {
    return IPluginTransportContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider createContainerItemProvider()
  {
    return new CDOItemProvider(getSite().getPage(), new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(openSessionAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void doubleClicked(Object object)
  {
    // if (object instanceof CDOAdapter)
    // {
    // openEditor((CDOAdapter)object);
    // }
    // else
    {
      super.doubleClicked(object);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class OpenSessionAction extends SafeAction
  {
    private OpenSessionAction()
    {
      super("Open Session", "Open a CDO session", getAddImageDescriptor());
    }

    @Override
    protected void doRun() throws Exception
    {
      IPluginTransportContainer.INSTANCE.getElement(CDOSessionFactory.SESSION_GROUP,
          CDOProtocolConstants.PROTOCOL_NAME, "tcp://127.0.0.1:2036/repo1");
    }
  }
}
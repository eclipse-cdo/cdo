package org.eclipse.emf.cdo.internal.ui.actions;

/**
 * @author Eike Stepper
 */
@Deprecated
public class ExportResourceAction extends EditingDomainAction
{
  public static final String ID = "export-resource";

  private static final String TITLE = "Export Resource";

  public ExportResourceAction()
  {
    super(TITLE + INTERACTIVE);
    setId(ID);
  }

  @Override
  protected void doRun() throws Exception
  {
  }
}
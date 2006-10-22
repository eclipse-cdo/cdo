package org.eclipse.net4j.util.registry;

/**
 * @author Eike Stepper
 */
public interface IRegistryElement<ID>
{
  public ID getID();

  public void dispose();

  /**
   * @author Eike Stepper
   */
  public interface Descriptor<ID> extends IRegistryElement<ID>
  {
    public IRegistryElement<ID> resolve();
  }
}
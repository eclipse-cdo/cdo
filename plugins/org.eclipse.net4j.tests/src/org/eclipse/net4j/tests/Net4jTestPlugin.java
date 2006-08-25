/**
 * <copyright>
 *
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: Net4jTestPlugin.java,v 1.2 2006-08-25 09:38:15 estepper Exp $
 */
package org.eclipse.net4j.tests;

import org.eclipse.core.runtime.Plugin;

public class Net4jTestPlugin 
extends Plugin
{
    private static Net4jTestPlugin instance;
    
    public Net4jTestPlugin()
    {
        super();
        instance = this;
    }

    public static Net4jTestPlugin getPlugin()
    {
        return instance;
    }
}

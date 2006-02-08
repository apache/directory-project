package org.apache.ldap.common.util;

import java.util.List;

/**
 * A monitoring set. Can be implemented to provide control for component order,
 * component existance, component duplication, etc.
 * 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public interface ComponentsMonitor
{
    public ComponentsMonitor useComponent( String component ) throws IllegalArgumentException;
    public boolean allComponentsUsed();
    public boolean finalStateValid();
    public List getRemainingComponents();
}

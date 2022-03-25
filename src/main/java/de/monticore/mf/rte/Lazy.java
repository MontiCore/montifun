/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.rte;

import de.monticore.mf.rte.functions.Function0;

/**
 * A Supplier interface for lazily calculated values
 * This Supplier may be used like a callable as well
 *
 * @param <T> the type to be calculated
 */
public interface Lazy<T> extends Function0<T> {
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.rte;

/**
 * A Supplier interface for lazily calculated values
 * This Supplier may be used like a callable as well
 *
 * @param <T> the type to be calculated
 */
//todo most likely remove later but check with lists
public interface Lazy<T> extends Function.A0<T> {
}

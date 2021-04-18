/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.portlek.scoreboard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class that represents boards.
 *
 * @param <O> type of the observers.
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Board<O> {

  /**
   * the dynamic observers.
   */
  @NotNull
  private final Set<Supplier<O>> dynamicObservers;

  /**
   * the filters.
   */
  @NotNull
  private final Set<Predicate<O>> filters;

  /**
   * the id.
   */
  @Nullable
  private final String id;

  /**
   * the observer class.
   */
  @NotNull
  private final Class<O> observerClass;

  /**
   * the static observers.
   */
  @NotNull
  private final Set<O> staticObservers;

  /**
   * creates a new instance of {@link Builder}.
   *
   * @param observerClass the observer class to create.
   * @param <O> type of the observers.
   *
   * @return a newly created instance of {@link Builder}.
   */
  @NotNull
  public static <O> Builder<O> builder(@NotNull final Class<O> observerClass) {
    return new Builder<>(observerClass);
  }

  /**
   * a class that represents builders for {@link Board}.
   *
   * @param <O> type of the observers.
   */
  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Builder<O> {

    /**
     * the observer class.
     */
    @NotNull
    private final Class<O> observerClass;

    /**
     * the dynamic observers.
     */
    @NotNull
    private Set<Supplier<O>> dynamicObservers = new HashSet<>();

    /**
     * the filters.
     */
    @NotNull
    private Set<Predicate<O>> filters = new HashSet<>();

    /**
     * the id.
     */
    @Nullable
    private String id;

    /**
     * the static observers.
     */
    @NotNull
    private Set<O> staticObservers = new HashSet<>();

    /**
     * adds the given dynamic observers to the {@link #dynamicObservers}.
     *
     * @param observers the observers to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addDynamicObservers(@NotNull final Supplier<O>... observers) {
      Collections.addAll(this.dynamicObservers, observers);
      return this;
    }

    /**
     * adds the given filters to the {@link #filters}.
     *
     * @param filters the filters to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addFilters(@NotNull final Predicate<O>... filters) {
      Collections.addAll(this.filters, filters);
      return this;
    }

    /**
     * adds the given static observers to the {@link #staticObservers}.
     *
     * @param observers the observers to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addStaticObservers(@NotNull final O... observers) {
      Collections.addAll(this.staticObservers, observers);
      return this;
    }

    /**
     * builds a new board instance from the builder's value.
     *
     * @return a newly created instance of {@link Board}.
     */
    @NotNull
    public Board<O> build() {
      return new Board<>(this.dynamicObservers, this.filters, this.id, this.observerClass, this.staticObservers);
    }

    /**
     * sets the dynamic observers.
     *
     * @param dynamicObservers the dynamic observers to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setDynamicObservers(@NotNull final Set<Supplier<O>> dynamicObservers) {
      this.dynamicObservers = dynamicObservers;
      return this;
    }

    /**
     * sets the filters.
     *
     * @param filters the filters to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setFilters(@NotNull final Set<Predicate<O>> filters) {
      this.filters = filters;
      return this;
    }

    /**
     * sets the id.
     *
     * @param id the id to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setId(final String id) {
      this.id = id;
      return this;
    }

    /**
     * sets the static observers.
     *
     * @param staticObservers the static observers to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setStaticObservers(@NotNull final Set<O> staticObservers) {
      this.staticObservers = staticObservers;
      return this;
    }
  }
}

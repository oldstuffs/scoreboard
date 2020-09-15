/*
 * MIT License
 *
 * Copyright (c) 2020 Hasan Demirtaş
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class Board<O, P extends Plugin, B extends Board<O, P, B>> implements Self<B> {

    private final Collection<Observer<O>> activeObserver = new ArrayList<>();

    private final Collection<Predicate<Observer<O>>> filters = new ArrayList<>();

    @NotNull
    private final P plugin;

    @NotNull
    private Collection<Observer<O>> observers = new ArrayList<>();

    @NotNull
    private Predicate<Observer<O>> removeIf = observer -> false;

    @NotNull
    private Consumer<Observer<O>> runBefore = observer -> {
    };

    @NotNull
    private Consumer<Observer<O>> runAfter = observer -> {
    };

    @Nullable
    private LineBuilder<O, P, B> lines;

    private long startDelay = 0L;

    private long tick = 20L;

    @SafeVarargs
    @NotNull
    public final B addObserver(@NotNull final O... observers) {
        this.observers.addAll(Arrays.asList(observers).stream().map(this::createObserver).collect(Collectors.toList()));
        return this.self();
    }

    @NotNull
    public final B addObserver(@NotNull final List<O> observers) {
        this.observers.addAll(observers.stream().map(this::createObserver).collect(Collectors.toList()));
        return this.self();
    }

    @NotNull
    public final B setObserver(@NotNull final Collection<O> observers) {
        this.observers = observers.stream().map(this::createObserver).collect(Collectors.toList());
        return this.self();
    }

    @NotNull
    public final B filter(@NotNull final Predicate<Observer<O>> filter) {
        this.filters.add(filter);
        return this.self();
    }

    @NotNull
    public final B removeIf(@NotNull final Predicate<Observer<O>> removeIf) {
        this.removeIf = removeIf;
        return this.self();
    }

    @NotNull
    public final B runBefore(@NotNull final Consumer<Observer<O>> runBefore) {
        this.runBefore = runBefore;
        return this.self();
    }

    @NotNull
    public final B runAfter(@NotNull final Consumer<Observer<O>> runAfter) {
        this.runAfter = runAfter;
        return this.self();
    }

    @NotNull
    public final B startDelay(final long startDelay) {
        this.startDelay = startDelay;
        return this.self();
    }

    @NotNull
    public final B tick(final long tick) {
        this.tick = tick;
        return this.self();
    }

    @NotNull
    public final LineBuilder<O, P, B> newLineBuilder() {
        return this.lines = new LineBuilder<>(this.self());
    }

    public final void start() {
    }

    public final void sendOnce() {
    }

    public abstract Observer<O> createObserver(@NotNull O observer);

}

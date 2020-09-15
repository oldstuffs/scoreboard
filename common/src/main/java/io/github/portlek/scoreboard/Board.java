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
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class Board<O, P extends Plugin, B extends Board<O, P, B>> implements Self<B> {

    private final Collection<Observer<O>> activeObserver = new ArrayList<>();

    private final Collection<Observer<O>> observers = new ArrayList<>();

    private final Collection<Predicate<Observer<O>>> filters = new ArrayList<>();

    @NotNull
    private final P plugin;

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

    @NotNull
    public final Board<O, P, B> addObserver(@NotNull final Observer<O> observer) {
        this.observers.add(observer);
        return this.self();
    }

    @NotNull
    public final Board<O, P, B> filter(@NotNull final Predicate<Observer<O>> filter) {
        this.filters.add(filter);
        return this.self();
    }

    @NotNull
    public final Board<O, P, B> removeIf(@NotNull final Predicate<Observer<O>> removeIf) {
        this.removeIf = removeIf;
        return this.self();
    }

    @NotNull
    public final Board<O, P, B> runBefore(@NotNull final Consumer<Observer<O>> runBefore) {
        this.runBefore = runBefore;
        return this.self();
    }

    @NotNull
    public final Board<O, P, B> runAfter(@NotNull final Consumer<Observer<O>> runAfter) {
        this.runAfter = runAfter;
        return this.self();
    }

    @NotNull
    public final LineBuilder<O, P, B> newLineBuilder() {
        return this.lines = new LineBuilder<>(this.self());
    }

}

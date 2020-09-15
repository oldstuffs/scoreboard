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

import io.github.portlek.scoreboard.line.DynamicLine;
import io.github.portlek.scoreboard.line.ObserverLine;
import io.github.portlek.scoreboard.line.StaticLine;
import io.github.portlek.scoreboard.util.Utilities;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class LineBuilder<O, P extends Plugin, B extends Board<O, P, B>> {

    private final Map<Integer, StaticLine> staticLines = new HashMap<>();

    private final Map<Integer, DynamicLine> dynamicLines = new HashMap<>();

    private final Map<Integer, ObserverLine<O>> observerLines = new HashMap<>();

    @NotNull
    private final B board;

    @NotNull
    public LineBuilder<O, P, B> staticLine(final int line, @NotNull final String staticLine) {
        this.staticLines.put(line, Line.staticLine(staticLine));
        return this;
    }

    @NotNull
    public LineBuilder<O, P, B> dynamicLine(final int line, @NotNull final Supplier<String> dynamicLine) {
        this.dynamicLines.put(line, Line.dynamicLine(dynamicLine));
        return this;
    }

    @NotNull
    public LineBuilder<O, P, B> observerLine(final int line, @NotNull final Function<Observer<O>, String> observerLine) {
        this.observerLines.put(line, Line.observerLine(observerLine));
        return this;
    }

    @NotNull
    public LineBuilder<O, P, B> addStaticLine(@NotNull final String staticLine) {
        this.staticLines.put(Utilities.maximum(this.staticLines.keySet()) + 1, Line.staticLine(staticLine));
        return this;
    }

    @NotNull
    public LineBuilder<O, P, B> addDynamicLine(@NotNull final Supplier<String> dynamicLine) {
        this.dynamicLines.put(Utilities.maximum(this.dynamicLines.keySet()) + 1, Line.dynamicLine(dynamicLine));
        return this;
    }

    @NotNull
    public LineBuilder<O, P, B> addObserverLine(@NotNull final Function<Observer<O>, String> observerLine) {
        this.observerLines.put(Utilities.maximum(this.observerLines.keySet()) + 1, Line.observerLine(observerLine));
        return this;
    }

    @NotNull
    public B back() {
        return this.board;
    }

}

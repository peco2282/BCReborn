/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.builder.patterns;

import com.peco2282.bcreborn.api.filler.IFillerPattern;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class FillerRegistry {
    private TreeMap<String, IFillerPattern> patterns = new TreeMap<>();

    public void addPattern(IFillerPattern pattern) {
        patterns.put(pattern.getUniqueTag(), pattern);
    }

    public IFillerPattern getPattern(String patternName) {
        return patterns.get(patternName);
    }

    public IFillerPattern getNextPattern(IFillerPattern currentPattern) {
        Map.Entry<String, IFillerPattern> pattern = patterns.higherEntry(currentPattern.getUniqueTag());
        if (pattern == null) {
            pattern = patterns.firstEntry();
        }
        return pattern.getValue();
    }

    public IFillerPattern getPreviousPattern(IFillerPattern currentPattern) {
        Map.Entry<String, IFillerPattern> pattern = patterns.lowerEntry(currentPattern.getUniqueTag());
        if (pattern == null) {
            pattern = patterns.lastEntry();
        }
        return pattern.getValue();
    }

    public Collection<IFillerPattern> getPatterns() {
        return Collections.unmodifiableCollection(patterns.values());
    }
}

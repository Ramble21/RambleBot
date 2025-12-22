package com.github.Ramble21.classes.geometrydash.savefile;

import java.util.HashSet;

public record CCParseProgress(int completedCount, HashSet<String> processedLevels, boolean isComplete) {
}

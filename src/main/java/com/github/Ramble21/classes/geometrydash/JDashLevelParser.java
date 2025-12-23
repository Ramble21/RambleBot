package com.github.Ramble21.classes.geometrydash;

import jdash.client.GDClient;
import jdash.client.exception.GDClientException;
import jdash.common.DemonDifficulty;
import jdash.common.LevelSearchFilter;
import jdash.common.LevelSearchMode;
import jdash.common.entity.GDLevel;

public class JDashLevelParser {

    private static final GDClient client = GDClient.create();

    public static jdash.common.entity.GDLevel fetchAPIResponse(long levelId) {
        int maxRetries = 7;
        int retryDelayMs = 800;
        sleep(1200);

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return client.findLevelById(levelId).block();
            } catch (GDClientException e) {
                if (attempt < maxRetries - 1) {
                    System.out.println("Error fetching level " + levelId + ". Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    sleep(retryDelayMs);
                    retryDelayMs *= 2;
                } else {
                    System.out.println("Max retries exceeded for Level " + levelId);
                    System.out.println("Error: " + e.getMessage());
                    if (e.getCause() != null) {
                        System.out.println("Cause: " + e.getCause().getMessage());
                    }
                }
            }
        }
        return null;
    }

    public static GDLevel fetchAPIResponseByName(String name, DemonDifficulty difficulty) {
        int maxRetries = 7;
        int retryDelayMs = 800;
        sleep(1200);

        LevelSearchFilter filter = LevelSearchFilter.create().withDemonFilter(difficulty);

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return client.searchLevels(LevelSearchMode.SEARCH, name, filter, 0)
                        .next()
                        .block();
            }
            catch (GDClientException e) {
                if (attempt < maxRetries - 1) {
                    System.out.println("Error searching for level '" + name + "'. Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    sleep(retryDelayMs);
                    retryDelayMs *= 2;
                } else {
                    System.out.println("Max retries exceeded for Level " + name);
                    System.out.println("Error: " + e.getMessage());
                }
            }
            catch (Exception e) {
                System.out.println("Unexpected error searching for level '" + name + "': " + e.getMessage());
                if (attempt < maxRetries - 1) {
                    sleep(retryDelayMs);
                    retryDelayMs *= 2;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted, continuing with retry logic");
        }
    }

}

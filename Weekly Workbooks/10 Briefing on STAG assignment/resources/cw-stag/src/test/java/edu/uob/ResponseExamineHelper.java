package edu.uob;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseExamineHelper {
    public static void testResponseContains(String response, String[] keywords) {
        response = response.toLowerCase();
        for (String keyword : keywords) {
            assertTrue(response.contains(keyword.toLowerCase()));
        }
    }

    public static void testResponseNotContains(String response, String[] keywords) {
        response = response.toLowerCase();
        for (String keyword : keywords) {
            assertFalse(response.contains(keyword.toLowerCase()));
        }
    }
}

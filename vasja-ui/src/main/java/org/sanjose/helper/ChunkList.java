package org.sanjose.helper;

import java.util.ArrayList;
import java.util.List;

public class ChunkList {

    public <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(list.size(), i + chunkSize);
            chunks.add(list.subList(i, end));
        }
        return chunks;
    }
}
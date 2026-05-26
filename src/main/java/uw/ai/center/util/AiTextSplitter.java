package uw.ai.center.util;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 简单文本分割器（替代 Spring AI TokenTextSplitter）。
 * 按字符数分割文本，优先在分隔符处断开。
 */
public class AiTextSplitter {

    private final int chunkSize;
    private final int chunkMinCharSize;
    private final int chunkMinEmbedSize;
    private final int chunkMaxNum;
    private final boolean keepSeparator;
    private final List<Character> separators;

    public AiTextSplitter(int chunkSize, int chunkMinCharSize, int chunkMinEmbedSize,
                          int chunkMaxNum, boolean keepSeparator, List<Character> separators) {
        this.chunkSize = chunkSize;
        this.chunkMinCharSize = chunkMinCharSize;
        this.chunkMinEmbedSize = chunkMinEmbedSize;
        this.chunkMaxNum = chunkMaxNum;
        this.keepSeparator = keepSeparator;
        this.separators = separators;
    }

    /**
     * 将文本分割为 TextSegment 列表。
     */
    public List<TextSegment> split(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        List<String> chunks = splitText(text);
        List<TextSegment> segments = new ArrayList<>(Math.min(chunks.size(), chunkMaxNum));
        int count = 0;
        for (String chunk : chunks) {
            if (count >= chunkMaxNum) {
                break;
            }
            String trimmed = chunk.trim();
            if (trimmed.length() < chunkMinCharSize) {
                continue;
            }
            if (trimmed.length() < chunkMinEmbedSize) {
                continue;
            }
            Metadata metadata = new Metadata();
            metadata.put("id", UUID.randomUUID().toString());
            segments.add(TextSegment.from(trimmed, metadata));
            count++;
        }
        return segments;
    }

    private List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            if (end < text.length()) {
                // 在 chunkSize 范围内找最佳分隔点
                int bestBreak = -1;
                for (int i = end; i > start; i--) {
                    char c = text.charAt(i - 1);
                    if (separators.contains(c)) {
                        bestBreak = i;
                        break;
                    }
                }
                if (bestBreak > start) {
                    end = bestBreak;
                }
            }
            String chunk = text.substring(start, end);
            // 如果保持分隔符，将分隔符附加到当前 chunk 末尾
            if (keepSeparator && end < text.length()) {
                chunk = chunk + text.charAt(end);
                end++;
            }
            chunks.add(chunk);
            start = end;
            // 跳过连续的分隔符
            while (start < text.length() && separators.contains(text.charAt(start))) {
                start++;
            }
        }
        return chunks;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getChunkMinCharSize() {
        return chunkMinCharSize;
    }

    public int getChunkMinEmbedSize() {
        return chunkMinEmbedSize;
    }

    public int getChunkMaxNum() {
        return chunkMaxNum;
    }

    public boolean isKeepSeparator() {
        return keepSeparator;
    }

    public List<Character> getSeparators() {
        return separators;
    }
}

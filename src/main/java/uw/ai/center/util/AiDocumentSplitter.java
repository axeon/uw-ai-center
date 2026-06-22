package uw.ai.center.util;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RAG文档分割器.
 * 封装LangChain4j原生递归分割器，并自动为每个segment添加UUID metadata，
 * 以兼容现有的docContent存储（Map&lt;UUID, chunkText&gt;）和deleteDocument()按ID删除机制。
 * 段落(\n\n) → 行(\n) → 句子 → 词 → 字符，超长段自动降级到子分割器。</p>
 */
public class AiDocumentSplitter implements DocumentSplitter {

    /**
     * LangChain4j原生递归分割器实例.
     */
    private final DocumentSplitter delegate;

    /**
     * chunk最大数量限制（原生分割器无此参数，需手动限制）.
     */
    private final int chunkMaxNum;

    /**
     * 构造RAG文档分割器.
     *
     * @param chunkSize 每个chunk的目标字符数
     * @param overlapSize 相邻chunk之间的重叠字符数
     * @param chunkMaxNum chunk最大数量限制
     */
    public AiDocumentSplitter(int chunkSize, int overlapSize, int chunkMaxNum) {
        // 使用LangChain4j原生递归分割器：
        // 递归层级：段落(\n\n) → 行(\n) → 句子 → 词 → 字符
        // 超长段自动降级到子分割器，小段自动合并到相邻chunk
        this.delegate = DocumentSplitters.recursive(chunkSize, overlapSize);
        this.chunkMaxNum = chunkMaxNum;
    }

    /**
     * 分割Document为TextSegment列表.
     * 调用原生分割器分割后，为每个segment补充UUID metadata，并限制最大数量。
     *
     * @param document LangChain4j文档对象
     * @return 带UUID metadata的TextSegment列表
     */
    @Override
    public List<TextSegment> split(Document document) {
        // 调用原生递归分割器进行分割
        List<TextSegment> rawSegments = delegate.split(document);
        List<TextSegment> segments = new ArrayList<>(rawSegments.size());
        for (TextSegment raw : rawSegments) {
            // 复制原有metadata（含index等信息），再添加UUID作为唯一标识
            // UUID用于：1.存入AiRagDoc.docContent的Map key  2.deleteDocument()时按ID删除ES chunk
            Metadata metadata = raw.metadata().copy();
            metadata.put("id", UUID.randomUUID().toString());
            segments.add(TextSegment.from(raw.text(), metadata));
        }
        // 限制最大chunk数量，防止超大文档产生过多segment
        // 注：subList 返回的是原 List 的视图，序列化或跨层传递时可能出问题，这里拷贝成独立 ArrayList
        if (segments.size() > chunkMaxNum) {
            segments = new ArrayList<>(segments.subList(0, chunkMaxNum));
        }
        return segments;
    }

    /**
     * 分割纯文本为TextSegment列表（便捷方法）.
     * 将文本包装为Document后再调用 {@link #split(Document)}。
     *
     * @param text 纯文本内容
     * @return 带UUID metadata的TextSegment列表
     */
    public List<TextSegment> split(String text) {
        return split(Document.from(text));
    }
}
